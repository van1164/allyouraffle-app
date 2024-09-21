package com.allyouraffle.allyouraffle.android.detail

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.allyouraffle.allyouraffle.android.home.myiconpack.ticketwhite.IcTickets
import com.allyouraffle.allyouraffle.android.home.myiconpack.ticketwhite.TicketWhite
import com.allyouraffle.allyouraffle.android.util.BannersAds
import com.allyouraffle.allyouraffle.android.util.BottomInfo
import com.allyouraffle.allyouraffle.android.util.CustomDialog
import com.allyouraffle.allyouraffle.android.util.LoadingScreen
import com.allyouraffle.allyouraffle.android.util.SharedPreference
import com.allyouraffle.allyouraffle.android.util.errorToast
import com.allyouraffle.allyouraffle.model.RaffleDetailResponse
import com.allyouraffle.allyouraffle.viewModel.RaffleDetailViewModel
import kotlinx.coroutines.runBlocking
import java.text.NumberFormat
import java.util.Locale


@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun RaffleDetail(navController: NavHostController, itemId: String, isFree: Boolean) {
    val context = LocalContext.current
    val raffleViewModel = remember { RaffleDetailViewModel() }
    val loading = raffleViewModel.loading.collectAsState(initial = true)
    val error = raffleViewModel.error.collectAsState()
    val raffleEnd = raffleViewModel.raffleEnd.collectAsState()
    val raffle = raffleViewModel.raffleDetail.collectAsState()
    var purchaseSuccess = raffleViewModel.purchaseSuccess.collectAsState()
    var purchaseFail = raffleViewModel.purchaseFail.collectAsState()
    var refreshing by remember { mutableStateOf(false) }
    val sharedPreference = SharedPreference(LocalContext.current)
    val jwt = sharedPreference.getJwt()
    val pullRefreshState = rememberPullRefreshState(refreshing, onRefresh = {
        refreshing = true
        runBlocking {
            raffleViewModel.initRaffleDetail(jwt,itemId,isFree)
        }
        refreshing = false
    })
    val buttonClickedState = remember{ mutableStateOf(false)}
    val userTicketsState = raffleViewModel.userTickets.collectAsState()
    val userTickets = userTicketsState.value
    LaunchedEffect(buttonClickedState.value) {
        if(buttonClickedState.value){
            if (userTickets == -1) {
                raffleViewModel.setError("응모권 조회과정에서 오류가 발생하였습니다.")
            } else if (userTickets <= 0) {
                raffleViewModel.setError("응모권이 부족합니다.")
            } else {
                raffleViewModel.purchaseWithTicket(jwt, itemId)
            }
        }
    }

    LaunchedEffect(Unit) {
        raffleViewModel.initRaffleDetail(jwt,itemId,isFree)
    }

    LaunchedEffect(raffleEnd.value) {
        if (raffleEnd.value) {
            errorToast(context, "이 래플이 종료되었습니다. 새로고침해주세요.", raffleViewModel)
            navController.popBackStack()
        }
    }

    if (loading.value) {
        LoadingScreen()
    }
    if (purchaseFail.value) {
        FailDialog(viewModel = raffleViewModel)
        buttonClickedState.value = false
    }

    if (error.value != null) {
        errorToast(context, error.value!!, raffleViewModel)
    }
    LaunchedEffect(purchaseSuccess.value) {
        if(purchaseSuccess.value){
            raffleViewModel.initRaffleDetail(jwt,itemId,isFree)
        }
    }
    if (purchaseSuccess.value) {
        SuccessDialog(raffleViewModel)
        buttonClickedState.value = false
    }

    if (loading.value || raffle.value == null || userTickets == -1) {
        LoadingScreen()
    } else {
        Box(modifier = Modifier.pullRefresh(pullRefreshState).background(MaterialTheme.colorScheme.background)) {
            val data = raffle.value
            checkNotNull(data)
            PullRefreshIndicator(
                refreshing = refreshing,
                state = pullRefreshState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .zIndex(1f)
            )
//            Icon(
//                painter = painterResource(R.drawable.ic_back),
//                contentDescription = null
//            )
            RaffleDetailBody(raffle = data, isFree)

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 10.dp, start = 15.dp, end = 15.dp)
            ) {

                BottomButton(
                    isFree,
                    data,
                    raffleViewModel,
                    itemId,
                    buttonClickedState
                )
            }
        }

    }

}

