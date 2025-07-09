package com.example.messageapp.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Chat(
    val idSender : String = "",
    val idReceiver : String = "",
    val name : String = "",
    val photo : String = "",
    val lastMessage : String = "",
    @ServerTimestamp
    val date : Date? = null
)
