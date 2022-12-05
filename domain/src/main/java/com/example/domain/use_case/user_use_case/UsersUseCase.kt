package com.example.domain.use_case.user_use_case

import com.example.domain.model.UsersModel
import com.example.domain.repository.UsersRepository
import com.example.domain.utils.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UsersUseCase @Inject constructor(private val usersRepository: UsersRepository) {

    operator fun invoke():Flow<Resource<List<UsersModel>>> = flow{

        try {
           emit(Resource.Loading<List<UsersModel>>())


            usersRepository.getUsers().collect{
                emit(Resource.Success<List<UsersModel>>(it))
            }

        }catch (e:Exception){
            emit(Resource.Error<List<UsersModel>>(e.localizedMessage))
        }

    }


}