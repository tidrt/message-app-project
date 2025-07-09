package com.example.messageapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.messageapp.databinding.FragmentMessagesBinding
import com.example.messageapp.model.Chat
import com.example.messageapp.utils.Constants
import com.example.messageapp.utils.showMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class MessagesFragment : Fragment() {
    private lateinit var binding : FragmentMessagesBinding
    private lateinit var snapshotEvent : ListenerRegistration

    private val firebase by lazy {
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
          snapshotEvent = firebase
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
                        }
                    }
                    if(chatList.isNotEmpty()){
                        // update adapter
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

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        snapshotEvent.remove()
    }

}