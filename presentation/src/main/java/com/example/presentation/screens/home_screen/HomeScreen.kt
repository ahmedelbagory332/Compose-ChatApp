
package com.example.presentation.screens.home_screen


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.presentation.components.BottomBar
import com.example.presentation.screens.home_screen.bottom_navigation_screen.HomePage
import com.example.presentation.screens.home_screen.bottom_navigation_screen.ProfilePage
import com.example.presentation.screens.home_screen.bottom_navigation_screen.SettingsPage
import com.example.presentation.theme.*
import com.example.presentation.utiles.Routes


@Composable
fun HomeScreen(mainNavController: NavHostController) {

      val bottomBarNavController = rememberNavController()

    ComposeChatAppTheme {
         Scaffold(
             bottomBar = {
                 BottomBar(bottomBarNavController = bottomBarNavController)
         }
         )
         { innerPadding->

             Box(modifier = Modifier.padding(0.dp, 0.dp, 0.dp, innerPadding.calculateBottomPadding())) {
                 NavHost(
                     navController = bottomBarNavController,
                     startDestination = Routes().homeButtonNavigationPageRoute
                 ) {
                     composable(route = Routes().homeButtonNavigationPageRoute) {
                         HomePage(mainNavController = mainNavController)
                     }

                     composable(route = Routes().profileButtonNavigationPageRoute) {
                         ProfilePage()

                     }
                     composable(route = Routes().settingsButtonNavigationPageRoute) {
                         SettingsPage()

                     }
                 }
             }
         }
    }
}




