package com.example.presentation.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.domain.model.UsersModel
import com.example.presentation.R
import com.example.presentation.ui.view_models.AuthViewModel


@SuppressLint("SuspiciousIndentation")
@Composable
fun ChatRow(
    userName: String,
    message: String,
    time: String,
    onChatClick: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    Card(
        elevation = 3.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(5.dp))
            .padding(3.dp)
            .clickable {
                onChatClick()
            },

        ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,

            ){

            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 5.dp).weight(1F),

                ) {


                Image(

                    painter = painterResource(id = R.drawable.profile_avatar),
                    contentDescription = "",
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(80.dp),
                )

                Column(Modifier.padding(horizontal = 5.dp)) {
                    if (authViewModel.getCurrentUser() != null) {

                        Text(text = userName, style = TextStyle(color = Color.Black))
                        Text(text = message, style = TextStyle(color = Color.Gray))
                    }
                }


            }


            Text(text = time, style = TextStyle(color = Color.Black))

        }
    }
}

