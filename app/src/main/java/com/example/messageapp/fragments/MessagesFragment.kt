package com.example.messageapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messageapp.activities.MessageActivity
import com.example.messageapp.adapters.ChatAdapter
import com.example.messageapp.databinding.FragmentMessagesBinding
import com.example.messageapp.model.Chat
import com.example.messageapp.model.User
import com.example.messageapp.utils.Constants
import com.example.messageapp.utils.showMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class MessagesFragment : Fragment() {
    private lateinit var binding : FragmentMessagesBinding
    private lateinit var snapshotEvent : ListenerRegistration
    private lateinit var chatAdapter : ChatAdapter

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        chatsListener()
    }

    private fun chatsListener() {
        val userId = auth.currentUser?.uid
        if(userId != null){
          snapshotEvent = firestore
                .collection(Constants.DB_CHAT)
                .document(userId)
                .collection(Constants.DB_LAST_CHATS)
                .addSnapshotListener { querySnapshot, error ->
                    if(error != null){
                        activity?.showMessage("Erro ao recuperar conversa")
                    }
                    val chatList = mutableListOf<Chat>()
                    val document = querySnapshot?.documents

                    document?.forEach { documentSnapshot ->
                        val chat = documentSnapshot.toObject(Chat::class.java)
                        if(chat != null){
                            chatList.add(chat)
                            Log.i("info_chat", "${chat.name} - ${chat.lastMessage}")
                        }
                    }
                    if(chatList.isNotEmpty()){
                        chatAdapter.addList(chatList)
                    }
                }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMessagesBinding.inflate(
            inflater, container, false
        )

        chatAdapter = ChatAdapter{ chat ->
            val intent = Intent(context, MessageActivity::class.java)

            val user = User(
                id = chat.idReceiver,
                name = chat.name,
                photo = chat.photo
            )
            intent.putExtra("recipientData", user)
            intent.putExtra("source", Constants.SOURCE_CONTACT)
            startActivity(intent)
        }

        with(binding){
            rvChats.adapter = chatAdapter
            rvChats.layoutManager = LinearLayoutManager(context)
            rvChats.addItemDecoration(
                DividerItemDecoration(
                    context, LinearLayoutManager.VERTICAL
                )
            )
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        snapshotEvent.remove()
    }

}