package com.example.messageapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.messageapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val auth by lazy {
        FirebaseAuth.getInstance()
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
        initializeOnClickEvents()
    }

    private fun initializeToolbar() {
        val toolbar = binding.includeTb.tbRegister
        setSupportActionBar(toolbar)
        supportActionBar.apply {
            title = "WhatsApp"
        }

        addMenuProvider(
            object : MenuProvider{
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.principal_menu, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    when(menuItem.itemId){
                        R.id.item_profile ->
                            startActivity(
                                Intent(applicationContext, ProfileActivity::class.java)
                            )
                        R.id.item_logout ->
                            userSignOut()
                    }
                    return true
                }
            }
        )
    }

    private fun initializeOnClickEvents() {
        binding.btnLogOut.setOnClickListener{
            userSignOut()
        }
    }

    private fun userSignOut() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Você deseja mesmo deslogar?")
            .setPositiveButton("Sim"){ _, _ ->
                auth.signOut()
                startActivity(
                    Intent(applicationContext, LoginActivity::class.java)
                )
            }
            .setNegativeButton("Não"){_, _ -> }
            .create()
            .show()
    }
}