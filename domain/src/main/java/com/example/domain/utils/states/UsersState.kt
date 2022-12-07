package com.example.domain.utils.states

import com.example.domain.model.UsersModel


data class UsersState(
    val isLoading: Boolean = false,
    val users:  List<UsersModel> = mutableListOf(),
    val error: String = ""
)
