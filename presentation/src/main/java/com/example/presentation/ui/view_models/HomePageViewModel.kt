package com.example.presentation.ui.view_models

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.use_case.user_use_case.UserUseCase
import com.example.domain.use_case.user_use_case.UsersUseCase
import com.example.domain.utils.Resource
import com.example.domain.utils.states.UserState
import com.example.domain.utils.states.UsersState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomePageViewModel  @Inject constructor(
    val usersUseCase: UsersUseCase,
    val userUseCase: UserUseCase

) : ViewModel(){


    val screens: State<List<String>> = mutableStateOf(listOf("Chats", "Users"))
    private val _searchTextState = mutableStateOf(value = "")
    private val _searchWidgetState = mutableStateOf(value = false)
    private val _usersState = mutableStateOf(UsersState())
    private val _userState = mutableStateOf(UserState())

    val usersState: State<UsersState> = _usersState
    val userState: State<UserState> = _userState

    val searchTextState: State<String> = _searchTextState
    val searchWidgetState: State<Boolean> = _searchWidgetState

    fun updateSearchWidgetState(newValue: Boolean) {
        _searchWidgetState.value = newValue
    }

    fun updateSearchTextState(newValue: String) {
        _searchTextState.value = newValue
    }


    init {
        getAllUsers()
    }




       private fun getAllUsers() {
        usersUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _usersState.value = UsersState(users = result.data ?: mutableListOf())
                }
                is Resource.Error -> {
                    _usersState.value = UsersState(
                        error = result.message ?: "An unexpected error occurred"
                    )
                }
                is Resource.Loading -> {
                    _usersState.value = UsersState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getUser(userId:String) {
       viewModelScope.launch {
           userUseCase(userId) { result ->
               when (result) {
                   is Resource.Success -> {
                       _userState.value = UserState(user = result.data ?: mutableListOf())
                   }
                   is Resource.Error -> {
                       _userState.value = UserState(
                           error = result.message ?: "An unexpected error occurred"
                       )
                   }
                   is Resource.Loading -> {
                    }
               }
           }
       }
    }


//    fun getUser(userId:String) {
//        userUseCase(userId).onEach { result ->
//            when (result) {
//                is Resource.Success -> {
//                    _userState.value = UserState(user = result.data ?: mutableListOf())
//                }
//                is Resource.Error -> {
//                    _userState.value = UserState(
//                        error = result.message ?: "An unexpected error occurred"
//                    )
//                }
//                is Resource.Loading -> {
//                    _userState.value = UserState(isLoading = true)
//                }
//            }
//        }.launchIn(viewModelScope)
//      }


}
