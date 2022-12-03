package com.example.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.presentation.ui.view_models.HomeScreenViewModel


@Composable
fun BottomBar(
    homeScreenViewModel: HomeScreenViewModel = hiltViewModel(),
    bottomBarNavController: NavHostController

) {
    val navBackStackEntry by bottomBarNavController.currentBackStackEntryAsState()
    val currentRoute  = navBackStackEntry?.destination?.route
    BottomNavigation(
        elevation = 10.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp, 15.dp, 0.dp, 0.dp))
    ) {

        homeScreenViewModel.bottomNavigationItems.forEach { bottomNavigationItem ->
            BottomNavigationItem(icon = {
                Icon(imageVector = bottomNavigationItem.icon as ImageVector, "")
            },
                label = { Text(text = bottomNavigationItem.title) },
                selected = (currentRoute == bottomNavigationItem.route),
                onClick = {

                    bottomBarNavController.navigate(bottomNavigationItem.route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(bottomBarNavController.graph.findStartDestination().id) {
                            saveState = true

                        }

                    }
                })
        }


    }
}

