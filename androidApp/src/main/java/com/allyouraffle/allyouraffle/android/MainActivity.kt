package com.allyouraffle.allyouraffle.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.allyouraffle.allyouraffle.model.RaffleResponse
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
            composable("main") { ProductListScreen() }
        }
    }
}

@Composable
fun LoginPage(navController: NavHostController) {
    Button(
        onClick = {navController.navigate("main")},
    ) {
        Text(text = "Login with Google")
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProductListScreen() {
    val viewModel = RaffleViewModel()
    val raffleList by viewModel.raffleList.collectAsState()

    viewModel.loadRaffles()

    var refreshing by remember { mutableStateOf(false) }

    val pullRefreshState = rememberPullRefreshState(refreshing, onRefresh = {
        // 새로고침 동작을 정의합니다.
        refreshing = true
        // 예시로 2초 지연 후 새로고침 완료로 설정합니다.
        viewModel.loadRaffles()
        refreshing = false
    })
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Google OAuth 로그인 버튼
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "AllYouRaffle", fontFamily = FontFamily(Font(R.font.logo)), fontSize = 20.sp)
        }

        PullRefreshIndicator(
            refreshing = refreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.CenterHorizontally).zIndex(1f)
        )

        // 상품 리스트
        LazyColumn(
            modifier = Modifier.pullRefresh(pullRefreshState)
        ) {
            items(raffleList) { raffle ->
                ProductCard(raffle)
            }
        }
    }
}

@Composable
fun ProductCard(raffle: RaffleResponse) {
    Card(
        modifier = Modifier
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(raffle.item.imageUrl)
                        .size(coil.size.Size.ORIGINAL) // Set the target size to load the image at.
                        .build(),
                    error = painterResource(
                        coil.compose.base.R.drawable.ic_100tb
                    )
                ),
                contentDescription = raffle.item.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = raffle.item.name,
                color = Color.Black,
                fontSize = 25.sp,
                modifier = Modifier.padding(start = 2.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = raffle.currentCount.toFloat() / raffle.totalCount,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(15.dp)
                    .padding(3.dp),
                trackColor = Color.LightGray,
                color = MaterialTheme.colorScheme.tertiary

            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = ((raffle.currentCount.toFloat() / raffle.totalCount.toFloat()) * 100).toInt()
                    .toString() + "%",
                color = Color.Black,
                fontSize = 15.sp,
                textAlign = TextAlign.Right,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 2.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary, // 버튼 배경색
                    contentColor = Color.White    // 버튼 글자색
                ),
            ) {
                Text(
                    text = "응모하기"
                )
            }
        }
    }
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        ProductListScreen()
    }
}
