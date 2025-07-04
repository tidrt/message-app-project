package com.example.messageapp.activities

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.messageapp.R
import com.example.messageapp.databinding.ActivityMessageBinding
import com.example.messageapp.model.Message
import com.example.messageapp.model.User
import com.example.messageapp.utils.Constants
import com.example.messageapp.utils.showMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.util.Date

class MessageActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMessageBinding.inflate(layoutInflater)
    }

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
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
        initializeClickEvents()
    }

    private fun initializeClickEvents() {
        with(binding){
            fabSend.setOnClickListener {
                val message = editMessage.text.toString()
                saveMessage(message)
                editMessage.setText("")
            }
        }
    }

    private fun saveMessage(textMessage: String) {
        if (textMessage.isNotEmpty()) {
            val senderId = auth.currentUser?.uid
            val receiverId = recipientData?.id

            if(senderId != null && receiverId != null){
                val message = Message(
                    senderId,
                    textMessage
                )

                // from sender perspective
                saveMassageDb(senderId, receiverId, message)

                // from receiver perspective
                saveMassageDb(receiverId, senderId, message)
            }
        }
    }

    private fun saveMassageDb(senderId: String, receiverId: String, message: Message) {
        firestore
            .collection("messages")
            .document(senderId)
            .collection(receiverId)
            .add(message)
            .addOnFailureListener {
                showMessage("Falha ao enviar mensagem")
            }
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