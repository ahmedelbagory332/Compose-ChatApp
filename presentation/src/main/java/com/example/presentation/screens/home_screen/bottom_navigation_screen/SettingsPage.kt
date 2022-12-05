
package com.example.presentation.screens.home_screen.bottom_navigation_screen


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.presentation.R
import com.example.presentation.components.DefaultAppBar
import com.example.presentation.theme.*


@Composable
@Preview
fun SettingsPage() {


    ComposeChatAppTheme {
        Scaffold(
            topBar = {
                DefaultAppBar(
                    tabsNavController = null,
                    actions ={},
                    title = { Text(text = "Settings") },
                )
            },
        ){
            Column(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight().padding(top = 10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Implement your Settings")
            }
        }
    }

}

