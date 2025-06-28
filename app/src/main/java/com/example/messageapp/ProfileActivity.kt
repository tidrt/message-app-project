package com.example.messageapp

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.messageapp.databinding.ActivityProfileBinding
import com.example.messageapp.utils.showMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ProfileActivity : AppCompatActivity() {
    private val binding by lazy{
        ActivityProfileBinding.inflate(layoutInflater)
    }

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private val storage by lazy {
        FirebaseStorage.getInstance()
    }

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private var camPermission = false
    private var galleryPermission = false

    private val galleryManager = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ){uri ->
        binding.imgProfile.setImageURI(uri)
        saveProfileImageOnStorage(uri)
    }

    private fun saveProfileImageOnStorage(uri: Uri?){
        val userId = auth.currentUser?.uid

        if(uri != null){
            if(userId != null){
                storage
                    .getReference("photos")
                    .child("users")
                    .child(userId)
                    .child("profile.jpg")
                    .putFile(uri)
                    .addOnSuccessListener { taskSnapshot ->
                        showMessage("Sucesso ao fazer o Upload da imagem ao Storage")
                        taskSnapshot
                            .metadata
                            ?.reference
                            ?.downloadUrl
                            ?.addOnSuccessListener { url ->
                                val data = mapOf(
                                    "photo" to url.toString()
                                )
                                updateProfile(userId, data)
                            }
                    }.addOnFailureListener {
                        showMessage("Falha ao fazer o Upload da imagem ao Storage")
                    }
            }
        }
    }

    private fun updateProfile(userId: String, data: Map<String, String>) {
        firestore
            .collection("users")
            .document(userId)
            .update(data)
            .addOnSuccessListener {
                showMessage("Sucesso ao alterar a foto de perfil")
            }
            .addOnFailureListener {
                showMessage("Falha ao alterar a foto de perfil")
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
        initializeToolbar()
        checkPermissions()
        initializeClickEvents()
    }

    private fun initializeClickEvents() {
        val userName = auth.currentUser?.displayName
        binding.editTextNameProfile.hint

        binding.fabEditImageProfile.setOnClickListener {
            if(galleryPermission){
                galleryManager.launch("image/*")
            } else {
                showMessage("Você não tem permissão para acessar a Galeria")
                checkPermissions()
            }
        }
    }

    private fun initializeToolbar() {
        val toolbar = binding.includeTbProfile.tbRegister
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Perfil"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun checkPermissions() {
        camPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            galleryPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        }

        val deniedPermissions = mutableListOf<String>()
        if(!camPermission){
            deniedPermissions.add(Manifest.permission.CAMERA)
        }
        if(!galleryPermission){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                deniedPermissions.add(Manifest.permission.READ_MEDIA_IMAGES)
            }
        }

        if(deniedPermissions.isNotEmpty()){
            val permissionsManager = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ){ permission ->
                camPermission = permission[Manifest.permission.CAMERA] ?: camPermission
                galleryPermission = permission[if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Manifest.permission.READ_MEDIA_IMAGES
                } else {
                }] ?: galleryPermission
            }.launch(deniedPermissions.toTypedArray())
        }
    }
}