package com.example.presentation.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TabPosition
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.presentation.theme.Purple500

@Composable
 fun HomeTabRow(
    tabPage: TabPages,
    screens: List<String>,
    onTabSelected:(tabPage: TabPages)->Unit

) {
    TabRow(
        selectedTabIndex = tabPage.ordinal,
        backgroundColor = Purple500,
        indicator = { HomeTabIndicator(tabPositions = it, tabPage = tabPage) }
    ) {
        screens.forEachIndexed { index, text ->
            val selected = tabPage == TabPages.values()[index]
            Box(
                modifier = Modifier.clickable {
                    onTabSelected(TabPages.values()[index])
                },
                contentAlignment = Alignment.Center

            ){
                Text(
                    modifier = Modifier.padding(15.dp),
                    text = text,
                    color = if (selected) Color.White else Color.LightGray,
                 )
            }
        }
    }
}


@Composable
private fun HomeTabIndicator(
    tabPositions: List<TabPosition>,
    tabPage: TabPages
) {
    val transition = updateTransition(tabPage, label = "Tab indicator")
    val indicatorLeft by transition.animateDp(
        transitionSpec  = {
            if(TabPages.ChatsTab isTransitioningTo TabPages.UsersTab){
                spring(stiffness = Spring.StiffnessLow)

            }else{
                spring(stiffness = Spring.StiffnessMedium)

            }
        },
        label = "Indicator left") { page ->
        tabPositions[page.ordinal].left
    }
    val indicatorRight by transition.animateDp(
        transitionSpec  = {
            if(TabPages.ChatsTab isTransitioningTo TabPages.UsersTab){
                spring(stiffness = Spring.StiffnessMedium)

            }else{
                spring(stiffness = Spring.StiffnessLow)

            }
        },label = "Indicator right") { page ->
        tabPositions[page.ordinal].right
    }

    Box(
        Modifier
            .fillMaxSize()
            .wrapContentSize(align = Alignment.BottomStart)
            .offset(x = indicatorLeft)
            .width(indicatorRight - indicatorLeft)
            .padding(4.dp)
            .fillMaxSize()
            .border(
                BorderStroke(2.dp, Color.White),
                RoundedCornerShape(4.dp)
            )
    )
}
