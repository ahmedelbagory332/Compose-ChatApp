package com.example.presentation.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.domain.model.ChatModel
import com.example.presentation.theme.Purple200
import com.example.presentation.theme.darkWhite
import com.example.presentation.ui.view_models.AuthViewModel
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MessageRow(
    textMessage: ChatModel,
    authViewModel: AuthViewModel,
    messageContent: @Composable() () -> Unit,
) {
    if (textMessage.messageSenderId == authViewModel.getCurrentUser()!!.uid) {

        Column(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(start = 60.dp, end = 8.dp, top = 2.dp, bottom = 2.dp),
            horizontalAlignment = Alignment.End,
        ) {
            Column(
                Modifier
                    .clip(RoundedCornerShape(topEnd = 15.dp, topStart = 15.dp, bottomStart = 15.dp))
                    .background(Purple200),
            ) {

                messageContent()

                Text(
                    text =
                    if (textMessage.timestamp == null)
                        SimpleDateFormat(
                            "dd MMM yyyy hh:mm a",
                            Locale.getDefault()
                        ).format(Calendar.getInstance().time)
                    else
                        getActualTime(textMessage.timestamp as Timestamp),
                    Modifier.padding(10.dp),
                    style = TextStyle(color = Color.White, textAlign = TextAlign.End)

                )
            }

        }
    } else {
        Column(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(end = 60.dp, start = 8.dp, top = 2.dp, bottom = 2.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Column(
                Modifier
                    .clip(
                        RoundedCornerShape(
                            topEnd = 15.dp,
                            topStart = 15.dp,
                            bottomEnd = 15.dp
                        )
                    )
                    .background(darkWhite),
            ) {

                messageContent()

                Text(
                    text =
                    if (textMessage.timestamp == null)
                        SimpleDateFormat(
                            "dd MMM yyyy hh:mm a",
                            Locale.getDefault()
                        ).format(Calendar.getInstance().time)
                    else
                        getActualTime(textMessage.timestamp as Timestamp),
                    Modifier.padding(10.dp),
                    style = TextStyle(color = Color.Black)

                )
            }

        }
    }
}


@SuppressLint("SimpleDateFormat")
fun getActualTime(timestamp: Timestamp): String =

    SimpleDateFormat("dd MMM yyyy hh:mm a")
        .format(Date(timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000))
        .toString()


