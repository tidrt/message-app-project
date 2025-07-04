package com.example.messageapp.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Message(
    val senderId : String = "",
    val message : String = "",
    @ServerTimestamp
    val date : Date? = null,
)
