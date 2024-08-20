package com.allyouraffle.allyouraffle.android.detail

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.allyouraffle.allyouraffle.android.util.CustomDialog
import com.allyouraffle.allyouraffle.android.util.GoogleAd
import com.allyouraffle.allyouraffle.android.util.LoadingScreen
import com.allyouraffle.allyouraffle.android.util.SharedPreference
import com.allyouraffle.allyouraffle.model.RaffleDetailResponse
import com.allyouraffle.allyouraffle.network.Api
import com.allyouraffle.allyouraffle.viewModel.RaffleDetailViewModel
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun RaffleDetail(navController: NavController, itemId: String, isFree: Boolean) {
    MobileAds.initialize(LocalContext.current as Activity)
    val raffleViewModel = remember { RaffleDetailViewModel() }
    val raffleLoading = raffleViewModel.raffleLoading.collectAsState()
    val raffle = raffleViewModel.raffleDetail.collectAsState()
    var isLoading = remember { MutableStateFlow(false) } // 광고 로드
    val isLoadingState = isLoading.collectAsState()
    var purchaseSuccess = remember { MutableStateFlow(false) }
    val purchaseSuccessState = purchaseSuccess.collectAsState()
    var purchaseFail = remember { MutableStateFlow(false) }
    val purchaseFailState = purchaseFail.collectAsState()

    var refreshing by remember { mutableStateOf(false) }
    val pullRefreshState = rememberPullRefreshState(refreshing, onRefresh = {
        refreshing = true
        runBlocking {
            raffleViewModel.getDetail(itemId,isFree)
        }
        refreshing = false
    })
    LaunchedEffect(Unit) {
        raffleViewModel.getDetail(itemId, isFree)
    }
    Log.d("AAAAAAFDSFDSF", raffleLoading.value.toString())
    if (isLoadingState.value) {
        LoadingScreen()
    }
    if (purchaseSuccessState.value) {
        SuccessDialog(purchaseSuccess)
    }

    if (purchaseFailState.value) {
        FailDialog(purchaseFail)
    }

    if (raffleLoading.value) {
        LoadingScreen()
    } else {
        Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
            val data = raffle.value
            checkNotNull(data)
            PullRefreshIndicator(
                refreshing = refreshing,
                state = pullRefreshState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .zIndex(1f)
            )
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
                    isLoading,
                    purchaseSuccess,
                    purchaseFail
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
            .padding(start = 30.dp, end = 30.dp)
            .fillMaxSize()
    ) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .fillMaxWidth()
                .padding(top = 50.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            ) {
                ImageLoading(raffle)
            }
            Spacer(modifier = Modifier.height(30.dp))
            Text(raffle.item.name, fontSize = 35.sp, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.padding(bottom = 20.dp))
            LinearProgressIndicator(
                progress = { raffle.currentCount.toFloat() / raffle.totalCount },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .clip(RoundedCornerShape(12.dp)),
                color = if (isFree) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiary,
                trackColor = Color.LightGray,
            )
            Spacer(modifier = Modifier.height(10.dp))
            androidx.compose.material3.Text(
                text = ((raffle.currentCount.toFloat() / raffle.totalCount.toFloat()) * 100).toInt()
                    .toString() + "%",
                color = Color.Black,
                fontSize = 25.sp,
                textAlign = TextAlign.Right,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 2.dp)
            )
        }
    }

}

