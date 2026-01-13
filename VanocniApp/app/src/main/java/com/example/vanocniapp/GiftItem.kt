package com.example.vanocniapp

import kotlinx.serialization.Serializable

@Serializable
data class GiftItem(
    val id: String,
    val name: String,
    val price: Double
)
