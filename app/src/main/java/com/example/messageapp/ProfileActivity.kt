package com.example.messageapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.messageapp.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private val binding by lazy{
        ActivityProfileBinding.inflate(layoutInflater)
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