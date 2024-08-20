package com.allyouraffle.allyouraffle.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.allyouraffle.allyouraffle.android.detail.RaffleDetail
import com.allyouraffle.allyouraffle.android.login.LoginPage
import com.allyouraffle.allyouraffle.android.raffle.RaffleListScreen
import com.allyouraffle.allyouraffle.exception.DetailServiceException
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
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            // 예외 처리 로직
            println("Uncaught exception in thread ${thread.name}: $throwable")
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
fun MainPage() {
    val navController = rememberNavController()
    val freeRaffleViewModel = RaffleViewModel()
    val noFreeRaffleViewModel = RaffleViewModel()
    BottomNav(navController, freeRaffleViewModel, noFreeRaffleViewModel)
}

@Composable
private fun BottomNav(
    navController: NavHostController,
    freeRaffleViewModel: RaffleViewModel,
    noFreeRaffleViewModel: RaffleViewModel
) {
    val startDestination = "광고 래플"
    val items = listOf(
        Triple("광고 래플", R.drawable.ic_ad_non_click, R.drawable.ic_ad_click),
        Triple("천원 래플", R.drawable.ic_shop_non_click, R.drawable.ic_shop_click),
        Triple("마이 페이지", R.drawable.ic_user_non_click, R.drawable.ic_user_click)
    )
    var selectedItem by remember { mutableStateOf(startDestination) }
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute in listOf("raffle/{itemId}/{isFree}")) return@Scaffold

            BottomNavigation(
                contentColor = MaterialTheme.colorScheme.tertiary,
                backgroundColor = Color.White,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp, 10.dp))
                    .border(BorderStroke(0.5.dp, Color.LightGray)),
                elevation = 10.dp
            ) {
                items.forEach { (screen, icon, selectedIcon) ->
                    BottomNavigationItem(
                        label = {
                            Text(
                                screen,
                                fontSize = 10.sp,
                                color = if (selectedItem == screen) MaterialTheme.colorScheme.tertiary else Color.LightGray
                            )
                        },
                        icon = {
                            Icon(
                                painter = painterResource(if (selectedItem == screen) icon else selectedIcon),
                                contentDescription = null
                            )
                        },
                        selected = selectedItem == screen,
                        onClick = {
                            selectedItem = screen
                            navController.navigate(screen) {
                                popUpTo(screen) { inclusive = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        selectedContentColor = MaterialTheme.colorScheme.tertiary,
                        unselectedContentColor = Color.LightGray,
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = startDestination,
            Modifier.padding(innerPadding)
        ) {
            composable("광고 래플") { RaffleListScreen(freeRaffleViewModel, navController, true) }
            composable("천원 래플") {
                RaffleListScreen(
                    noFreeRaffleViewModel,
                    navController,
                    isFree = false
                )
            }
            composable("마이 페이지") { Third() }
            composable("raffle/{itemId}/{isFree}") { backStackEntry ->
                val itemId =
                    backStackEntry.arguments?.getString("itemId") ?: throw DetailServiceException()
                val isFree = (backStackEntry.arguments?.getString("isFree")
                    ?: throw DetailServiceException()).toBoolean()
                RaffleDetail(navController, itemId, isFree)
            }
        }

        val context = LocalContext.current
        BackHandler(enabled = true) {
            val noFinishRouteList = listOf("raffle/{itemId}/{isFree}")
            if (currentRoute !in noFinishRouteList) {
                (context as ComponentActivity).finish()
            } else {
                navController.popBackStack()
            }
        }
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
        RaffleListScreen(RaffleViewModel(), rememberNavController(), true)
    }
}
