package com.example.messageapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.messageapp.databinding.ActivityRegisterBinding
import com.example.messageapp.model.User
import com.example.messageapp.utils.showMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private lateinit var name : String
    private lateinit var email : String
    private lateinit var password : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeToolbar()
        initializeClickEvents()
    }

    private fun initializeClickEvents() {
        binding.btnSignUp.setOnClickListener {
            if(validateData()){
                createUser(email, password)
            }
        }
    }

    private fun validateData(): Boolean {

        name = binding.editTextName.text.toString()
        email = binding.editTextEmail.text.toString()
        password = binding.editTextPassword.text.toString()

        if(name.isNotEmpty()){
            binding.editTextName.error = null

            if(email.isNotEmpty()){
                binding.editTextEmail.error = null

                if(password.isNotEmpty()){
                    binding.editTextPassword.error = null
                    return true
                } else {
                    binding.editTextPassword.error = "Digite sua senha"
                    return false
                }
            } else {
                binding.editTextEmail.error = "Preencha seu e-mail"
                return false
            }
        } else {
            binding.editTextName.error = "Preencha seu nome"
            return false
        }
    }

    private fun createUser(email : String, password : String){
        auth.createUserWithEmailAndPassword(
            email,
            password
        ).addOnCompleteListener { result ->
            if (result.isSuccessful) {
                showMessage("Cadastro realizado com sucesso")

                val userId = result.result.user?.uid
                if(userId != null){
                    val user = User(userId, name, email)
                    saveUserOnFirebase(user)
                }
            }
        }.addOnFailureListener { exception ->
            try {
                throw exception
            } catch (weakPasswordException : FirebaseAuthWeakPasswordException){
                weakPasswordException.printStackTrace()
                showMessage("Senha fraca, digite uma senha com ao menos 1 caractere especial e uma letra maiúscula")
            } catch (userCollisionException : FirebaseAuthUserCollisionException){
                userCollisionException.printStackTrace()
                showMessage("Já existe um usuário com esse e-mail, digite outro e-mail")
            } catch (invalidCredentialsException : FirebaseAuthInvalidCredentialsException){
                invalidCredentialsException.printStackTrace()
                showMessage("E-mail inválido, digite outro e-mail")
            }
        }
    }

    private fun saveUserOnFirebase(user: User) {
        firestore
            .collection("users")
            .document(user.id)
            .set(user)
            .addOnSuccessListener {
                startActivity(
                    Intent(applicationContext, MainActivity::class.java)
                )
                showMessage("Sucesso ao salvar seus dados")
            }
            .addOnFailureListener {
                showMessage("Erro ao salvar seus dados!")
            }
    }

    private fun initializeToolbar() {
        val toolbar = binding.includeTbRegister.tbRegister
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = ""
            setDisplayHomeAsUpEnabled(true)
        }
    }
}


