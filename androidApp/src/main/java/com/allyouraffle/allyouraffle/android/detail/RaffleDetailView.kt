package com.allyouraffle.allyouraffle.android.detail

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
        androidx.compose.material3.Text(
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
            androidx.compose.material3.Text(
                text = "광고 보기",
                color = Color.White,
                fontSize = 19.sp,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }

    }
}
