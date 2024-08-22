package com.allyouraffle.allyouraffle.android.mypage

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.allyouraffle.allyouraffle.android.R
import com.allyouraffle.allyouraffle.android.util.LoadingScreen
import com.allyouraffle.allyouraffle.android.util.LogoutButton
import com.allyouraffle.allyouraffle.android.util.SharedPreference
import com.allyouraffle.allyouraffle.android.util.errorToast
import com.allyouraffle.allyouraffle.network.UserInfoResponse
import com.allyouraffle.allyouraffle.viewModel.MyPageViewModel
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun MyPageScreen(myPageViewModel: MyPageViewModel) {
    val userInfo = myPageViewModel.userInfo.collectAsState()
    val loading = myPageViewModel.loading.collectAsState()
    val sharedPreference = SharedPreference(LocalContext.current)
    val coroutineScope = rememberCoroutineScope()
    val error = myPageViewModel.error.collectAsState()
    val jwt = sharedPreference.getJwt()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        myPageViewModel.initUserInfo(jwt)
    }

    if (error.value != null) {
        errorToast(context, error.value!!, myPageViewModel)
    }

    if (loading.value) {
        LoadingScreen()
    } else {
        val data = userInfo.value
        if (data == null) {
            coroutineScope.launch {
                myPageViewModel.getUserInfo(jwt)
            }
        } else {
            MyPage(data)
        }
    }
}

@Composable
fun MyPage(userInfo: UserInfoResponse) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            "마이 페이지",
            modifier = Modifier.padding(top = 40.dp, bottom = 20.dp),
            color = MaterialTheme.colorScheme.tertiary,
            fontFamily = MaterialTheme.typography.titleLarge.fontFamily,
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 40.sp,
                shadow = Shadow(
                    color = Color.DarkGray, offset = Offset(2.0f, 2.0f), blurRadius = 3f
                )
            ),
        )
        // 프로필 이미지
        ImageLoading(userInfo.profileImageUrl)


        Spacer(modifier = Modifier.height(16.dp))

        // 닉네임
        Text(
            text = userInfo.nickname,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 버튼들
        UserActionButton("주소 변경") { /* 주소 변경 로직 */ }
        UserActionButton("휴대폰번호 변경") { /* 휴대폰번호 변경 로직 */ }
        UserActionButton("래플 이력") { /* 래플 이력 화면으로 이동 */ }
        Spacer(modifier = Modifier.height(15.dp))
        LogoutButton()
    }

}

@Composable
fun UserActionButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.primary)
    ) {
        Text(text = label, color = Color.Black, textAlign = TextAlign.Start)
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun ImageLoading(imageUrl: String?) {
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl ?: R.drawable.default_user_image)
            .build(),
        contentDescription = imageUrl,
        modifier = Modifier.width(150.dp)
    ) {
        val state = painter.state
        when (state) {
            is AsyncImagePainter.State.Loading -> {
                // 로딩 중일 때 표시할 UI
                CircularProgressIndicator()
            }

            is AsyncImagePainter.State.Error -> {
                // 에러 발생 시 표시할 UI
                // 벡터 자산 로드
                val vectorImage =
                    painterResource(id = R.drawable.default_user_image) // your_vector_asset은 벡터 파일 이름입니다.
                // 이미지 표시
                Image(
                    painter = vectorImage,
                    contentDescription = "Vector Asset",
                    modifier = Modifier.width(150.dp)
                )
            }

            else -> {
                // 이미지가 성공적으로 로드된 경우
                SubcomposeAsyncImageContent(
                )
            }
        }
    }
}
