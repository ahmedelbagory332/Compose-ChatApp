
package com.example.presentation.screens.home_screen.bottom_navigation_screen


import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.presentation.R
import com.example.presentation.components.DefaultAppBar
import com.example.presentation.components.SearchAppBar
import com.example.presentation.theme.*
import com.example.presentation.ui.view_models.AuthViewModel
import com.example.presentation.utiles.Routes


@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview
fun ProfilePage(
    authViewModel: AuthViewModel =  hiltViewModel(),

    ) {


    ComposeChatAppTheme {
        Scaffold(
            topBar = {
                     DefaultAppBar(
                         tabsNavController = null,
                         actions ={},
                         title = { Text(text = "Home") },
                     )
            },
            ){
            Column(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight().padding(top = 10.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(

                    painter = painterResource(id = R.drawable.profile_avatar),
                    contentDescription = "",
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(200.dp),
                )
                ListItem(
                    text = { Text(text = "Name ")},
                    secondaryText = { Text(text = "${authViewModel.getCurrentUser()!!.displayName}")},
                    icon = {Icon(
                        Icons.Filled.Person,
                        contentDescription = null
                    )}
                )
                Divider(startIndent = 75.dp)
                ListItem(
                    text = { Text(text = "Email ")},
                    secondaryText = { Text(text = "${authViewModel.getCurrentUser()!!.email}")},
                    icon = {Icon(
                        Icons.Filled.Email,
                        contentDescription = null
                    )}
                )
            }
        }
    }
}

