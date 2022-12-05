@file:Suppress("UNREACHABLE_CODE")

package com.example.presentation.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.presentation.R
import com.example.presentation.components.*
import com.example.presentation.ui.view_models.AuthViewModel
import com.example.presentation.ui.view_models.ChatPageViewModel
import com.example.presentation.ui.view_models.HomePageViewModel

@Composable
fun ChatScreen(
    userId: String = "",
    homePageViewModel: HomePageViewModel = hiltViewModel(),
    chatPageViewModel: ChatPageViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
) {
    homePageViewModel.getUser(userId)
    val state = homePageViewModel.userState.value


    Scaffold(
        topBar = {
            if (state.error.isNotEmpty()) {
                TopAppBar(title = {
                    Text(
                        text = state.error,
                        style = TextStyle(color = Color.White)
                    )
                })
            } else if (state.user.isNotEmpty()) {

                chatPageViewModel.setChatId(
                    if (state.user[0].userId.hashCode() > authViewModel.getCurrentUser()!!.uid.hashCode())
                        "${state.user[0].userId}-${authViewModel.getCurrentUser()!!.uid}"
                    else
                        "${authViewModel.getCurrentUser()!!.uid}-${state.user[0].userId}"
                )
                chatPageViewModel.getMessages()
                ChatTopBar(state.user[0], onUserClick = {

                })

            }

        },
    ) {
        Column {
            if (chatPageViewModel.messagesState.value.messages.isEmpty() ||
                chatPageViewModel.messagesState.value.error.isNotEmpty()
            ) {

                Image(
                    painter = painterResource(id = R.drawable.no_messages_blank_state),
                    contentDescription = "logo",
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )

            } else if (chatPageViewModel.messagesState.value.messages.isNotEmpty()) {
                LazyColumn(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    reverseLayout = true
                ) {
                    itemsIndexed(
                        items = chatPageViewModel.messagesState.value.messages,
                    ) { index, textMessage ->

                        BuildChatScreenContent(chatPageViewModel, index, textMessage, authViewModel)

                    }
                }
            }

            ChatScreenBottom(chatPageViewModel, authViewModel, state)

        }

    }
}




