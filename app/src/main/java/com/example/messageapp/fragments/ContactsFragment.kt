package com.example.messageapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.messageapp.R
import com.example.messageapp.databinding.FragmentContactsBinding
import com.example.messageapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class ContactsFragment : Fragment() {
    private lateinit var binding : FragmentContactsBinding

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        firestore
            .collection("users")
            .addSnapshotListener { querySnapshot, error ->
                val documents = querySnapshot?.documents
                documents?.forEach { documentSnapshot ->
                    val user = documentSnapshot.toObject(User::class.java)
                    if(user != null){
                        Log.i("contact_listener", "nome: ${user.name}")
                    }
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}