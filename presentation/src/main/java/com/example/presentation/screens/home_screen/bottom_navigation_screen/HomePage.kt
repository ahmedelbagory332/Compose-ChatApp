package com.example.presentation.screens.home_screen.bottom_navigation_screen


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.presentation.components.*
import com.example.presentation.screens.home_screen.tabs_screen.ChatsScreen
import com.example.presentation.screens.home_screen.tabs_screen.UsersScreen
import com.example.presentation.theme.*
import com.example.presentation.ui.view_models.AuthViewModel
import com.example.presentation.ui.view_models.HomePageViewModel
import com.example.presentation.utiles.Routes


@Composable
fun HomePage(
    mainNavController: NavHostController,
    homePageViewModel: HomePageViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()

    ) {
    val tabsNavController = rememberNavController()
    val navBackStackEntry by tabsNavController.currentBackStackEntryAsState()
    val currentRoute  = navBackStackEntry?.destination?.route

    ComposeChatAppTheme {
        Scaffold(
            topBar = {
                if (homePageViewModel.searchWidgetState.value){

                    SearchAppBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Purple500),
                        value = homePageViewModel.searchTextState.value,
                        onValueChange = {
                            homePageViewModel.updateSearchTextState(it)

                            when (currentRoute) {
                                Routes().chatsTabRoute -> {

                                    Log.d("tab search" , "you search on ${homePageViewModel.searchTextState.value} in chat tab")

                                }
                                Routes().usersTabRoute -> {
                                    Log.d("tab search" , "you search on ${homePageViewModel.searchTextState.value} in users tab")

                                }

                            }

                        },
                        placeholder = {
                            Text(
                                text = "type the name to search for...",
                                color = Color.White
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    if (homePageViewModel.searchTextState.value.isNotEmpty()) {
                                        homePageViewModel.updateSearchTextState("")
                                    } else {
                                        homePageViewModel.updateSearchWidgetState(false)


                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close Icon",
                                    tint = Color.White
                                )
                            }
                        },
                    )
                }
                else{

                    DefaultAppBar(
                        tabsNavController = tabsNavController,
                        actions = {
                            IconButton(onClick = {
                                homePageViewModel.updateSearchWidgetState(true)

                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Search,
                                    contentDescription = "search",
                                    tint = Color.White
                                )
                            }
                            IconButton(onClick = {
                                authViewModel.userLogOut()
                                mainNavController.navigate(Routes().loginRoute) {
                                     popUpTo(Routes().loginRoute) {
                                         mainNavController.popBackStack()
                                     }

                                }

                            }) {
                                Icon(
                                    imageVector = Icons.Filled.ExitToApp,
                                    contentDescription = "log out",
                                    tint = Color.White

                                )
                            }
                        },
                        title = { Text(text = "Home") },

                        )
                }

            },


        ) {
            NavHost(
                navController = tabsNavController,
                startDestination = Routes().chatsTabRoute
            ) {
                composable(route = Routes().chatsTabRoute) {
                    ChatsScreen(mainNavController)

                }
                composable(route = Routes().usersTabRoute) {
                    UsersScreen(mainNavController)

                }


            }
        }
    }
}




