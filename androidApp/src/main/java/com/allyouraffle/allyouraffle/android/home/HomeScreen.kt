package com.allyouraffle.allyouraffle.android.home

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.allyouraffle.allyouraffle.android.R
import com.allyouraffle.allyouraffle.android.home.myiconpack.IcTickets
import com.allyouraffle.allyouraffle.android.home.myiconpack.ticketwhite.IcTickets
import com.allyouraffle.allyouraffle.android.home.myiconpack.ticketwhite.TicketWhite
import com.allyouraffle.allyouraffle.android.raffle.ProductCard
import com.allyouraffle.allyouraffle.android.util.BannersAds
import com.allyouraffle.allyouraffle.android.util.LoadingScreen
import com.allyouraffle.allyouraffle.android.util.SharedPreference
import com.allyouraffle.allyouraffle.android.util.errorToast
import com.allyouraffle.allyouraffle.android.util.getRewardedAd
import com.allyouraffle.allyouraffle.viewModel.HomeViewModel
import com.allyouraffle.allyouraffle.viewModel.TicketViewModel
import kotlinx.coroutines.runBlocking

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    adViewModel: AdViewModel,
    navHostController: NavHostController
) {
    // Lottie 애니메이션
    val context = LocalContext.current
    val sharedPreference = SharedPreference(context)
    val jwt = sharedPreference.getJwt()
    val error = homeViewModel.error.collectAsState()
    LaunchedEffect(Unit) {
        homeViewModel.initHomeV2(jwt)
    }
    val scrollShape = rememberScrollState()
    if (error.value != null) {
        errorToast(context, error.value!!, homeViewModel)
    }
    HomeScreenBody(homeViewModel, adViewModel, jwt, scrollShape, navHostController)

}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun HomeScreenBody(
    homeViewModel: HomeViewModel,
    adViewModel: AdViewModel,
    jwt: String,
    scrollShape: ScrollState,
    navHostController: NavHostController
) {
    var refreshing by remember { mutableStateOf(false) }
    val pullRefreshState = rememberPullRefreshState(refreshing, onRefresh = {
        refreshing = true
        runBlocking {
            homeViewModel.refreshV2()
        }
        refreshing = false
    })

    Box(
        modifier = Modifier
            .pullRefresh(pullRefreshState)
            .background(MaterialTheme.colorScheme.background)
    ) {
        PullRefreshIndicator(
            refreshing = refreshing,
            state = pullRefreshState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(1f)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollShape)
                .pullRefresh(pullRefreshState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
//            Logo()
            Spacer(modifier = Modifier.height(15.dp))
            Surface(
                shape = RoundedCornerShape(15.dp),
                elevation = 10.dp,
                color = MaterialTheme.colorScheme.onPrimary
            ) {
                // 응모권 개수 표시
                TicketView(adViewModel, jwt)
            }
            Spacer(modifier = Modifier.height(15.dp))
            BannersAds("ca-app-pub-7372592599478425/6983154510")
            Spacer(modifier = Modifier.height(15.dp))
            Surface(
                shape = RoundedCornerShape(15.dp),
                elevation = 10.dp
            ) {
                PopularRankingView(homeViewModel, navHostController)
            }
        }

    }
}

@Composable
private fun PopularRankingView(homeViewModel: HomeViewModel, navHostController: NavHostController) {
    val fireComposition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.fire))
    val raffleList = homeViewModel.popularRaffleList.collectAsState()
    val isLoading = homeViewModel.loading.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.background(MaterialTheme.colorScheme.onPrimary)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LottieAnimation(
                fireComposition,
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.CenterVertically),
                iterations = LottieConstants.IterateForever,
                alignment = Alignment.Center,
            )
            // 인기 경품 랭킹 표시
            Text(
                text = "인기 래플",
                fontSize = 30.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
        if (isLoading.value) {
            LoadingScreen()
        } else {
            // 상품 리스트
            Column(
                modifier = Modifier
                    .fillMaxHeight()
            ) {
                raffleList.value.forEach { raffle ->
                    ProductCard(raffle, navHostController, raffle.isFree)
                }
            }
        }
    }
}

