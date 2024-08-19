package me.xx2bab.mediapiper.llm

import me.xx2bab.mediapiper.randomUUID

/**
 * Used to represent a ChatMessage
 */
data class ChatMessage(
    val id: String = randomUUID(),
    val message: String = "",
    val author: String,
    val isLoading: Boolean = false
) {
    val isFromUser: Boolean
        get() = author == USER_PREFIX
}