package com.example.domain.use_case.auth_use_case

import com.example.domain.repository.AuthRepository
import com.example.domain.utils.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RegisterUseCase @Inject constructor(private val authRepository: AuthRepository) {

    operator fun invoke(firstName: String, lastName: String, email: String, password: String):Flow<Resource<String>> = flow{

        try {
            emit(Resource.Loading<String>())
            authRepository.signUp(firstName,lastName,email,password).collect{
                emit(Resource.Success<String>(it))
            }

        }catch (e:Exception){
            emit(Resource.Error<String>(e.localizedMessage))
        }

        delay(100)
        emit(Resource.Success<String>(""))
        emit(Resource.Error<String>(""))
    }

}