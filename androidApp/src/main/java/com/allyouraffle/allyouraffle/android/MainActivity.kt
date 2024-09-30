package com.allyouraffle.allyouraffle.android

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.allyouraffle.allyouraffle.android.firebase.TicketPlusEvent
import com.allyouraffle.allyouraffle.android.home.AdViewModel
import com.allyouraffle.allyouraffle.android.home.HomeScreen
import com.allyouraffle.allyouraffle.android.login.LoginPage
import com.allyouraffle.allyouraffle.android.login.LoginViewModel
import com.allyouraffle.allyouraffle.android.mypage.MyPageScreen
import com.allyouraffle.allyouraffle.android.permission.NotificationPermissionRequest
import com.allyouraffle.allyouraffle.android.raffle.RaffleListScreen
import com.allyouraffle.allyouraffle.android.util.LoadingScreen
import com.allyouraffle.allyouraffle.android.util.SharedPreference
import com.allyouraffle.allyouraffle.exception.DetailServiceException
import com.allyouraffle.allyouraffle.exception.JwtException
import com.allyouraffle.allyouraffle.exception.NetworkException
import com.allyouraffle.allyouraffle.exception.NotificationException
import com.allyouraffle.allyouraffle.network.LoginApi
import com.allyouraffle.allyouraffle.viewModel.HomeViewModel
import com.allyouraffle.allyouraffle.viewModel.MyPageViewModel
import com.allyouraffle.allyouraffle.viewModel.RaffleViewModel
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    lateinit var exceptionHandler: CoroutineExceptionHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this)
        FirebaseApp.initializeApp(this)
        createNotificationChannel()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.onPrimary
                ) {
                    NotificationPermissionRequest()
                    var isSplashVisible by remember { mutableStateOf(true) }

                    LaunchedEffect(Unit) {
                        // 3초 동안 초기화 작업을 진행
                        delay(2200)
                        isSplashVisible = false
                    }
                    MyApp()
                    if (isSplashVisible) {
                        SplashScreen()
                    }

                }
            }
        }


        exceptionHandler = CoroutineExceptionHandler { _, exception ->
            when (exception) {
                is JwtException -> {
                    val sharedPreference = SharedPreference(this)
                    val jwt = LoginApi.refresh(sharedPreference.getRefreshToken())
                    if (jwt == null) {
                        Toast.makeText(this, "앱을 재시작해주세요.", Toast.LENGTH_SHORT).show()
                    } else {
                        SharedPreference(this).setJwt(jwt.jwt)
                        Toast.makeText(this, "다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }

                is NetworkException -> {
                    Toast.makeText(this, "네트워크 에러가 발생했습니다.", Toast.LENGTH_LONG).show()
                }

                else -> {
                    println("예상치 못한 오류가 발생했습니다. 앱을 재시작해주세요.")
                }
            }
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "channelId",
            "Default Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "This is the default notification channel."
        }

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
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
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        // 스플래시 화면 콘텐츠
        LoadingScreen()
    }
}

@Composable
fun MainPage() {
    val context = LocalContext.current
    val loginViewModel = remember { LoginViewModel() }
    val sharedPreference = SharedPreference(context)

    LaunchedEffect(Unit) {
        while (true) {
            // 특정 함수 실행
            val refreshToken = sharedPreference.getRefreshToken()
            val jwtResponse = loginViewModel.refresh(refreshToken)?:continue
            sharedPreference.setJwt(jwtResponse.jwt)
            // 5분 대기 (5분 = 5 * 60 * 1000ms)
            delay(5 * 60 * 1000)
        }
    }
    val navController = rememberNavController()
    BottomNav(navController)
}

@Composable
private fun BottomNav(
    navController: NavHostController,
) {
    val startDestination = "홈"
    val items = listOf(
        Triple("홈", R.drawable.ic_home_non_click, R.drawable.ic_home_click),
        Triple("광고 래플", R.drawable.ic_ad_non_click, R.drawable.ic_ad_click),
//        Triple("천원 래플", R.drawable.ic_shop_non_click, R.drawable.ic_shop_click),
        Triple("마이 페이지", R.drawable.ic_user_non_click, R.drawable.ic_user_click)
    )
    var selectedItem by rememberSaveable { mutableStateOf(startDestination) }
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    var backPressedTime by remember { mutableLongStateOf(0L) }
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier.background(color = MaterialTheme.colorScheme.onPrimary),
        bottomBar = {
            if (currentRoute in listOf("raffle/{itemId}/{isFree}","ticketPlusEvent/{reward}")) return@Scaffold

            BottomNavigation(
                contentColor = MaterialTheme.colorScheme.tertiary,
                backgroundColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .clip(RoundedCornerShape(10.dp, 10.dp)),
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
                        enabled = selectedItem != screen,
                        selectedContentColor = MaterialTheme.colorScheme.tertiary,
                        unselectedContentColor = Color.LightGray,
                    )
                }
            }
        }
    ) { innerPadding ->
        val freeViewModel = remember { RaffleViewModel() }
        val noFreeViewModel = remember {
            RaffleViewModel()
        }
        val homeViewModel = remember { HomeViewModel() }
        val adViewModel = remember{AdViewModel()}
        val myPageViewModel = remember { MyPageViewModel() }
        NavHost(
            navController,
            startDestination = startDestination,
            Modifier.padding(innerPadding)
        ) {
            composable("홈") { HomeScreen(homeViewModel,adViewModel, navController) }
            composable("광고 래플") { RaffleListScreen(navController, true, freeViewModel) }
//            composable("천원 래플") {
//                RaffleListScreen(
//                    navController,
//                    isFree = false, noFreeViewModel
//                )
//            }
            composable("마이 페이지") { MyPageScreen(myPageViewModel) }
            composable("raffle/{itemId}/{isFree}") { backStackEntry ->
                val itemId =
                    backStackEntry.arguments?.getString("itemId") ?: throw DetailServiceException()
                val isFree = (backStackEntry.arguments?.getString("isFree")
                    ?: throw DetailServiceException()).toBoolean()
                RaffleDetail(navController, itemId, isFree)
            }
            composable("ticketPlusEvent/{reward}"){backStackEntry ->
                val reward =
                    backStackEntry.arguments?.getString("reward") ?: throw NotificationException()

                val rewardInt = reward.toIntOrNull()?.run {
                    TicketPlusEvent(navController,this)
                }
            }
            composable("test2"){
                test2()
            }
        }

        BackHandler(enabled = true) {
            val noFinishRouteList = listOf("raffle/{itemId}/{isFree}")
            if (currentRoute !in noFinishRouteList) {
                val currentTime = SystemClock.elapsedRealtime()
                println(currentTime)
                if (currentTime - backPressedTime < 2000) {
                    (context as ComponentActivity).finish()
                } else {
                    backPressedTime = currentTime
                    Toast.makeText(context, "뒤로가기를 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
                }

            } else {
                navController.popBackStack()
            }
        }
        LaunchedEffect(Unit) {
            val activity = context as? Activity
            // Activity의 Intent에서 데이터를 가져옴
            val intent = activity?.intent
            // FCM 메시지에 따라 특정 화면으로 이동 처리
            intent?.let {
                val targetScreen = it.getStringExtra("notification")
                val reward = it.getStringExtra("reward")
                targetScreen?.let { screen ->
                    handleNotificationNavigation(screen,reward,navController)
                }
            }
        }

    }

}
private fun handleNotificationNavigation(screen: String,reward : String?,navController: NavHostController) {
    // targetScreen에 따라 특정 화면으로 네비게이트
    when (screen) {
        "ticketPlusEvent" -> {
                navController.navigate("ticketPlusEvent/$reward"){
            }
        }
        "test2" -> {
            navController.navigate("test2")
        }
        else -> {
            Log.d("qwert","기타등등")
        }
    }
}

@Composable
fun test1(){
    Text("TEST1")
}

@Composable
fun test2(){
    Text("TEST2")
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        RaffleListScreen(rememberNavController(), true, RaffleViewModel())
    }
}
