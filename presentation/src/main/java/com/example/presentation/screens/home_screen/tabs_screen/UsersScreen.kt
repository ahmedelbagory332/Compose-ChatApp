
package com.example.presentation.screens.home_screen.tabs_screen


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
 import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.example.domain.model.UsersModel
import com.example.presentation.R
import com.example.presentation.components.LoadingIndicator
import com.example.presentation.components.NoDataFound
import com.example.presentation.components.UserRow
import com.example.presentation.theme.*
import com.example.presentation.ui.view_models.ApplicationViewModel
import com.example.presentation.ui.view_models.AuthViewModel
import com.example.presentation.ui.view_models.HomePageViewModel
import com.example.presentation.utiles.Routes


@Composable
fun UsersScreen(
    mainNavController: NavHostController,
    allUsers:List<UsersModel>,
    homePageViewModel: HomePageViewModel = hiltViewModel(),
    authViewModel: AuthViewModel =  hiltViewModel(),
    applicationViewModel: ApplicationViewModel =  hiltViewModel(),
    ) {

    ComposeChatAppTheme {
        if (homePageViewModel.usersState.value.isLoading) {
            LoadingIndicator()
        }
        else  if (homePageViewModel.usersState.value.error.isNotEmpty()) {
            NoDataFound(
                title = "No Result Found",
                image =  painterResource(id = R.drawable.no_result)
            )
        }
        else  if (allUsers.isNotEmpty()){
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .background(darkWhite)
                        ){
                items(allUsers){ users ->

                    UserRow(users, onUserClick = { user ->
                         if(authViewModel.getCurrentUser()!!.uid == user.userId){
                            Toast.makeText(applicationViewModel.application,"you can't send message to yourself",Toast.LENGTH_SHORT).show()
                        }else
                        mainNavController.navigate(route = Routes().chatScreen + "/${user.userId}") {
                            launchSingleTop = true

                        }
                    })
                }
            }

        }




    }


}



