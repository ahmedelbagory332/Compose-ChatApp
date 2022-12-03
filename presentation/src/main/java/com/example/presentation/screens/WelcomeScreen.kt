@file:OptIn(ExperimentalAnimationApi::class)

package com.example.presentation.screens


import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.background
import com.example.presentation.R
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.presentation.ui.MainActivity
import com.example.presentation.components.AppLogo
import com.example.presentation.theme.*


@Composable
fun WelcomeScreen(mainActivity: MainActivity) {

    val visibleState = remember { MutableTransitionState(false) }
    visibleState.targetState = true
    mainActivity.window.statusBarColor = ContextCompat.getColor(mainActivity,R.color.purple_500)

    ComposeChatAppTheme {
        Column(
           modifier = Modifier
               .fillMaxWidth()
               .fillMaxHeight().background(welcomeScreenColor),
           verticalArrangement = Arrangement.Center
            ) {

            AnimatedVisibility(visibleState,
                enter = slideInVertically(
                    initialOffsetY = { 1000 }
                ) + scaleIn(initialScale = 0.10f),
                exit =  scaleOut()) {
                    AppLogo(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 50.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        logoName = "Bego Chat",
                        logoNameStyle = TextStyle(color = Color.White,fontSize = 30.sp),
                        logoImage = painterResource(id = R.drawable.logo),
                        logoImageModifier = Modifier
                            .size(120.dp, 120.dp)
                            .padding(end = 5.dp)
                    )
                }






        }
    }
}

