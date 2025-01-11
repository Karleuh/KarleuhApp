package com.example.karleuhapp.list
import kotlinx.serialization.SerialName
import java.io.Serializable
import java.util.UUID


@kotlinx.serialization.Serializable
data class Task(
    @SerialName("id")
    val id: String,
    @SerialName("content")
    val title: String,
    @SerialName("description")
    val description: String? = null
) : java.io.Serializable