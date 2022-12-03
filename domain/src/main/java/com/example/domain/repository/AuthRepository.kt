package com.example.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {


    fun signUp(firstName:String,lastName:String,email:String, password:String): Flow<String>

    fun signIn(email:String, password:String): Flow<String>

}