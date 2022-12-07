package com.example.presentation.components

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import coil.ImageLoader
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.example.domain.model.ChatModel
import com.example.presentation.R
import com.example.presentation.ui.view_models.ApplicationViewModel
import com.example.presentation.ui.view_models.AuthViewModel
import com.example.presentation.ui.view_models.ChatPageViewModel
import com.example.presentation.utiles.*
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView
import java.io.File


@OptIn(ExperimentalMaterialApi::class)
@Composable
  fun BuildChatScreenContent(
    chatPageViewModel: ChatPageViewModel,
    index: Int,
    messages: ChatModel,
    authViewModel: AuthViewModel,
    applicationViewModel: ApplicationViewModel = hiltViewModel(),
    onFileClick:(fileUrl:String,fileName:String)->Unit
) {
    when (chatPageViewModel.messagesState.value.messages[index].messageType) {
        "text" -> {
            MessageRow(messages, authViewModel) {
                Text(
                    text = messages.messageText.toString(),
                    if (messages.messageSenderId == authViewModel.getCurrentUser()!!.uid)
                        Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp)
                    else
                        Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp),
                    style = if (messages.messageSenderId == authViewModel.getCurrentUser()!!.uid)
                        TextStyle(color = Color.White)
                    else
                        TextStyle(color = Color.Black)
                )
            }

        }
        "image" -> {
            MessageRow(messages, authViewModel) {

                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(applicationViewModel.application)
                        .data(messages.messageFile)
                        .crossfade(true)
                        .build(),

                    loading = {
                        Image(

                            painter = painterResource(id = R.drawable.image_place_holder),
                            contentDescription = "",
                            contentScale = ContentScale.FillWidth

                        )
                    },
                    error = {
                        Image(

                            painter = painterResource(id = R.drawable.image_place_holder),
                            contentDescription = "",
                            contentScale = ContentScale.FillWidth

                        )
                    },
                    contentDescription = "",
                    modifier = Modifier.fillMaxWidth()


                )
            }


        }
        "gif" -> {
            val context = applicationViewModel.application
            val imageLoader = ImageLoader.Builder(context)
                .components {
                    if (Build.VERSION.SDK_INT >= 28) {
                        add(ImageDecoderDecoder.Factory())
                    } else {
                        add(GifDecoder.Factory())
                    }
                }
                .build()
            val painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(applicationViewModel.application)
                    .data(messages.messageFile)
                    .size(Size.ORIGINAL)
                    .crossfade(true)
                    .build(),
                imageLoader = imageLoader,

                )

            MessageRow(messages, authViewModel) {
                val state = painter.state
                if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
                    Image(

                        painter = painterResource(id = R.drawable.image_place_holder),
                        contentDescription = "",
                        contentScale = ContentScale.FillWidth

                    )
                }
                Image(
                    painter = painter,
                    contentDescription = null,

                    )

            }


        }
        "document", "pdf", "audio" -> {
            MessageRow(messages, authViewModel) {
                Row(
                    Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp).clickable {
                       onFileClick(
                           chatPageViewModel.messagesState.value.messages[index].messageFile.toString(),
                           chatPageViewModel.messagesState.value.messages[index].messageText.toString()
                       )
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.AttachFile, "AttachFile")
                    Text(
                        text = messages.messageText.toString(),
                        style = if (messages.messageSenderId == authViewModel.getCurrentUser()!!.uid)
                            TextStyle(color = Color.White)
                        else
                            TextStyle(color = Color.Black)
                    )
                }
            }
        }
        "video" -> {
            val context = LocalContext.current
            val exoPlayer = remember(context) {
                ExoPlayer.Builder(context).build().apply {
                    setMediaItem(MediaItem.fromUri(messages.messageFile.toString()))
                }
            }


            ComposableLifecycle { _, event ->
                if (event == Lifecycle.Event.ON_PAUSE) {
                    exoPlayer.pause()
                } else if (event == Lifecycle.Event.ON_RESUME) {
                    exoPlayer.play()
                }
            }

            MessageRow(messages, authViewModel) {

                AndroidView(modifier = Modifier
                    .fillMaxWidth()
                    .height(214.dp), factory = {
                    if (exoPlayer.isPlaying)
                        exoPlayer.pause()
                    StyledPlayerView(context).apply {
                        player = exoPlayer


                    }
                })
            }
        }
        "record" -> {
            if (!myDir.exists())
                myDir.mkdirs()
            val list = myDir.listFiles()
            MessageRow(messages, authViewModel) {
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,

                    ){
                    Surface(
                        onClick = {
                            chatPageViewModel.selectedIndex.value = index
                            voiceMessage(
                                list,
                                messages,
                                index,
                                chatPageViewModel,
                                applicationViewModel
                            )
                        },
                        color = Color.Transparent
                    ) {
                        if(chatPageViewModel.selectedIndex.value != index) {
                            Icon(
                                imageVector = Icons.Default.PlayCircle,
                                contentDescription = "",
                                Modifier.size(30.dp)
                            )

                        }
                        else
                            Icon(
                                imageVector = Icons.Default.PauseCircle,
                                contentDescription = "",
                                Modifier.size(30.dp)
                            )

                    }
                    if(chatPageViewModel.selectedIndex.value == index)
                        Slider(
                            value = chatPageViewModel.sliderPosition.value,
                            valueRange = 0.0f..maxVal.toFloat(),
                            onValueChange = { chatPageViewModel.sliderPosition.value = it })
                    else
                        Slider(
                            value = 0f,
                            valueRange = 0.0f..0f,
                            onValueChange = { })
                }
                if(chatPageViewModel.selectedIndex.value == index)
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if(minVal =="0") Text(text = "00:00") else Text(text = minVal)
                        ShowVoiceDuration(messages, applicationViewModel)
                    }
                else
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "00:00")
                        ShowVoiceDuration(messages, applicationViewModel)

                    }


            }




        }
    }
}

@Composable
private fun ShowVoiceDuration(
    messages: ChatModel,
    applicationViewModel: ApplicationViewModel
) {
    if (hasPermissions(applicationViewModel.application, storagePermissions)) {
        if (File("$root/Bego Chat/Bego Recorders/${messages.messageText}").exists())
            Text(
                text = getDuration(
                    "$root/Bego Chat/Bego Recorders/${messages.messageText}",
                    applicationViewModel.application,
                null).toString()
            )
        else
            Text(
                text = getDuration(
                    "$root/Bego Chat/Bego Recorders/${messages.messageText}",
                    applicationViewModel.application,
                    messages.messageFile).toString()
            )
    }

    else
        Text(
            text = getDuration(
                "$root/Bego Chat/Bego Recorders/${messages.messageText}",
                applicationViewModel.application,
                messages.messageFile).toString()
        )
}

