package org.bitc.petpalapp.model

import java.io.Serializable

data class Messages(
    val senderId: String = "",
    val text: String = "",
    val timestamp: Long = 0
)