@Composable
fun RaffleDetailBody(
    raffle: RaffleDetailResponse,
    isFree: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .fillMaxWidth()
                .padding(top = 50.dp)
        ) {
        Column(Modifier.padding(start = 30.dp, end = 30.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7F)
                    .align(Alignment.CenterHorizontally)
            ) {
                ImageLoading(raffle.item.imageUrl)
            }
            Spacer(modifier = Modifier.height(30.dp))
            Text(raffle.item.name, fontSize = 35.sp, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.padding(bottom = 20.dp))
            LinearProgressIndicator(
                progress = { raffle.currentCount.toFloat() / raffle.totalCount },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .clip(RoundedCornerShape(12.dp)),
                color = if (isFree) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.tertiary,
                trackColor = MaterialTheme.colorScheme.onTertiary,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.Absolute.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                androidx.compose.material3.Text(
                    text = formatNumberWithCommas(raffle.currentCount) +  "/" + formatNumberWithCommas(raffle.totalCount),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 25.sp,
                    textAlign = TextAlign.Right,
                    modifier = Modifier
                        .padding(start = 2.dp)
                )

                androidx.compose.material3.Text(
                    text = ((raffle.currentCount.toFloat() / raffle.totalCount.toFloat()) * 100).toInt()
                        .toString() + "%",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 25.sp,
                    textAlign = TextAlign.Right,
                    modifier = Modifier
                        .padding(end = 2.dp)
                )
            }
        }
            Spacer(modifier = Modifier.height(15.dp))
            BannersAds(adId = "ca-app-pub-7372592599478425/1330069240")
            Column(modifier = Modifier.fillMaxWidth().padding(top = 30.dp)) {
                raffle.item.imageList.forEach {
                    ImageLoading(imageUrl = it.imageUrl)
                }
            }
            Spacer(modifier = Modifier.height(200.dp))
            BottomInfo()
        }
    }

}

@Composable
private fun ImageLoading(imageUrl: String) {
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .build(),
        contentScale = ContentScale.FillWidth,
        contentDescription = imageUrl,
        modifier = Modifier.fillMaxSize()
    ) {
        val state = painter.state
        when (state) {
            is AsyncImagePainter.State.Loading -> {
                // 로딩 중일 때 표시할 UI
                LoadingScreen()
            }

            is AsyncImagePainter.State.Error -> {
                // 에러 발생 시 표시할 UI
                Text(text = "이미지 로딩에 실패하였습니다.")
            }

            else -> {
                // 이미지가 성공적으로 로드된 경우
                SubcomposeAsyncImageContent(
                )
            }
        }
    }
}

@Composable
private fun BottomButton(
    isFree: Boolean,
    raffle: RaffleDetailResponse,
    raffleViewModel: RaffleDetailViewModel,
    itemId: String,
    buttonClickedState: MutableState<Boolean>,
) {
    val userTickets = raffleViewModel.userTickets.collectAsState()
    val sharedPreference = SharedPreference(LocalContext.current)
    val jwt = sharedPreference.getJwt()
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.padding(16.dp)
        ) {
//            Text(text = "응모권 갯수 : "+userTickets.value.toString(), modifier = Modifier.padding(bottom = 8.dp))
            if (isFree) {
                NewViewAdButton(
                    viewModel = raffleViewModel,
                    jwt = jwt,
                    itemId = itemId,
                    buttonClickedState,
                    userTickets
                )
            } else {
                PurchaseButton(
                    raffle = raffle,
                    viewModel = raffleViewModel,
                    jwt = jwt
                )
            }
        }
    }
}

