package com.allyouraffle.allyouraffle.android.raffle

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.allyouraffle.allyouraffle.android.Second
import com.allyouraffle.allyouraffle.android.Third
import com.allyouraffle.allyouraffle.android.util.Logo
import com.allyouraffle.allyouraffle.android.util.SharedPreference
import com.allyouraffle.allyouraffle.model.RaffleResponse
import com.allyouraffle.allyouraffle.viewModel.RaffleViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RaffleListScreen(viewModel: RaffleViewModel) {
    val raffleList by viewModel.raffleList.collectAsState()
    var refreshing by remember { mutableStateOf(false) }

    val pullRefreshState = rememberPullRefreshState(refreshing, onRefresh = {
        refreshing = true
        viewModel.loadRaffles()
        refreshing = false
    })
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Logo(60.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Banner(message = "광고 래플")
        Box {
            PullRefreshIndicator(
                refreshing = refreshing,
                state = pullRefreshState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .zIndex(1f)
            )
            // 상품 리스트
            LazyColumn(
                modifier = Modifier.pullRefresh(pullRefreshState)
            ) {
                items(raffleList) { raffle ->
                    ProductCard(raffle, viewModel)
                }
            }
        }
    }
}

@Composable
fun ProductCard(raffle: RaffleResponse, viewModel: RaffleViewModel) {
    val sharedPreference = SharedPreference(LocalContext.current)
    val jwt = sharedPreference.getJwt()
    val context = LocalContext.current
    val navController = rememberNavController()
    // TODO: JWT 없으면 로그인 화면으로 보내기 필요.

    NavHost(navController = navController, startDestination = "test"){
        composable("광고 래플") { RaffleListScreen(viewModel) }
    }
    val rowHeight = 120.dp
    Card(
        modifier = Modifier
            .padding(3.dp)
            .shadow(2.dp)
            .clickable {
                Log.d("ZZZZZZZZZZZZZ", "AAAAAAAAAAAAAAAAA")
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(rowHeight)
                .align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(modifier = Modifier.aspectRatio(1f)){
                Image(
                    painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(raffle.item.imageUrl)
//                        .size(Size.ORIGINAL)
                            .build(),
                        error = painterResource(
                            coil.compose.base.R.drawable.ic_100tb
                        )
                    ),
                    contentDescription = raffle.item.name,
                    modifier = Modifier
                        .fillMaxHeight(),

                    contentScale = ContentScale.Crop
                )
            }


            RaffleRightColumn(raffle, viewModel, context, jwt,rowHeight)

        }

    }
}

@Composable
fun RaffleRightColumn(
    raffle: RaffleResponse,
    viewModel: RaffleViewModel,
    context: Context,
    jwt: String,
    rowHeight: Dp,
) {
    Column(
        modifier = Modifier
            .background(Color.White)
            .height(rowHeight)
            .padding(start = 3.dp)
        ,
        verticalArrangement = Arrangement.Center,
    ) {

//        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = raffle.item.name,
            color = Color.Black,
            fontSize = 23.sp,
            fontWeight = FontWeight.Light,
            modifier = Modifier.padding(start = 2.dp)
        )
        Spacer(modifier = Modifier.height(15.dp))
        LinearProgressIndicator(
            progress = raffle.currentCount.toFloat() / raffle.totalCount,
            modifier = Modifier
                .fillMaxWidth()
                .height(15.dp)
                .padding(3.dp)
                .clip(RoundedCornerShape(12.dp)),
            trackColor = Color.LightGray,
            color = MaterialTheme.colorScheme.tertiary,
        )
        Spacer(modifier = Modifier.height(15.dp))
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
//        Spacer(modifier = Modifier.height(12.dp))

    }
}

@Composable
fun PurchaseButton(
    raffle: RaffleResponse,
    viewModel: RaffleViewModel,
    context: Context,
    jwt: String
) {
    Button(
        onClick = {
            val response = viewModel.purchase(jwt, raffle.id.toString())
            if (response) {
                Toast.makeText(context, "요청 성공", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "요청 실패", Toast.LENGTH_LONG).show()
            }
        },
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.tertiary, // 버튼 배경색
            contentColor = Color.White    // 버튼 글자색
        ),
    ) {
        Text(
            text = "응모하기",
            color = Color.White
        )
    }
}
