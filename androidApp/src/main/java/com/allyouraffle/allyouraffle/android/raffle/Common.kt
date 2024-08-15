package com.allyouraffle.allyouraffle.android.raffle

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.allyouraffle.allyouraffle.android.R

@Composable
fun Banner(message : String){
    Box(
        modifier = Modifier.fillMaxWidth().height(70.dp).padding(15.dp)
    ){
        Text(message, fontSize = 25.sp, fontFamily = FontFamily(Font(R.font.fontdefault)))
    }
}