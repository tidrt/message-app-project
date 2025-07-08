package com.example.messageapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.messageapp.databinding.ItemContactBinding
import com.example.messageapp.model.User
import com.squareup.picasso.Picasso

class ContactAdapter(
    private val onClick : (User) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    private var contactsList = emptyList<User>()
    fun addList(list: List<User>){
        contactsList = list
        notifyDataSetChanged()
    }

    inner class ContactViewHolder(private val binding : ItemContactBinding): ViewHolder(binding.root){
        fun bind(user: User){
            binding.txtName.text = user.name
            Picasso
                .get()
                .load(user.photo)
                .into(binding.imgProfileCardView)
            // click events
            binding.clCardContact.setOnClickListener {
                onClick(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context);
        val itemView = ItemContactBinding.inflate(layoutInflater, parent, false)
        return ContactViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val user = contactsList[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int {
        return contactsList.size
    }
}
