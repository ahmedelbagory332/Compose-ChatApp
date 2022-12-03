package com.example.domain.utils.states

import com.example.domain.model.ChatModel
import com.example.domain.model.UsersModel


data class ChatState (
    val isLoading: Boolean = false,
    val messages: List<ChatModel> = mutableListOf(),
    val error: String = ""
)
