package com.example.domain.use_case.auth_use_case

import com.example.domain.repository.AuthRepository
import com.example.domain.utils.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateUserStatusUseCase @Inject constructor(private val authRepository: AuthRepository) {

    operator fun invoke(userStatus:String, lastSeen: Any?)=
        authRepository.updateUserStatus(userStatus, lastSeen)

}