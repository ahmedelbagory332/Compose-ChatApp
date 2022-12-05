package com.example.domain.use_case.user_use_case

import com.example.domain.model.UsersModel
import com.example.domain.repository.AuthRepository
import com.example.domain.repository.UsersRepository
import com.example.domain.utils.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserUseCase @Inject constructor(private val usersRepository: UsersRepository) {


    operator fun invoke(userId: String, fireBaseResponse: (Resource<List<UsersModel>>) -> Unit) {


        usersRepository.getUser(userId) {
            fireBaseResponse(it)

        }


//    operator fun invoke(userId:String):Flow<Resource<List<UsersModel>>> = flow{
//
//        try {
//            emit(Resource.Loading<List<UsersModel>>())
//
//            usersRepository.getUser(userId).collect{
//                emit(Resource.Success<List<UsersModel>>(it))
//            }
//
//        }catch (e:Exception){
//            emit(Resource.Error<List<UsersModel>>(e.localizedMessage))
//        }
//
//    }

    }
}