@Composable
private fun ImageLoading(raffle: RaffleDetailResponse) {
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(raffle.item.imageUrl)
            .build(),
        contentDescription = raffle.item.imageUrl,
        modifier = Modifier.fillMaxSize()
    ) {
        val state = painter.state
        when (state) {
            is AsyncImagePainter.State.Loading -> {
                // 로딩 중일 때 표시할 UI
                CircularProgressIndicator()
            }

            is AsyncImagePainter.State.Error -> {
                // 에러 발생 시 표시할 UI
                Text(text = "Image loading failed")
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
    isLoading: MutableStateFlow<Boolean>,
    purchaseSuccess: MutableStateFlow<Boolean>,
    purchaseFail: MutableStateFlow<Boolean>,
) {
    val sharedPreference = SharedPreference(LocalContext.current)
    val jwt = sharedPreference.getJwt()
    if (isFree) {
        ViewAdButton(
            raffle = raffle,
            viewModel = raffleViewModel,
            jwt = jwt,
            isLoading,
            purchaseSuccess,
            purchaseFail
        )
    } else {
        PurchaseButton(
            raffle = raffle,
            viewModel = raffleViewModel,
            jwt = jwt,
            purchaseSuccess,
            purchaseFail
        )
    }
}


@Composable
fun PurchaseButton(
    raffle: RaffleDetailResponse,
    viewModel: RaffleDetailViewModel,
    jwt: String,
    purchaseSuccess: MutableStateFlow<Boolean>,
    purchaseFail: MutableStateFlow<Boolean>
) {
    val context = LocalContext.current
    FloatingActionButton(
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

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ViewAdButton(
    raffle: RaffleDetailResponse,
    viewModel: RaffleDetailViewModel,
    jwt: String,
    isLoading: MutableStateFlow<Boolean>,
    purchaseSuccess: MutableStateFlow<Boolean>,
    purchaseFail: MutableStateFlow<Boolean>,
) {
    val context = LocalContext.current
    var adLoaded by remember { mutableStateOf(false) }
    var buttonClicked by remember { mutableStateOf(false) }
    var rewardedAd: RewardedAd? by remember {
        mutableStateOf(null)
    }
    val googleAd = remember {
        GoogleAd(context) { ad ->
            rewardedAd = ad
            adLoaded = true
            if (isLoading.value) {
                isLoading.update { false }
            }
            Log.d("ADD LOADED", adLoaded.toString())
            Log.d("AD LOADED", rewardedAd.toString())
            Log.d("AD LOADING", isLoading.toString())
        }
    }


    if (buttonClicked && adLoaded && !isLoading.value) {
        Log.d("AD Loading Complete", "TTTTTTTTTTTTTTTTTTTTTTTTTTTTT")
        Log.d("AD Loading Complete", rewardedAd.toString())
        rewardedAd?.show(context as Activity) { reward ->
            println(reward.type)
            println(reward.amount)
            val response = viewModel.purchase(jwt, raffle.id.toString())
            if (response) {
                purchaseSuccess.update { true }
            } else {
                purchaseFail.update { true }
            }

            buttonClicked = false
            isLoading.update { false }
            adLoaded = false
            googleAd.refreshAd()
        }
    }
    Log.d("LLLLLLLLLLLLLLLLLLLLL", rewardedAd.toString())

    FloatingActionButton(
        onClick = {
            Log.d("AAAAAAAAAAAAAAAAAAAAAAAAA", adLoaded.toString())
            Log.d("AAAAAAAAAAAAAAAAAAAAAAAAA", rewardedAd.toString())
            if (!buttonClicked) {
                if (adLoaded) {
                    buttonClicked = true
                } else {
                    buttonClicked = true
                    isLoading.update { true }
                }
            }
        },

        shape = RoundedCornerShape(15.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clickable(enabled = !buttonClicked) {},
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor = Color.White
    ) {
        Row {
            Text(
                text = "광고 보기",
                color = Color.White,
                fontSize = 19.sp,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }

    }

}

@Composable
fun SuccessDialog(purchaseSuccess: MutableStateFlow<Boolean>) {
    CustomDialog(
        title = "응모 완료!",
        body = "응모가 완료되었습니다. \n구매내역은 마이페이지에서 확인 가능합니다. \n당첨시 메일로 당첨내역이 발송됩니다.",
        "확인"
    ) {
        purchaseSuccess.update { false }
    }
}

@Composable
fun FailDialog(purchaseFail: MutableStateFlow<Boolean>) {
    CustomDialog(title = "응모 실패", body = "응모에 실패하였습니다. \n다시 시도해주세요", "확인") {
        purchaseFail.update { false }
    }
}
//private fun showAd(
//    rewardedAd: RewardedAd?,
//    context: Context
//) {
//
//}

@Preview
@Composable
fun Preview() {
    RaffleDetail(navController = rememberNavController(), itemId = "1", isFree = true)
}