@Composable
private fun TicketView(
    adViewModel: AdViewModel,
    jwt: String,
) {
    // 버튼 클릭 시 응모권 추가 애니메이션
    val context = LocalContext.current
    val ticketViewModel = remember { TicketViewModel() }
    val ticketCount = ticketViewModel.ticketCount.collectAsState()
    val buttonClicked = adViewModel.buttonClicked.collectAsState()
    val rewardedAd = adViewModel.rewardedAd.collectAsState()
    val isLoading = ticketViewModel.loading.collectAsState()
    val icon: ImageVector = if (isSystemInDarkTheme()) {
        remember {
            TicketWhite.IcTickets
        }
    } else {
        remember {
            MyIconPack.IcTickets
        }
    }

    LaunchedEffect(Unit) {
        ticketViewModel.loadTickets(jwt)
    }
    if (buttonClicked.value && rewardedAd.value != null) {
        rewardedAd.value?.show(context as Activity) { reward ->
            runBlocking {
                ticketViewModel.ticketPlusOne(jwt)
                adViewModel.setButtonClickedFalse()
                adViewModel.setAdNull()
                getRewardedAd(context, {
                    ticketViewModel.setLoadingFalse()
                    adViewModel.setButtonClickedFalse()
                    ticketViewModel.setError("광고가 모두 소진되었습니다.. ㅠㅠ 10분정도 이후에 시도해주세요.")
                }) { ad ->
                    adViewModel.loadRewardedAd(ad)
                    ticketViewModel.setLoadingFalse()

                }
            }
        } ?: ticketViewModel.setError("광고 로드중에 오류가 발생했습니다.")
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "현재 응모권",
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            if (ticketCount.value == -1) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            } else {
                Image(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
                Text(
                    text = "${ticketCount.value}",
                    fontSize = 35.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(16.dp),
                    fontFamily = FontFamily(Font(R.font.fontdefault))
                )
            }
        }


        // 광고를 보고 응모권을 획득하는 버튼
        Button(
            onClick = {
                if (!buttonClicked.value) {
                    if (rewardedAd.value != null) {
                        adViewModel.setButtonClickedTrue()
                    } else {
                        getRewardedAd(context, {
                            ticketViewModel.setLoadingFalse()
                            adViewModel.setButtonClickedFalse()
                            ticketViewModel.setError("광고가 모두 소진되었습니다.. ㅠㅠ 10분정도 이후에 시도해주세요.")
                        }) { ad ->
                            adViewModel.setAdNull()
                            adViewModel.loadRewardedAd(ad)
                            ticketViewModel.setLoadingFalse()
                        }
                        adViewModel.setButtonClickedTrue()
                        ticketViewModel.setLoadingTrue()
                    }
                }
            },
            enabled = !buttonClicked.value,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.tertiary, disabledBackgroundColor = Color.DarkGray),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        ) {
            if(buttonClicked.value){
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
            else{
                Text(text = "광고 시청 후 응모권 획득", color = Color.White)
            }
        }
    }

}

//
//@Composable
//fun ProductCard(
//    raffle: RaffleResponse,
//    navController: NavController,
//    isFree: Boolean
//) {
//    // TODO: JWT 없으면 로그인 화면으로 보내기 필요.
//    val rowHeight = 120.dp
//    androidx.compose.material3.Card(
//        modifier = Modifier
//            .padding(3.dp)
//            .shadow(2.dp)
//            .clickable {
//                navController.navigate("raffle/" + raffle.id + "/" + isFree)
//            }
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(rowHeight)
//                .align(Alignment.CenterHorizontally),
//            horizontalArrangement = Arrangement.SpaceBetween,
//        ) {
//            Box(modifier = Modifier.aspectRatio(1f)) {
//                Image(
//                    painter = rememberAsyncImagePainter(
//                        model = ImageRequest.Builder(LocalContext.current)
//                            .data(raffle.item.imageUrl)
//                            .build(),
//                        error = painterResource(
//                            R.drawable.baseline_error_outline_24
//                        )
//                    ),
//                    contentDescription = raffle.item.name,
//                    modifier = Modifier
//                        .fillMaxHeight(),
//
//                    contentScale = ContentScale.Crop
//                )
//            }
//
//
//            RaffleRightColumn(raffle, rowHeight, isFree)
//
//        }
//
//    }
//}
//
//@Composable
//fun RaffleRightColumn(
//    raffle: RaffleResponse,
//    rowHeight: Dp,
//    isFree: Boolean
//) {
//    Column(
//        modifier = Modifier
//            .background(Color.White)
//            .height(rowHeight)
//            .padding(start = 3.dp),
//        verticalArrangement = Arrangement.Center,
//    ) {
//
//        Text(
//            text = raffle.item.name,
//            color = Color.Black,
//            fontSize = 23.sp,
//            fontWeight = FontWeight.Light,
//            modifier = Modifier.padding(start = 2.dp),
//            maxLines = 1,
//            overflow = TextOverflow.Ellipsis
//        )
//        Spacer(modifier = Modifier.height(15.dp))
//        LinearProgressIndicator(
//            progress = { raffle.currentCount.toFloat() / raffle.totalCount },
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(15.dp)
//                .padding(3.dp)
//                .clip(RoundedCornerShape(12.dp)),
//            color = if (isFree) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiary,
//            trackColor = Color.LightGray,
//        )
//        Spacer(modifier = Modifier.height(15.dp))
//        Text(
//            text = ((raffle.currentCount.toFloat() / raffle.totalCount.toFloat()) * 100).toInt()
//                .toString() + "%",
//            color = Color.Black,
//            fontSize = 15.sp,
//            textAlign = TextAlign.Right,
//            modifier = Modifier
//                .align(Alignment.End)
//                .padding(end = 2.dp)
//        )
//    }
//}