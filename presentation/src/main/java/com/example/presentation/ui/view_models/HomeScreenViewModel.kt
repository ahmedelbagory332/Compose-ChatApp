package com.example.presentation.ui.view_models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.lifecycle.ViewModel
import com.example.domain.model.BottomBarModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class HomeScreenViewModel  @Inject constructor() : ViewModel(){





    val bottomNavigationItems: List<BottomBarModel> = listOf(
        BottomBarModel(title = "Home", icon = Icons.Default.Home ,  route = "homePage"),
        BottomBarModel(title = "Profile", icon = Icons.Default.Person,  route = "profilePage"),
        BottomBarModel(title = "Settings", icon = Icons.Default.Settings ,  route = "settingsPage")
    )

}
