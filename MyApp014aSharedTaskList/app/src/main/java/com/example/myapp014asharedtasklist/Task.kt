package com.example.myapp014asharedtasklist

import com.google.firebase.firestore.Exclude

data class Task(
    @get:Exclude var id: String = "myapp014asharedtasklist2-9d5b3", // Document ID z Firestore, neukládáme ho do těla dokumentu
    val title: String = "",
    val completed: Boolean = false
)
