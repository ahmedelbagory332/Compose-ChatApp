package com.example.domain.repository

import com.example.domain.model.UsersModel
import com.example.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

interface UsersRepository {



     fun getUsers():Flow<MutableList<UsersModel>>

     fun getUser(userId:String,fireBaseResponse:(Resource<List<UsersModel>>)->Unit)


}