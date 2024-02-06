package org.bitc.petpalapp.model

import java.io.Serializable

data class ChatRoom(
    val roomId: String = "",
    val participants: List<String> = emptyList()
)