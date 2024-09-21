package com.allyouraffle.allyouraffle.android.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
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
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
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
    Spacer(modifier = Modifier.height(15.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "AllYouRaffle",
            fontFamily = FontFamily(Font(R.font.fontdefault)),
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
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center // Box의 중앙 정렬
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.logo_shadow_trans))
        LottieAnimation(
            composition,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            iterations = LottieConstants.IterateForever,
            alignment = Alignment.Center,
        )

//        CircularProgressIndicator(
//            color = MaterialTheme.colorScheme.tertiary
//        ) // 중앙에 위치할 CircularProgressIndicator
    }
}

@Composable
fun LogoutButton() {
    val context = LocalContext.current
    val sharedPreference = SharedPreference(context)
    val showDialog = remember { mutableStateOf(false) }
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { },
            backgroundColor = MaterialTheme.colorScheme.onPrimary,
            text = {
                Text(
                    text = "로그아웃 하시겠습니까?",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary
                )

            },
            confirmButton = {
                Button(
                    onClick = {
                        Log.d("LogOut", "LogOut")
                        sharedPreference.deleteAllToken()
                        googleSignOut(context)
                        val intent = Intent(context, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                        (context as Activity).finish()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.tertiary),
                    modifier = Modifier.padding(5.dp)
                ) {
                    androidx.compose.material.Text(text = "로그아웃", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog.value = false },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray),
                    modifier = Modifier.padding(5.dp)
                ) {
                    androidx.compose.material.Text("취소")
                }
            }
        )
    }


    Text("다른 계정으로 로그인하기 =>",
        color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.5f),
        modifier = Modifier.clickable {
            showDialog.value = true
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
                Text(body, color = MaterialTheme.colorScheme.primary,lineHeight = 20.sp, fontSize = 13.sp)
            }
        },
        confirmButton = {
            MainButton(onClick = onDismiss, modifier = Modifier.padding(10.dp).fillMaxWidth()) {
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
        backgroundColor = MaterialTheme.colorScheme.onPrimary,
        contentColor = MaterialTheme.colorScheme.primary,
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

@Composable
fun LazyListState.OnBottomReached(loadMore: () -> Unit) {
    val isAtBottom by remember {
        derivedStateOf {
            // LazyColumn의 스크롤 위치와 총 항목 수를 사용하여 끝에 도달했는지 확인
            layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1
        }
    }
    LaunchedEffect(isAtBottom) {
        if (isAtBottom) {
            loadMore()
        }
    }
}

@Composable
fun ScrollingText(text: String) {
    val scrollState = rememberScrollState()

    BasicText(
        text = text,
        modifier = Modifier
            .horizontalScroll(scrollState) // 가로 스크롤 가능하도록 설정
    )
}

@Composable
fun BottomInfo() {

    var isExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        // 버튼
        androidx.compose.material3.Button(
            shape = RoundedCornerShape(5.dp),
            onClick = { isExpanded = !isExpanded },
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimary),
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth()
        ) {
            Text(text = if (isExpanded) "정보 숨기기" else "판매자 정보 보기", color = MaterialTheme.colorScheme.primary)
        }

        // 정보 표시
        if (isExpanded) {
            Column(modifier = Modifier
                .fillMaxSize()
                .background(Color(0XFFF4F4F4))
                .height(200.dp)
                .padding(start = 5.dp)
                .graphicsLayer {
                    shape = RoundedCornerShape(10.dp)
                }
            ) {
                androidx.compose.material.Text(
                    text = "상호 : 올유레플",
                    modifier = Modifier.padding(1.dp),
                    fontSize = 10.sp
                )
                androidx.compose.material.Text(
                    text = "대표자 명 : 김시환",
                    modifier = Modifier.padding(1.dp),
                    fontSize = 10.sp
                )
                androidx.compose.material.Text(
                    text = "사업자 등록 번호 : 580-46-01046",
                    modifier = Modifier.padding(1.dp),
                    fontSize = 10.sp
                )
                androidx.compose.material.Text(
                    text = "문의 이메일 : allyouraffle.info@gmail.com",
                    modifier = Modifier.padding(1.dp),
                    fontSize = 10.sp
                )
                androidx.compose.material.Text(
                    text = "사업장 소재지 : 경기도 용인시 기흥구 서그내로 46-14",
                    modifier = Modifier.padding(1.dp),
                    fontSize = 10.sp
                )
            }
        }
    }

}