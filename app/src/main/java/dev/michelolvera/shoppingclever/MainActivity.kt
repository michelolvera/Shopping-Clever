package dev.michelolvera.shoppingclever

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        setTheme(R.style.AppThemeNoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        addActivityListeners()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(user: FirebaseUser?){
        user?.let{
            Toast.makeText(this, "El usuario Existe y es: ${it.displayName}", Toast.LENGTH_SHORT).show()
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

    private fun addActivityListeners (){
        buttonLogin.setOnClickListener {
            signIn(editTextTextEmailAddress.text.toString().trim(), editTextTextPassword.text.toString().trim())
        }
    }
}