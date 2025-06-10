package com.example.messageapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.messageapp.databinding.ActivityLoginBinding
import com.example.messageapp.utils.showMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private lateinit var email : String
    private lateinit var password : String

    override fun onStart() {
        super.onStart()
        verifyUserLogged()
    }

    private fun verifyUserLogged() {
        val currentUser = auth.currentUser
        if(currentUser != null) {
            startActivity(
                Intent(this, MainActivity::class.java)
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initializeOnClickFunctions()
    }

    private fun initializeOnClickFunctions() {
        binding.btnLogin.setOnClickListener {
            if(validateData()){
                userLogin()
            }
        }

        binding.txtSignUp.setOnClickListener {
            startActivity(
                Intent(this, RegisterActivity::class.java)
            )
        }
    }

    private fun validateData(): Boolean {
        email = binding.editTextLoginEmail.text.toString()
        password = binding.editTextLoginPassword.text.toString()

        if(email.isNotEmpty()){
            binding.txtInputLoginEmailContainer.error = null
            if(password.isNotEmpty()){
                binding.txtInputLoginPasswordContainer.error = null
                return true
            } else {
                binding.txtInputLoginPasswordContainer.error = "Preencha sua senha"
                return false
            }
        } else {
            binding.txtInputLoginEmailContainer.error = "Preencha seu e-mail"
            return false
        }
    }

    private fun userLogin() {
        email = binding.editTextLoginEmail.text.toString()
        password = binding.editTextLoginPassword.text.toString()

        auth
            .signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                startActivity(
                    Intent(this, MainActivity::class.java)
                )
                showMessage("Login realizado com sucesso")
            }
            .addOnFailureListener{ erro ->
                try {
                    throw erro
                } catch (errorInvalidEmail : FirebaseAuthInvalidUserException){
                    showMessage("E-mail não cadastrado!")
                } catch (errorInvalidCredentials : FirebaseAuthInvalidCredentialsException){
                    showMessage("E-mail ou senha incorreto!")
                }
            }
    }
}