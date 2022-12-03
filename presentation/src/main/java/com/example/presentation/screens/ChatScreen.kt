@file:Suppress("UNREACHABLE_CODE")

package com.example.presentation.screens


import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
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
import com.example.presentation.components.ChatScreenBottom
import com.example.presentation.components.ChatTopBar
import com.example.presentation.components.MessageRow
import com.example.presentation.ui.view_models.ApplicationViewModel
import com.example.presentation.ui.view_models.AuthViewModel
import com.example.presentation.ui.view_models.ChatPageViewModel
import com.example.presentation.ui.view_models.HomePageViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView

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

@Composable
private fun BuildChatScreenContent(
    chatPageViewModel: ChatPageViewModel,
    index: Int,
    textMessage: ChatModel,
    authViewModel: AuthViewModel,
    applicationViewModel: ApplicationViewModel = hiltViewModel()
) {
    when (chatPageViewModel.messagesState.value.messages[index].messageType) {
        "text" -> {
            MessageRow(textMessage, authViewModel) {
                Text(
                    text = textMessage.messageText.toString(),
                    if (textMessage.messageSenderId == authViewModel.getCurrentUser()!!.uid)
                        Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp)
                    else
                        Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp),
                    style = if (textMessage.messageSenderId == authViewModel.getCurrentUser()!!.uid)
                        TextStyle(color = Color.White)
                    else
                        TextStyle(color = Color.Black)
                )
            }

        }
        "image" -> {
            MessageRow(textMessage, authViewModel) {

                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(applicationViewModel.application)
                        .data(textMessage.messageFile)
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
                    if (SDK_INT >= 28) {
                        add(ImageDecoderDecoder.Factory())
                    } else {
                        add(GifDecoder.Factory())
                    }
                }
                .build()
            val painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(applicationViewModel.application)
                    .data(textMessage.messageFile)
                    .size(Size.ORIGINAL)
                    .crossfade(true)
                    .build(),
                imageLoader = imageLoader,

                )

            MessageRow(textMessage, authViewModel) {
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
            MessageRow(textMessage, authViewModel) {
                Row(
                    if (textMessage.messageSenderId == authViewModel.getCurrentUser()!!.uid)
                        Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp)
                    else
                        Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.AttachFile, "AttachFile")
                    Text(
                        text = textMessage.messageText.toString(),
                        style = if (textMessage.messageSenderId == authViewModel.getCurrentUser()!!.uid)
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
                    setMediaItem(MediaItem.fromUri(textMessage.messageFile.toString()))
                }
            }


            ComposableLifecycle { _, event ->
                if (event == Lifecycle.Event.ON_PAUSE) {
                    exoPlayer.pause()
                } else if (event == Lifecycle.Event.ON_RESUME) {
                    exoPlayer.play()
                }
            }

            MessageRow(textMessage, authViewModel) {

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
    }
}


@Composable
fun ComposableLifecycle(
    lifeCycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onEvent: (LifecycleOwner, Lifecycle.Event) -> Unit
) {
    DisposableEffect(lifeCycleOwner) {
        val observer = LifecycleEventObserver { source, event ->
            onEvent(source, event)
        }
        lifeCycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifeCycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

