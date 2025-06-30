package com.example.messageapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.messageapp.databinding.FragmentContactsBinding
import com.example.messageapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration


class ContactsFragment : Fragment() {
    private lateinit var binding : FragmentContactsBinding

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private lateinit var snapshotEvent : ListenerRegistration

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactsBinding.inflate(
            inflater, container, false
        )
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        contactsListener()
    }

    private fun contactsListener() {
        snapshotEvent = firestore
            .collection("users")
            .addSnapshotListener { querySnapshot, _ ->
                val contacts = mutableListOf<User>()
                val documents = querySnapshot?.documents
                documents?.forEach { documentSnapshot ->
                    val user = documentSnapshot.toObject(User::class.java)
                    if(user != null){
                        val actualUser = auth.currentUser?.uid
                        if(actualUser != null && actualUser != user.id){
                            contacts.add(user)
                        }
                    }
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        snapshotEvent.remove()
    }
}