@Composable
fun NewViewAdButton(
    viewModel: RaffleDetailViewModel,
    jwt: String,
    itemId: String,
    buttonClickedState: MutableState<Boolean>,
    userTickets: State<Int?>,
) {
    var buttonClicked by remember {
        buttonClickedState
    }

    FloatingActionButton(
        onClick = {
            buttonClicked = true
        },

        shape = RoundedCornerShape(15.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clickable(enabled = !buttonClicked) {},
        containerColor = MaterialTheme.colorScheme.tertiary,
        contentColor = Color.White
    ) {
        Row {
            Text(
                text = "응모 하기",
                color = Color.White,
                fontSize = 19.sp,
                modifier = Modifier.align(Alignment.CenterVertically),
            )
            Spacer(modifier = Modifier.width(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                val icon = remember {
                    TicketWhite.IcTickets
                }
                Image(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(30.dp)
                        .padding(end = 5.dp)
                )
                Text(
                    text = userTickets.value.toString(),
                    fontSize = 25.sp,
                    color = Color.White,
                )
            }
        }

    }
}

@Composable
fun PurchaseButton(
    raffle: RaffleDetailResponse,
    viewModel: RaffleDetailViewModel,
    jwt: String,
) {
    val context = LocalContext.current
    FloatingActionButton(
        onClick = {
            runBlocking {
                viewModel.purchase(jwt, raffle.id.toString())
            }
        },
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        containerColor = MaterialTheme.colorScheme.tertiary,
        contentColor = Color.White
    ) {
        Text(
            text = "응모하기",
            color = Color.White,
            fontSize = 19.sp
        )
    }
}
fun formatNumberWithCommas(number: Int): String {
    val formatter = NumberFormat.getInstance(Locale.US)
    return formatter.format(number)
}

@Composable
fun SuccessDialog(viewModel: RaffleDetailViewModel) {
    CustomDialog(
        title = "응모 완료!",
        body = "응모가 완료되었습니다. \n구매내역은 마이페이지에서 확인 가능합니다. \n당첨시 메일로 당첨내역이 발송됩니다.",
        "확인"
    ) {
        viewModel.setSuccessFalse()
    }
}

@Composable
fun FailDialog(viewModel: RaffleDetailViewModel) {
    CustomDialog(title = "응모 실패", body = "응모에 실패하였습니다. \n다시 시도해주세요", "확인") {
        viewModel.setFailFalse()
    }
}


//혹시 몰라 납두겠음.
//@SuppressLint("StateFlowValueCalledInComposition")
//@Composable
//fun ViewAdButton(
//    raffle: RaffleDetailResponse,
//    viewModel: RaffleDetailViewModel,
//    jwt: String,
//    isLoading: MutableStateFlow<Boolean>,
//    itemId: String,
//    isFree: Boolean
//) {
//    val context = LocalContext.current
//    var adLoaded by remember { mutableStateOf(false) }
//    var buttonClicked by remember { mutableStateOf(false) }
//    var rewardedAd: RewardedAd? by remember {
//        mutableStateOf(null)
//    }
//    val googleAd = remember {
//        GoogleAd(context) { ad ->
//            rewardedAd = ad
//            adLoaded = true
//            if (isLoading.value) {
//                isLoading.update { false }
//            }
//            Log.d("ADD LOADED", adLoaded.toString())
//            Log.d("AD LOADED", rewardedAd.toString())
//            Log.d("AD LOADING", isLoading.toString())
//        }
//    }
//
//
//    if (buttonClicked && adLoaded && !isLoading.value) {
//        Log.d("AD Loading Complete", "TTTTTTTTTTTTTTTTTTTTTTTTTTTTT")
//        Log.d("AD Loading Complete", rewardedAd.toString())
//        rewardedAd?.show(context as Activity) { reward ->
//            println(reward.type)
//            println(reward.amount)
//            runBlocking {
//                Log.d("PURCHASE START", "START")
//                viewModel.purchase(jwt, raffle.id.toString())
//                buttonClicked = false
//                isLoading.update { false }
//                adLoaded = false
//                googleAd.refreshAd()
//                viewModel.getDetail(itemId, isFree)
//                Log.d("PURCHASE End", "End")
//            }
//        }
//    }
//    Log.d("LLLLLLLLLLLLLLLLLLLLL", rewardedAd.toString())
//
//    FloatingActionButton(
//        onClick = {
//            Log.d("AAAAAAAAAAAAAAAAAAAAAAAAA", adLoaded.toString())
//            Log.d("AAAAAAAAAAAAAAAAAAAAAAAAA", rewardedAd.toString())
//            if (!buttonClicked) {
//                if (adLoaded) {
//                    buttonClicked = true
//                } else {
//                    buttonClicked = true
//                    isLoading.update { true }
//                }
//            }
//        },
//
//        shape = RoundedCornerShape(15.dp),
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(5.dp)
//            .clickable(enabled = !buttonClicked) {},
//        containerColor = MaterialTheme.colorScheme.secondary,
//        contentColor = Color.White
//    ) {
//        Row {
//            Text(
//                text = "광고 보기",
//                color = Color.White,
//                fontSize = 19.sp,
//                modifier = Modifier.align(Alignment.CenterVertically)
//            )
//        }
//
//    }
//
//}
