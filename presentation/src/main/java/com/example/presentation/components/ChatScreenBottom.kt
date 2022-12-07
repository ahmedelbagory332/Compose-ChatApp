package com.example.presentation.components


import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.example.domain.utils.states.UserState
import com.example.presentation.theme.Purple500
import com.example.presentation.ui.view_models.ApplicationViewModel
import com.example.presentation.ui.view_models.AuthViewModel
import com.example.presentation.ui.view_models.ChatPageViewModel
import com.example.presentation.utiles.*
import com.google.firebase.firestore.FieldValue
import java.io.File
import java.util.*


@SuppressLint("SuspiciousIndentation")
@Composable
fun ChatScreenBottom(
    chatPageViewModel: ChatPageViewModel,
    authViewModel: AuthViewModel,
    state: UserState,
    applicationViewModel: ApplicationViewModel = hiltViewModel(),
) {
    var offset by rememberSaveable { mutableStateOf(0f) }

    val takePictureLauncher =
        prepareCameraAndUploadPicture(applicationViewModel, chatPageViewModel, authViewModel, state)

    val takePicturePermissionLauncher =
        takePicturePermission(takePictureLauncher, applicationViewModel, chatPageViewModel)

    val filePickerLauncher =
        fileAttachPicker(applicationViewModel, chatPageViewModel, authViewModel, state)

    val filePickerPermissionLauncher =
        filePickerPermission(filePickerLauncher, applicationViewModel)

    val voiceRecorderPermissionLauncher = voiceRecorderPermissions(
        applicationViewModel,
        chatPageViewModel,
        authViewModel,
        state
    )



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
                placeholder = {
                    if (chatPageViewModel.showTimer.value)
                        Text("Start talking, recording....")
                    else
                        Text("write message.....")

                },
                enabled = !chatPageViewModel.showTimer.value,
                onValueChange = {
                    chatPageViewModel.textMessage.value = it
                    chatPageViewModel.showIcons.value = it.text.isEmpty()
                    if (it.text.isNotEmpty()) {
                        authViewModel.updateUserStatus("Typing...",null)
                    } else {
                        authViewModel.updateUserStatus("Online",null)
                    }
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

            AnimatedVisibility(visible = chatPageViewModel.showIcons.value) {
                IconButton(
                    onClick = {
                        if (hasPermissions(applicationViewModel.application, storagePermissions)) {

                            filePickerLauncher.launch("*/*")
                        }else{
                            filePickerPermissionLauncher.launch(storagePermissions)

                        }
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = "attach"
                    )
                }
            }

            AnimatedVisibility(visible = chatPageViewModel.showIcons.value) {
                IconButton(
                    onClick = {
                        chatPageViewModel.cameraImageName.value =
                            "IMG_${Calendar.getInstance().time}.jpg"
                        if (hasPermissions(applicationViewModel.application, cameraPermissions)) {

                            takePictureLauncher.launch(
                                createUriToTakePictureByCamera(
                                    applicationViewModel.application,
                                    chatPageViewModel
                                )
                            )

                        }else{
                            takePicturePermissionLauncher.launch(cameraPermissions)

                        }


                    },
                ) {
                    Icon(
                        imageVector = Icons.Filled.CameraAlt,
                        contentDescription = "camera",
                    )
                }
            }

        }

        ComposableLifecycle { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                authViewModel.updateUserStatus("offline", FieldValue.serverTimestamp())
                if (!chatPageViewModel.isRecording.value) {
                    if (myAudioRecorder != null) {
                        myAudioRecorder!!.stop()
                        Toast.makeText(
                            applicationViewModel.application,
                            "Recorder canceled",
                            Toast.LENGTH_LONG
                        ).show()
                        chatPageViewModel.resetMediaRecorder()
                        File("$root/Bego Chat/Bego Recorders/${chatPageViewModel.recordFileName.value}").delete()
                    }
                }
            } else if (event == Lifecycle.Event.ON_STOP) {
                authViewModel.updateUserStatus("offline", FieldValue.serverTimestamp())
                chatPageViewModel.resetMediaRecorder()

            } else if (event == Lifecycle.Event.ON_DESTROY) {
                authViewModel.updateUserStatus("offline", FieldValue.serverTimestamp())
                chatPageViewModel.resetMediaRecorder()
            }   else if (event ==Lifecycle.Event.ON_RESUME||event ==Lifecycle.Event.ON_CREATE||event ==Lifecycle.Event.ON_START) {
            authViewModel.updateUserStatus("Online",null)
        }

        }



        Spacer(modifier = Modifier.width(5.dp))
        FloatingActionButton(
            modifier = Modifier.size(48.dp),
            backgroundColor = Purple500,
            onClick = {
                if (chatPageViewModel.textMessage.value.text.isNotEmpty()) {
                    sendTextMessage(chatPageViewModel, authViewModel, state)
                } else {
                    if (hasPermissions(applicationViewModel.application, voiceRecorderPermissions)) {

                        initRecorder(applicationViewModel)
                        sendVoiceMessage(
                            applicationViewModel,
                            root,
                            chatPageViewModel,
                            authViewModel,
                            state
                        )

                    }else{
                        voiceRecorderPermissionLauncher.launch(voiceRecorderPermissions)
                    }

                }
            }
        ) {
            Icon(
                tint = Color.White,
                imageVector = if (chatPageViewModel.textMessage.value.text.isEmpty()) {
                    if (!chatPageViewModel.isRecording.value)
                        Icons.Filled.StopCircle
                    else
                        Icons.Filled.Mic
                } else Icons.Filled.Send,
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.width(5.dp))
    }
    Spacer(modifier = Modifier.height(10.dp))
}







