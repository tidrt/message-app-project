package com.example.messageapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.messageapp.databinding.ItemChatBinding
import com.example.messageapp.model.Chat
import com.squareup.picasso.Picasso

class ChatAdapter(
    private val onClick : (Chat) -> Unit
) : Adapter<ChatAdapter.ChatViewHolder>() {

    private var chatList = emptyList<Chat>()
    fun addList(chat : List<Chat>){
        chatList = chat
        notifyDataSetChanged()
    }

    inner class ChatViewHolder(private val binding : ItemChatBinding) : ViewHolder(binding.root){
        // fun que faz o bind e o evento de click
        fun bind(chat : Chat){
            with(binding){
                Picasso.get()
                    .load(chat.photo)
                    .into(imgProfileChat)

                txtNameChat.text = chat.name
                txtLastMessageChat.text = chat.lastMessage

                clCardChat.setOnClickListener {
                    onClick(chat)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        // infla nosso layout
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = ItemChatBinding.inflate(layoutInflater, parent, false)
        return ChatViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chatList[position]
        holder.bind(chat)
    }

    override fun getItemCount(): Int {
        return chatList.size
    }
}