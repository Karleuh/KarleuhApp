package com.example.karleuhapp.list
import kotlinx.serialization.SerialName



@kotlinx.serialization.Serializable
data class Task(
    @SerialName("id")
    val id: String,
    @SerialName("content")
    val title: String,
    @SerialName("description")
    val description: String? = null
) : java.io.Serializable