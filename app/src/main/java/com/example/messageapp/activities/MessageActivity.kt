package com.example.messageapp.activities

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.messageapp.R
import com.example.messageapp.databinding.ActivityMessageBinding
import com.example.messageapp.model.User
import com.example.messageapp.utils.Constants
import com.squareup.picasso.Picasso

class MessageActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMessageBinding.inflate(layoutInflater)
    }

    private var recipientData : User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        recoverRecipientData()
        initializeToolbar()
    }

    private fun initializeToolbar() {
        val toolbar = binding.mtMessageToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = ""
            if(recipientData != null){
                Picasso.get()
                    .load(recipientData!!.photo)
                    .into(binding.imgMessageRecipientProfilePhoto)

                binding.txtMessageRecipientProfileName.text = recipientData!!.name
            }
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun recoverRecipientData() {
        val extras = intent.extras
        if(extras != null){
            val source = extras.getString("source")
            if(source == Constants.SOURCE_CONTACT){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    recipientData = extras.getParcelable("recipientData", User::class.java)
                } else {
                    recipientData = extras.getParcelable("recipientData")
                }
            } else if(source == Constants.SOURCE_CHAT){

            }
        }
    }
}