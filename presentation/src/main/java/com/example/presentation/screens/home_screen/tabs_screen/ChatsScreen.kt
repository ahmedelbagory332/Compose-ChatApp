
package com.example.presentation.screens.home_screen.tabs_screen


import android.annotation.SuppressLint
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.presentation.R
import com.example.presentation.components.ChatRow
import com.example.presentation.components.ChatScreenBottom
import com.example.presentation.components.getActualTime
import com.example.presentation.theme.*
import com.example.presentation.ui.view_models.AuthViewModel
import com.example.presentation.ui.view_models.ChatPageViewModel
import com.example.presentation.ui.view_models.HomePageViewModel
import com.example.presentation.utiles.Routes
import com.google.firebase.Timestamp


@SuppressLint("SuspiciousIndentation")
@Composable

fun ChatsScreen(
    mainNavController: NavHostController,
    chatPageViewModel: ChatPageViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),

    ) {
     if(authViewModel.getCurrentUser()!=null)
    chatPageViewModel.getLastMessages(authViewModel.getCurrentUser()!!.uid)
    ComposeChatAppTheme {
        Column {
            if (chatPageViewModel.lastMessagesState.value.messages.isEmpty() ||
                chatPageViewModel.lastMessagesState.value.error.isNotEmpty()
            ) {

                Image(
                    painter = painterResource(id = R.drawable.no_messages_blank_state),
                    contentDescription = "logo",
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )

            } else if (chatPageViewModel.lastMessagesState.value.messages.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .background(darkWhite)
                ){
                    itemsIndexed(
                        items = chatPageViewModel.lastMessagesState.value.messages,
                    ) { index, textMessage ->
                        if(authViewModel.getCurrentUser()!=null) {
                            if (textMessage.msgType == "text") {
                                val message =
                                    if (textMessage.messageSenderId == authViewModel.getCurrentUser()!!.uid)
                                        "you : ${textMessage.message}" else textMessage.message
                                val userName =
                                    if (textMessage.messageSenderId != authViewModel.getCurrentUser()!!.uid)
                                        textMessage.messageSenderName else textMessage.messageReceiverName
                                ChatRow(userName = userName.toString(),
                                    message = message.toString(),
                                    time = getActualTime(textMessage.msgTime as Timestamp),
                                    onChatClick = {
                                        val userId =
                                            if (textMessage.messageSenderId == authViewModel.getCurrentUser()!!.uid)
                                                textMessage.messageReceiverId else textMessage.messageSenderId
                                        mainNavController.navigate(route = Routes().chatScreen + "/$userId") {
                                            launchSingleTop = true

                                        }
                                    })

                            }
                            else {

                                val userName =
                                    if (textMessage.messageSenderId != authViewModel.getCurrentUser()!!.uid)
                                        textMessage.messageSenderName else textMessage.messageReceiverName

                                val message =
                                    if (textMessage.messageSenderId == authViewModel.getCurrentUser()!!.uid)
                                        "you sent ${textMessage.msgType} " else "$userName sent ${textMessage.msgType} "

                                ChatRow(userName = userName.toString(),
                                    message = message.toString(),
                                    time = getActualTime(textMessage.msgTime as Timestamp),
                                    onChatClick = {
                                        val userId =
                                            if (textMessage.messageSenderId == authViewModel.getCurrentUser()!!.uid)
                                                textMessage.messageReceiverId else textMessage.messageSenderId
                                        mainNavController.navigate(route = Routes().chatScreen + "/$userId") {
                                            launchSingleTop = true

                                        }
                                    })


                            }
                        }




                    }
                }
            }



        }
    }
}

