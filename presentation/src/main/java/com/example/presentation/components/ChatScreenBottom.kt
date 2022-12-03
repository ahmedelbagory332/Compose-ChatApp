package com.example.presentation.components


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.domain.utils.states.UserState
import com.example.presentation.theme.Purple500
import com.example.presentation.ui.view_models.ApplicationViewModel
import com.example.presentation.ui.view_models.AuthViewModel
import com.example.presentation.ui.view_models.ChatPageViewModel
import java.util.*



@Composable
fun ChatScreenBottom(
    chatPageViewModel: ChatPageViewModel,
    authViewModel: AuthViewModel,
    state: UserState,
    applicationViewModel: ApplicationViewModel = hiltViewModel(),

    ) {
    var offset by remember { mutableStateOf(0f) }

    val imagePickerLauncher =
        prepareCameraAndUploadPicture(applicationViewModel, chatPageViewModel, authViewModel, state)


    val filePickerLauncher =
        fileAttachPicker(applicationViewModel, chatPageViewModel, authViewModel, state)

    Row(
        verticalAlignment = Alignment.Bottom
    ) {
        Spacer(modifier = Modifier.width(5.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f)
                .border(1.dp, Color.LightGray, CircleShape),
        ) {
            TextField(
                maxLines = 2,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .scrollable(
                        orientation = Orientation.Vertical,
                        state = rememberScrollableState { delta ->
                            offset += delta
                            delta
                        }
                    ),
                value = chatPageViewModel.textMessage.value,
                onValueChange = {
                    chatPageViewModel.textMessage.value = it
                },
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.Black,
                    backgroundColor = Color.White,
                    disabledTextColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )
            AnimatedVisibility(visible = chatPageViewModel.textMessage.value.text.isEmpty()) {
                IconButton(
                    onClick = {
                        filePickerLauncher.launch("*/*")
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = "attach"
                    )
                }
            }

            AnimatedVisibility(visible = chatPageViewModel.textMessage.value.text.isEmpty()) {
                IconButton(
                    onClick = {
                        chatPageViewModel.cameraImageName.value =
                            "IMG_${Calendar.getInstance().time}.jpg"

                        imagePickerLauncher.launch(
                            createUriToTakePictureByCamera(
                                applicationViewModel.application,
                                chatPageViewModel
                            )
                        )

                    },
                ) {
                    Icon(
                        imageVector = Icons.Filled.CameraAlt,
                        contentDescription = "camera",
                    )
                }
            }


        }

        Spacer(modifier = Modifier.width(5.dp))
        FloatingActionButton(
            modifier = Modifier.size(48.dp),
            backgroundColor = Purple500,
            onClick = {
                if (chatPageViewModel.textMessage.value.text.isNotEmpty()) {
                    chatPageViewModel.sendMessage(
                        messageSenderId = authViewModel.getCurrentUser()!!.uid,
                        messageSenderName = authViewModel.getCurrentUser()!!.displayName.toString(),
                        messageReceiverId = state.user[0].userId.toString(),
                        messageReceiverName = state.user[0].name.toString(),
                        messageText = chatPageViewModel.textMessage.value.text
                    )
                    chatPageViewModel.textMessage.value = TextFieldValue("")
                }
            }
        ) {
            Icon(
                tint = Color.White,
                imageVector = if (chatPageViewModel.textMessage.value.text.isEmpty()) Icons.Filled.Mic else Icons.Filled.Send,
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.width(5.dp))
    }
    Spacer(modifier = Modifier.height(10.dp))
}



