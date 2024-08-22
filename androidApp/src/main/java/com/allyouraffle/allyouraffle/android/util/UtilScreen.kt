package com.allyouraffle.allyouraffle.android.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.allyouraffle.allyouraffle.android.MainActivity
import com.allyouraffle.allyouraffle.android.R
import com.allyouraffle.allyouraffle.viewModel.BaseViewModel
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
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center // Box의 중앙 정렬
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.tertiary
        ) // 중앙에 위치할 CircularProgressIndicator
    }
}

@Composable
fun LogoutButton() {
    val context = LocalContext.current
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

@Composable
fun CustomDialog(title: String, body: String, buttonMessage: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.tertiary
            )
        },
        text = {
            Column(modifier = Modifier.padding(3.dp)) {
                Text(body, lineHeight = 20.sp, fontSize = 13.sp)
            }
        },
        confirmButton = {
            MainButton(onClick = onDismiss) {
                Text(buttonMessage, color = Color.White)
            }
//            Button(
//                onClick = onDismiss,
//                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF6200EE), contentColor = Color.White),
//                modifier = Modifier.padding(8.dp)
//            ) {
//                Text(buttonMessage)
//            }
        },
        shape = RoundedCornerShape(16.dp),
        backgroundColor = Color.White,
        contentColor = Color.Black,
        modifier = Modifier
            .padding(5.dp)
            .graphicsLayer {
                shadowElevation = 8.dp.toPx()
            }
    )
}

fun errorToast(context: Context, message: String, viewModel: BaseViewModel) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    viewModel.setNullError()
}