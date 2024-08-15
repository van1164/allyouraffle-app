package com.allyouraffle.allyouraffle.android.detail

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
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
        modifier = Modifier.padding(15.dp)
    ) {
        Column {
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

            if (isFree) {
                ViewAddButton(
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
    }
}


@Composable
fun PurchaseButton(
    raffle: RaffleDetailResponse,
    viewModel: RaffleDetailViewModel,
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
        androidx.compose.material3.Text(
            text = "응모하기",
            color = Color.White
        )
    }
}

@Composable
fun ViewAddButton(
    raffle: RaffleDetailResponse,
    viewModel: RaffleDetailViewModel,
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
        androidx.compose.material3.Text(
            text = "응모하기",
            color = Color.White
        )
    }
}