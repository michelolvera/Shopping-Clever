package dev.michelolvera.shoppingclever

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppThemeNoActionBar)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth

        // Initialize Facebook Login button
        callbackManager = CallbackManager.Factory.create()
        fbLoginButton.setPermissions("email", "public_profile")

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        addActivityListeners()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Error al obtener credenciales de Usuario de Google: $e", Toast.LENGTH_SHORT).show()
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }

    }

    private fun updateUI(user: FirebaseUser?) {
        user?.let {
            Toast.makeText(this, "El usuario Existe y es: ${if (user.isAnonymous) "anonimo" else user.email}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addActivityListeners() {
        buttonLogin.setOnClickListener {
            signIn(editTextTextEmailAddress.text.toString().trim(), editTextTextPassword.text.toString().trim())
        }

        fbLoginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Toast.makeText(this@MainActivity, "Facebook Login Canceled", Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: FacebookException) {
                Toast.makeText(this@MainActivity, "Facebook Login Error", Toast.LENGTH_SHORT).show()
            }
        })

        google_button.setOnClickListener {
            googleSignIn()
        }

        textViewNoLogin.setOnClickListener {
            anonymousSignIn()
        }
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Usuario Creado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Error al crear Usuario ${task.exception}", Toast.LENGTH_SHORT).show()
                    }
                }
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Sesion Iniciada", Toast.LENGTH_SHORT).show()
                    } else {
                        try {
                            throw task.exception!!
                        } catch (e: FirebaseAuthInvalidUserException) {
                            createAccount(email, password)
                        } catch (e: Exception) {
                            Toast.makeText(this, "Error al Iniciar Sesion: ${task.exception}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
    }

    private fun anonymousSignIn() {
        auth.signInAnonymously().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                updateUI(auth.currentUser)
            } else {
                Toast.makeText(this, "Error al crear Usuario Anonimo ${task.exception}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun googleSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                updateUI(auth.currentUser)
            } else {
                Toast.makeText(this, "Error al crear Usuario de Google ${task.exception}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                updateUI(auth.currentUser)
            } else {
                Toast.makeText(this, "Error al crear Usuario de FB ${task.exception}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val GOOGLE_SIGN_IN = 9001
    }
}