package com.example.karleuhapp.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("email")
    val email: String,
    @SerialName("full_name")
    var name: String,
    @SerialName("avatar_medium")
    val avatar: String? = null
)


@Serializable
data class Args(
    @SerialName("full_name")
    val username: String,

)
@Serializable
data class Command(
    val type: String,
    val uuid: String,
    val args: Args
)

@Serializable
data class Commands(
    val commands: List<Command>
)
