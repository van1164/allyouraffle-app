package com.allyouraffle.allyouraffle.android.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.allyouraffle.allyouraffle.android.MainActivity
import com.allyouraffle.allyouraffle.android.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

@Composable
fun ImageButton(image: Int, modifier: Modifier = Modifier, block: () -> Unit) {
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
fun Logo(fontSize: TextUnit = 40.sp) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "AllYouRaffle",
            fontFamily = FontFamily(Font(R.font.logo)),
            style = androidx.compose.ui.text.TextStyle(
                fontSize = fontSize,
                shadow = Shadow(
                    color = Color.DarkGray, offset = Offset(3.0f, 3.0f), blurRadius = 3f
                )
            ),
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.tertiary,
        )
    }
}

@Composable
fun MainButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.tertiary),
        modifier = modifier
        // 원하는 색상
    ) {
        content()
    }
}

@Composable
fun LogoutButton(){
    val context= LocalContext.current
    val sharedPreference = SharedPreference(context)
    Text("다른 계정으로 로그인하기 =>",
        color = Color.DarkGray.copy(alpha = 0.5f),
        modifier = Modifier.clickable {
            Log.d("LogOut", "LogOut")
            sharedPreference.deleteAllToken()
            googleSignOut(context)
            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            (context as Activity).finish()
        })
}

private fun googleSignOut(context: Context) {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .build()
    val googleSignInClient = GoogleSignIn.getClient(context, gso)
    googleSignInClient.signOut()
}