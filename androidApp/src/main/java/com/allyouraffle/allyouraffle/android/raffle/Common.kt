package com.allyouraffle.allyouraffle.android.raffle

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.allyouraffle.allyouraffle.android.R
import com.allyouraffle.allyouraffle.android.home.MyIconPack
import com.allyouraffle.allyouraffle.android.home.myiconpack.IcTickets
import com.allyouraffle.allyouraffle.android.home.myiconpack.ticketwhite.IcTickets
import com.allyouraffle.allyouraffle.android.home.myiconpack.ticketwhite.TicketWhite

@Composable
fun Banner(message: String, tickets: Int) {
    val icon : ImageVector = if(isSystemInDarkTheme()){
        remember {
            TicketWhite.IcTickets
        }
    } else{
        remember {
            MyIconPack.IcTickets
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            val fireComposition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.ad_animation))
            LottieAnimation(
                fireComposition,
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = 4f
                        scaleY = 4f
                    }
                    .size(50.dp)
                    .padding(end = 4.dp)
                    .align(Alignment.CenterVertically),
                iterations = LottieConstants.IterateForever,
                alignment = Alignment.Center,
            )
            Text(message, color = MaterialTheme.colorScheme.primary, fontSize = 30.sp, fontFamily = FontFamily(Font(R.font.fontdefault)))

        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 10.dp)
            )
            Text(
                text = "$tickets",
                fontSize = 30.sp,
                color = MaterialTheme.colorScheme.primary,
                fontFamily = FontFamily(Font(R.font.fontdefault))
            )
        }
    }
}