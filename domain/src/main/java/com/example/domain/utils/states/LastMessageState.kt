package com.example.domain.utils.states

import com.example.domain.model.ChatModel
import com.example.domain.model.LastMessageModel
import com.example.domain.model.UsersModel


data class LastMessageState (
    val isLoading: Boolean = false,
    val messages: List<LastMessageModel> = mutableListOf(),
    val error: String = ""
)
