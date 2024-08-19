package com.allyouraffle.allyouraffle.android.detail

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.Toast
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
import androidx.compose.material.Text
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.allyouraffle.allyouraffle.android.util.SharedPreference
import com.allyouraffle.allyouraffle.model.RaffleDetailResponse
import com.allyouraffle.allyouraffle.network.Api
import com.allyouraffle.allyouraffle.viewModel.RaffleDetailViewModel
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback


@Composable
fun RaffleDetail(navController: NavController, itemId: String, isFree: Boolean) {
    val raffleViewModel = RaffleDetailViewModel()
    val apiState = raffleViewModel.apiState.collectAsState()
    val raffle = raffleViewModel.raffleDetail.collectAsState()
    val sharedPreference = SharedPreference(LocalContext.current)
    val jwt = sharedPreference.getJwt()
    raffleViewModel.getDetail(itemId, isFree)
    Log.d("AAAAAAFDSFDSF", apiState.value.toString())
    if (apiState.value == Api.ApiState.Before || apiState.value == Api.ApiState.Loading) {
        CircularProgressIndicator()
    } else {
        val data = raffle.value
        checkNotNull(data)
        RaffleDetailBody(raffle = data, isFree, raffleViewModel, LocalContext.current, jwt)
    }

}

@Composable
fun RaffleDetailBody(
    raffle: RaffleDetailResponse,
    isFree: Boolean,
    raffleViewModel: RaffleDetailViewModel,
    context: Context,
    jwt: String,

    ) {
    Box(
        modifier = Modifier
            .padding(start = 50.dp,end = 50.dp)
            .fillMaxSize()
    ) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier.verticalScroll(scrollState).padding(top=50.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            ) {
                ImageLoading(raffle)
            }
            Spacer(modifier = Modifier.height(30.dp))
            Text(raffle.item.name, fontSize = 35.sp)
            Spacer(modifier = Modifier.padding(bottom = 500.dp))
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 10.dp)
        ) {
            BottomButton(isFree, raffle, raffleViewModel, context, jwt)
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
    context: Context,
    jwt: String
) {
    if (isFree) {
        ViewAdButton(
            raffle = raffle,
            viewModel = raffleViewModel,
            context = context,
            jwt = jwt
        )
    } else {
        PurchaseButton(
            raffle = raffle,
            viewModel = raffleViewModel,
            context = context,
            jwt = jwt
        )
    }
}


@Composable
fun PurchaseButton(
    raffle: RaffleDetailResponse,
    viewModel: RaffleDetailViewModel,
    context: Context,
    jwt: String
) {
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

@Composable
fun ViewAdButton(
    raffle: RaffleDetailResponse,
    viewModel: RaffleDetailViewModel,
    context: Context,
    jwt: String
) {
    var rewardedAd : RewardedAd? = null
    var adRequest = AdRequest.Builder().build()
    RewardedAd.load(context,"ca-app-pub-7372592599478425/5652948903", adRequest, object : RewardedAdLoadCallback() {
        override fun onAdFailedToLoad(adError: LoadAdError) {
            Log.d("AD ERROR", adError.toString())
            rewardedAd = null
        }

        override fun onAdLoaded(ad: RewardedAd) {
            Log.d("AD Success", "Ad was loaded.")
            rewardedAd = ad
        }
    })
    rewardedAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
        override fun onAdClicked() {
            // Called when a click is recorded for an ad.
            Log.d(TAG, "Ad was clicked.")
        }

        override fun onAdDismissedFullScreenContent() {
            // Called when ad is dismissed.
            // Set the ad reference to null so you don't show the ad a second time.
            Log.d(TAG, "Ad dismissed fullscreen content.")
            rewardedAd = null
        }

        override fun onAdImpression() {
            // Called when an impression is recorded for an ad.
            Log.d(TAG, "Ad recorded an impression.")
        }

        override fun onAdShowedFullScreenContent() {
            // Called when ad is shown.
            Log.d(TAG, "Ad showed fullscreen content.")
        }
    }

    FloatingActionButton(
        onClick = {
            rewardedAd?.let { ad ->
                ad.show(context as Activity, OnUserEarnedRewardListener { rewardItem ->
                    // Handle the reward.
                    val rewardAmount = rewardItem.amount
                    val rewardType = rewardItem.type
                    Log.d(TAG, "User earned the reward.")
                })
            } ?: run {
                Log.d(TAG, "The rewarded ad wasn't ready yet.")
            }

//            val response = viewModel.purchase(jwt, raffle.id.toString())
//            if (response) {
//                Toast.makeText(context, "요청 성공", Toast.LENGTH_LONG).show()
//            } else {
//                Toast.makeText(context, "요청 실패", Toast.LENGTH_LONG).show()
//            }
        },
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor = Color.White
    ) {
        Row {
//            Icon(
//                painter = painterResource(id = R.drawable.baseline_screenshot_monitor_24), // 아이콘 리소스 ID
//                contentDescription = "Your Icon Description",
//                modifier = Modifier.align(Alignment.CenterVertically)
//            )
//            Spacer(modifier = Modifier.width(50.dp))
            Text(
                text = "광고 보기",
                color = Color.White,
                fontSize = 19.sp,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }

    }
}
