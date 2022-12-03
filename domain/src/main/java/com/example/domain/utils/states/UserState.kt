package com.example.domain.utils.states

import com.example.domain.model.UsersModel


data class UserState (
    val isLoading: Boolean = false,
    val user: List<UsersModel> = mutableListOf(),
    val error: String = ""
)
