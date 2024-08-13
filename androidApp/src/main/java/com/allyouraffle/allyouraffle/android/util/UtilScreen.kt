package com.allyouraffle.allyouraffle.android.util

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.allyouraffle.allyouraffle.android.R
import java.time.format.TextStyle

@Composable
fun ImageButton(image : Int,modifier: Modifier = Modifier,block : () -> Unit) {
    val imageModifier = modifier
        .clickable {
            block.invoke()
        }

    Image(
        painter = painterResource(id = image), // 이미지 리소스
        contentDescription = null,
        modifier = imageModifier
    )
}

@Composable
fun Logo(fontSize : TextUnit = 30.sp){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "AllYouRaffle",
            fontFamily = FontFamily(Font(R.font.logo)),
            fontSize = fontSize,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}