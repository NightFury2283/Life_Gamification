package com.example.life_gamification.presentation.nav

import com.example.life_gamification.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun BottomNavBar(navController: NavController, currentRoute: String?) {
    val items = listOf(
        BottomNavScreen.Status to R.drawable.ic_status,
        BottomNavScreen.Tasks to R.drawable.ic_calendar,
        BottomNavScreen.Shop to R.drawable.ic_magazine,
        BottomNavScreen.Inventory to R.drawable.ic_inventory,
        BottomNavScreen.Settings to R.drawable.ic_settings
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF4813B2))
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        items.forEach { (screen, iconRes) ->
            val selected = currentRoute == screen.route

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (selected) Color(0xFF270B54) else Color.Transparent)
                    .clickable {
                        if (!selected) {
                            navController.navigate(screen.route) {
                                popUpTo(BottomNavScreen.Status.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                    .padding(8.dp)
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = screen.route,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
