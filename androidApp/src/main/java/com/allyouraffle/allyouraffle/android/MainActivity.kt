package com.allyouraffle.allyouraffle.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.twotone.Check
import androidx.compose.material.icons.twotone.Person
import androidx.compose.material.icons.twotone.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.allyouraffle.allyouraffle.android.raffle.RaffleListScreen
import com.allyouraffle.allyouraffle.viewModel.RaffleViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    MyApp()
                }
            }
        }
    }
}

@Composable
fun MyApp() {
    MaterialTheme {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "login") {
            composable("login") { LoginPage(navController) }
            composable("main") { MainPage() }
        }
    }
}

@Composable
fun LoginPage(navController: NavHostController) {
    Button(
        onClick = { navController.navigate("main") },
    ) {
        Text(text = "Login with Google")
    }
}

@Composable
fun MainPage() {
    val navController = rememberNavController()
    val viewModel = RaffleViewModel()
    viewModel.loadRaffles()
    Scaffold(
        bottomBar = {
            BottomNavigation(
                contentColor = MaterialTheme.colorScheme.tertiary,
                backgroundColor = Color.White,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp, 10.dp))
                    .border(BorderStroke(0.5.dp, Color.Black)),
                elevation = 10.dp
            ) {
                val items = listOf(
                    Triple("광고 래플", Icons.Default.Check, Icons.Filled.Check),
                    Triple("유료 래플", Icons.Default.ShoppingCart, Icons.Filled.ShoppingCart),
                    Triple("마이 페이지", Icons.Default.Person, Icons.Filled.Person)
                )
                val currentDestination = navController.currentDestination?.route

                items.forEach { (screen, icon,selectedIcon) ->
                        BottomNavigationItem(
                            label = { Text(screen, fontSize = 10.sp, color = Color.Black) },
                            icon = { Icon(if(currentDestination == screen) icon else selectedIcon, contentDescription = null) },
                            selected = currentDestination == screen,
                            onClick = {
                                navController.navigate(screen) {
                                    popUpTo(screen) { inclusive = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            selectedContentColor = MaterialTheme.colorScheme.tertiary,
                            unselectedContentColor = Color.LightGray,
//                        modifier = Modifier
//                            .clip(RoundedCornerShape(10.dp))

                        )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = "광고 래플", Modifier.padding(innerPadding)) {
            composable("광고 래플") { RaffleListScreen(viewModel) }
            composable("유료 래플") { Second() }
            composable("마이 페이지") { Third() }
        }
    }
}

@Composable
fun Second() {
    Button(onClick = { /*TODO*/ }) {
        Text(text = "Second")
    }
}

@Composable
fun Third() {
    Button(onClick = { /*TODO*/ }) {
        Text(text = "Third")
    }
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        RaffleListScreen(RaffleViewModel())
    }
}
