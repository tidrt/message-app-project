package com.example.messageapp.activities

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messageapp.R
import com.example.messageapp.adapters.ContactAdapter
import com.example.messageapp.adapters.MessageAdapter
import com.example.messageapp.databinding.ActivityMessageBinding
import com.example.messageapp.model.Chat
import com.example.messageapp.model.Message
import com.example.messageapp.model.User
import com.example.messageapp.utils.Constants
import com.example.messageapp.utils.showMessage
import com.google.api.Distribution.BucketOptions.Linear
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
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

    private lateinit var listenerRegistration: ListenerRegistration
    private lateinit var messagesAdapter : MessageAdapter
    private var senderData : User? = null
    private var receiverData : User? = null

    private fun initializeListeners() {
        val senderId = auth.currentUser?.uid
        val receiverId = receiverData?.id

        if(senderId != null && receiverId != null){
            listenerRegistration = firestore.collection(Constants.DB_MESSAGES)
                .document(senderId)
                .collection(receiverId)
                .orderBy("date", Query.Direction.ASCENDING)
                .addSnapshotListener { querySnapshot, error ->
                    if(error != null){
                        showMessage("Erro ao carregar mensagens!")
                    }

                    val messageList = mutableListOf<Message>()
                    val documents = querySnapshot?.documents
                    documents?.forEach { documentSnapshot ->
                        val message = documentSnapshot.toObject(Message::class.java)
                        if(message != null){
                            messageList.add(message)
                            Log.i("info_messages", message.message)
                        }
                    }

                    if(messageList.isNotEmpty()){
                        messagesAdapter.addList(messageList)
                    }
                }
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
        recoverUserData()
        initializeListeners()
        initializeToolbar()
        initializeClickEvents()
        initializeViewHolder()
    }

    private fun initializeViewHolder() {
        with(binding){
            messagesAdapter = MessageAdapter()
            rvMessages.adapter = messagesAdapter
            rvMessages.layoutManager = LinearLayoutManager(applicationContext)
        }
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
            val receiverId = receiverData?.id

            if(senderId != null && receiverId != null){
                val message = Message(
                    senderId,
                    textMessage
                )

                // from sender perspective
                // receiver chat -> photo and name (receiver)
                saveMassageDb(senderId, receiverId, message)
                val senderChat = Chat(
                    senderId,
                    receiverId,
                    receiverData!!.name,
                    receiverData!!.photo,
                    textMessage
                )
                saveChatDb(senderChat)

                // from receiver perspective
                // receiver chat -> photo and name (sender)
                saveMassageDb(receiverId, senderId, message)
                val receiverChat = Chat(
                    receiverId,
                    senderId,
                    senderData!!.name,
                    senderData!!.photo,
                    textMessage
                )
                saveChatDb(receiverChat)
            }
        }
    }

    private fun saveChatDb(chat: Chat) {
        firestore
            .collection(Constants.DB_CHAT)
            .document(chat.idSender)
            .collection(Constants.DB_LAST_CHATS)
            .document(chat.idReceiver)
            .set(chat)
            .addOnFailureListener {
                showMessage("Erro ao salvar conversa!")
            }
    }

    private fun saveMassageDb(senderId: String, receiverId: String, message: Message) {
        firestore
            .collection(Constants.DB_MESSAGES)
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
            if(receiverData != null){
                Picasso.get()
                    .load(receiverData!!.photo)
                    .into(binding.imgMessageRecipientProfilePhoto)

                binding.txtMessageRecipientProfileName.text = receiverData!!.name
            }
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun recoverUserData() {
        val senderId = auth.currentUser?.uid

        if(senderId != null){
            firestore
                .collection(Constants.DB_USERS)
                .document(senderId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val user = documentSnapshot.toObject(User::class.java)
                    if(user != null){
                        senderData = user
                    }
                }
        }

        val extras = intent.extras
        if(extras != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                receiverData = extras.getParcelable("recipientData", User::class.java)
            } else {
                receiverData = extras.getParcelable("recipientData")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration.remove()
    }
}