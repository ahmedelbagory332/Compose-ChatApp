package com.example.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppLogo(
    modifier: Modifier,
    verticalAlignment: Alignment.Vertical,
    horizontalArrangement: Arrangement.Horizontal,
    logoName: String,
    logoNameStyle:TextStyle,
    logoImage: Painter,
    logoImageModifier: Modifier

){
    Row (
        modifier = modifier,
        verticalAlignment = verticalAlignment,
        horizontalArrangement = horizontalArrangement,
    ){
        Image(
            painter = logoImage,
            contentDescription = "logo",
            modifier =  logoImageModifier
        )
        Text(
            text = logoName,
            style = logoNameStyle,
        )
    }
}