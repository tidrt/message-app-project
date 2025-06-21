package com.example.messageapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.messageapp.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private val binding by lazy{
        ActivityProfileBinding.inflate(layoutInflater)
    }

    private var camPermission = false
    private var galleryPermission = false

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
    }

    private fun checkPermissions() {
        camPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        galleryPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        val deniedPermissions = mutableListOf<String>()
        if(!camPermission){
            deniedPermissions.add(Manifest.permission.CAMERA)
        }
        if(!galleryPermission){
            deniedPermissions.add(Manifest.permission.CAMERA)
        }

        if(deniedPermissions.isNotEmpty()){
            val permissionsManager = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ){ permission ->
                camPermission = permission[Manifest.permission.CAMERA] ?: camPermission
                galleryPermission = permission[Manifest.permission.READ_EXTERNAL_STORAGE] ?: galleryPermission
            }.launch(deniedPermissions.toTypedArray())
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
}