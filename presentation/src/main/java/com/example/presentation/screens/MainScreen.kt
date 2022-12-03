package com.example.presentation.screens



import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.presentation.ui.MainActivity
import com.example.presentation.theme.ComposeChatAppTheme
import com.example.presentation.ui.view_models.AuthViewModel
import com.example.presentation.utiles.Routes
import kotlinx.coroutines.delay

@Composable
fun MainScreen(
    mainActivity: MainActivity,
    navController: NavHostController,
    authViewModel: AuthViewModel =  hiltViewModel(),
) {

        ComposeChatAppTheme {

           if(authViewModel.getCurrentUser() == null){
               WelcomeScreen(mainActivity)
               LaunchedEffect(true) {
                   delay(1000)
                   navController.navigate(Routes().loginRoute){
                       launchSingleTop = true
                       restoreState = true
                       popUpTo(this.popUpToId){
                           saveState = true
                       }
                       navController.popBackStack()

                   }
               }
           }else{
               navController.navigate(Routes().homeRoute){
                   launchSingleTop = true
                   restoreState = true
                   popUpTo(this.popUpToId){
                       saveState = true
                   }
                   navController.popBackStack()

               }
           }
    }
}

