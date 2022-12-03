package com.example.presentation.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

@Composable
fun AuthCard(
    modifier : Modifier,
    shape: RoundedCornerShape,
    elevation : Dp,
    backgroundColor : Color,
    content: @Composable() (() -> Unit)


){
    Card(
        backgroundColor = backgroundColor,
        modifier = modifier,
        shape = shape,
        elevation = elevation,
        content = content
    )
}


