package com.example.messageapp.fragments

    import android.content.Intent
    import android.os.Bundle
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import androidx.fragment.app.Fragment
    import androidx.recyclerview.widget.DividerItemDecoration
    import androidx.recyclerview.widget.LinearLayoutManager
    import com.example.messageapp.activities.MessageActivity
    import com.example.messageapp.adapters.ContactAdapter
    import com.example.messageapp.databinding.FragmentContactsBinding
    import com.example.messageapp.model.User
    import com.example.messageapp.utils.Constants
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.firestore.FirebaseFirestore
    import com.google.firebase.firestore.ListenerRegistration


class ContactsFragment : Fragment() {
    private lateinit var binding : FragmentContactsBinding
    private lateinit var contactsAdapter : ContactAdapter

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

        contactsAdapter = ContactAdapter { user ->
            val intent = Intent(context, MessageActivity::class.java)
            intent.putExtra("recipientData", user)
            intent.putExtra("source", Constants.SOURCE_CONTACT)
            startActivity(intent)
        }

        with(binding){
            rvContacts.adapter = contactsAdapter
            rvContacts.layoutManager = LinearLayoutManager(context)
            rvContacts.addItemDecoration(
                DividerItemDecoration(
                    context,
                    LinearLayoutManager.VERTICAL
                )
            )
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        contactsListener()
    }

    private fun contactsListener() {
        snapshotEvent = firestore
            .collection(Constants.DB_USERS)
            .addSnapshotListener { querySnapshot, _ ->
                val contacts = mutableListOf<User>()
                val documents = querySnapshot?.documents
                documents?.forEach { documentSnapshot ->
                    val actualUser = auth.currentUser?.uid
                    val user = documentSnapshot.toObject(User::class.java)
                    if(user != null && actualUser != null){
                        if(actualUser != user.id){
                            contacts.add(user)
                        }
                    }
                }
                if (contacts.isNotEmpty()) {
                    contactsAdapter.addList(contacts)
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        snapshotEvent.remove()
    }
}