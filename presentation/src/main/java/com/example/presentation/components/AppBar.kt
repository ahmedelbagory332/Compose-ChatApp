package com.example.presentation.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
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
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.SubcomposeAsyncImage
import com.example.domain.model.UsersModel
import com.example.presentation.theme.Purple500
import com.example.presentation.ui.view_models.HomePageViewModel
import com.example.presentation.utiles.Routes


enum class TabPages{
    ChatsTab , UsersTab
}


@SuppressLint("SuspiciousIndentation")
@Composable
fun DefaultAppBar(
    tabsNavController: NavHostController?,
    homePageViewModel: HomePageViewModel = hiltViewModel(),
    actions: @Composable (RowScope.() -> Unit),
    title: @Composable () -> Unit
) {

    if (tabsNavController == null)
        TopAppBar(
            title = title,
            actions = actions,
        )
    else {
        val navBackStackEntry by tabsNavController.currentBackStackEntryAsState()
        val currentRoute  = navBackStackEntry?.destination?.route
        var tabPage = if (currentRoute == Routes().usersTabRoute) TabPages.UsersTab else TabPages.ChatsTab
        Column {
            TopAppBar(
                title = title,
                actions = actions,
            )
            HomeTabRow(
                tabPage = tabPage,
                screens = homePageViewModel.screens.value,
                onTabSelected = {
                    tabPage = it
                    if (tabPage == TabPages.ChatsTab)

                    tabsNavController.navigate(Routes().chatsTabRoute) {
                        launchSingleTop = true
                         popUpTo(tabsNavController.graph.findStartDestination().id)

                    }
                    else
                    tabsNavController.navigate(Routes().usersTabRoute) {
                        launchSingleTop = true
                          popUpTo(tabsNavController.graph.findStartDestination().id)

                    }
                }
            )
        }
    }}

@Composable
fun SearchAppBar(
    modifier : Modifier,
    value: String,
    onValueChange:(String)->Unit,
    placeholder: @Composable()()->Unit,
    trailingIcon: @Composable() () -> Unit,
) {

    SearchTextField(
        modifier = modifier
            .fillMaxWidth()
            .background(Purple500),
        value = value,
        onValueChange = onValueChange ,
        placeholder = placeholder,
        trailingIcon = trailingIcon
    )
}


@SuppressLint("SimpleDateFormat")
@Composable
fun ChatTopBar(user: UsersModel, onUserClick:(user: UsersModel)->Unit)  {
    TopAppBar {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 5.dp).clickable {
                onUserClick(user)
            },

            ) {


            SubcomposeAsyncImage(
                model = user.imageProfile,
                loading = {
                    CircularProgressIndicator()
                },
                error = {
                    Image(

                        painter = painterResource(id = com.example.presentation.R.drawable.profile_avatar),
                        contentDescription = "",
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(50.dp),
                    )
                },
                contentDescription = "",
                modifier = Modifier
                    .clip(CircleShape)
                    .size(50.dp),
                contentScale = ContentScale.Crop,

                )

            Column() {
                Text(text = "${user.name}", style = TextStyle(color = Color.White))

                if (user.lastSeen!=null&& user.type.toString() == "offline") {

                     Text(text =   getActualTime( user.lastSeen as com.google.firebase.Timestamp), style = TextStyle(color = Color.White))
                }else{
                    Text(text = "${user.type}", style = TextStyle(color = Color.White))

                }
            }


        }
    }

}

