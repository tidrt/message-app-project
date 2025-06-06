package com.example.messageapp.utils

import android.app.Activity
import android.widget.Toast

fun Activity.showMessage(s : String){
    Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
}