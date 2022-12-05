@file:Suppress("UNREACHABLE_CODE")

package com.example.presentation.screens


import android.app.Application
import android.app.DownloadManager
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
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
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.example.presentation.R
import com.example.presentation.components.*
import com.example.presentation.ui.view_models.ApplicationViewModel
import com.example.presentation.ui.view_models.AuthViewModel
import com.example.presentation.ui.view_models.ChatPageViewModel
import com.example.presentation.ui.view_models.HomePageViewModel
import com.example.presentation.utiles.downloadFile
import com.example.presentation.utiles.root
import com.google.firebase.firestore.FieldValue
import java.io.File


@Composable
fun ChatScreen(
    userId: String = "",
     homePageViewModel: HomePageViewModel = hiltViewModel(),
    chatPageViewModel: ChatPageViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    applicationContext: ApplicationViewModel = hiltViewModel(),
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

                        BuildChatScreenContent(
                            chatPageViewModel,
                            index,
                            textMessage,
                            authViewModel,
                            onFileClick = { fileUrl,fileName->
                                downloadFile(fileUrl,fileName, applicationContext)
                        })

                    }
                }
            }

            ChatScreenBottom(chatPageViewModel, authViewModel, state)

        }

        ComposableLifecycle { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    Log.d("TAG", "ChatScreen:ON_PAUSE ")
                    authViewModel.updateUserStatus("offline", FieldValue.serverTimestamp())

                }
                Lifecycle.Event.ON_RESUME, Lifecycle.Event.ON_CREATE,Lifecycle.Event.ON_START -> {

                     authViewModel.updateUserStatus("Online",null)

                }
                Lifecycle.Event.ON_DESTROY -> {
                    Log.d("TAG", "ChatScreen:ON_DESTROY ")
                    authViewModel.updateUserStatus("offline", FieldValue.serverTimestamp())

                }
                Lifecycle.Event.ON_STOP -> {
                    Log.d("TAG", "ChatScreen:ON_STOP ")
                    authViewModel.updateUserStatus("offline", FieldValue.serverTimestamp())

                }
                else -> {
                    Log.d("TAG", "ChatScreen:else ")
                    authViewModel.updateUserStatus("offline", FieldValue.serverTimestamp())

                }
            }
        }

    }
}



