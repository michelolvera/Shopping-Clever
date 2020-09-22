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
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        setTheme(R.style.AppThemeNoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Facebook Login button
        callbackManager = CallbackManager.Factory.create()
        fbLoginButton.setPermissions("email", "public_profile")

        addActivityListeners()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun updateUI(user: FirebaseUser?){
        user?.let{
            Toast.makeText(this, "El usuario Existe y es: ${it.email}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful){
                    Toast.makeText(this, "Usuario Creado", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, "Error al crear Usuario ${task.exception}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful){
                    Toast.makeText(this, "Sesion Iniciada", Toast.LENGTH_SHORT).show()
                }else{
                    try {
                        throw task.exception!!
                    }catch (e: FirebaseAuthInvalidUserException){
                        createAccount(email, password)
                    }catch (e: Exception){
                        Toast.makeText(this, "Error al Iniciar Sesion: ${task.exception}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun handleFacebookAccessToken(token: AccessToken){
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                updateUI(auth.currentUser)
            } else {
                Toast.makeText(this, "Error al crear Usuario de FB ${task.exception}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addActivityListeners (){
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
    }
}