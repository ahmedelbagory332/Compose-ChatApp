package com.example.presentation.ui.view_models

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.use_case.LogInUseCase
import com.example.domain.use_case.RegisterUseCase
import com.example.domain.utils.Resource
import com.example.domain.utils.states.AuthState
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@HiltViewModel
class AuthViewModel  @Inject constructor(
    val registerUseCase: RegisterUseCase,
    val logInUseCase: LogInUseCase,
    private val firebaseAuth: FirebaseAuth,
) : ViewModel() {

    private var _authState = mutableStateOf(AuthState())

    val authState: State<AuthState>
        get() = _authState


    fun rest(){
        _authState = mutableStateOf(AuthState())
    }


     fun signUp(firstName: String, lastName: String, email: String, password: String) {
        registerUseCase(firstName,lastName,email,password).onEach { result ->
            Log.d("bego", "${result.data}")

            when (result) {
                is Resource.Success -> {
                    _authState.value = AuthState(authState = result.data ?: "")
                }
                is Resource.Error -> {
                    _authState.value = AuthState(
                        error = result.message ?: "An unexpected error occurred"
                    )
                }
                is Resource.Loading -> {
                    _authState.value = AuthState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }


    fun signIn(email: String, password: String) {
        logInUseCase(email,password).onEach { result ->
            Log.d("bego", "${result.data}")

            when (result) {
                is Resource.Success -> {
                    _authState.value = AuthState(authState = result.data ?: "")
                }
                is Resource.Error -> {
                    _authState.value = AuthState(
                        error = result.message ?: "An unexpected error occurred"
                    )
                }
                is Resource.Loading -> {
                    _authState.value = AuthState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }


    fun getCurrentUser() = firebaseAuth.currentUser

    fun userLogOut() = firebaseAuth.signOut()


}