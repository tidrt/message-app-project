package com.example.messageapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.messageapp.databinding.ItemMessageReceiverBinding
import com.example.messageapp.databinding.ItemMessageSenderBinding
import com.example.messageapp.model.Message
import com.example.messageapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter : Adapter<ViewHolder>() {

    private var messagesList = emptyList<Message>()
    fun addList(messages : List<Message>){
        messagesList = messages
        notifyDataSetChanged()
    }

    class MessageSenderViewHolder(
        private val binding : ItemMessageSenderBinding
    ) : ViewHolder(binding.root){

        fun bind(message: Message){
            binding.txtMessageSender.text = message.message
        }

        companion object {
            fun inflateMessageSender(parent: ViewGroup) : ViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val itemView = ItemMessageSenderBinding.inflate(layoutInflater, parent, false)
                return MessageSenderViewHolder(itemView)
            }
        }
    }

    class MessageReceiverViewHolder(
        private val binding : ItemMessageReceiverBinding
    ) : ViewHolder(binding.root){

        fun bind(message: Message){
            binding.txtMessageReceiver.text = message.message
        }

        companion object{
            fun inflateMessageReceiver(parent: ViewGroup) : ViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val itemView = ItemMessageReceiverBinding.inflate(layoutInflater, parent, false)
                return MessageReceiverViewHolder(itemView)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = messagesList[position]
        val loggedUserId = FirebaseAuth.getInstance().currentUser?.uid.toString()

        return if(loggedUserId == message.senderId){
            Constants.TYPE_SENDER
        } else {
            Constants.TYPE_RECEIVER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if(viewType == Constants.TYPE_SENDER){
            return MessageSenderViewHolder.inflateMessageSender(parent)
        }
        return MessageReceiverViewHolder.inflateMessageReceiver(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messagesList[position]
        when(holder){
            is MessageSenderViewHolder -> holder.bind(message)
            is MessageReceiverViewHolder -> holder.bind(message)
        }
    }

    override fun getItemCount(): Int {
       return messagesList.size
    }
}