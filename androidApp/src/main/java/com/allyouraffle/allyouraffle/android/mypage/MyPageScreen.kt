package com.allyouraffle.allyouraffle.android.mypage

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.allyouraffle.allyouraffle.android.R
import com.allyouraffle.allyouraffle.android.login.SetAddressView
import com.allyouraffle.allyouraffle.android.login.UserPhoneNumberMain
import com.allyouraffle.allyouraffle.android.permission.NotificationPermissionRequestAlways
import com.allyouraffle.allyouraffle.android.util.BannersAds
import com.allyouraffle.allyouraffle.android.util.BottomInfo
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
    val infoUpdated = remember {
        mutableStateOf(false)
    }
    LaunchedEffect(Unit) {
        myPageViewModel.initUserInfo(jwt)
    }

    LaunchedEffect(infoUpdated.value) {
        if (infoUpdated.value) {
            myPageViewModel.getUserInfo(jwt)
        }
        infoUpdated.value = false
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
            val myPageNavController = rememberNavController()
            NavHost(myPageNavController, startDestination = "mypage") {
                composable("mypage") { MyPage(userInfo = data, myPageNavController) }
                composable("userAddress") { SetAddressView(myPageNavController) }
                composable("changePhone") { UserPhoneNumberMain(myPageNavController) }
                composable("userPhoneNumber") {
                    infoUpdated.value = true
                }
                composable("main") {
                    infoUpdated.value = true
                }
                composable("raffleHistory") {
                    RaffleHistoryScreen(myPageNavController)
                }
            }
        }
    }
}


@Composable
fun MyPage(userInfo: UserInfoResponse, myPageNavController: NavHostController) {
    val scrollState = rememberScrollState()
    val showNotificationAlert = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val notificationState = remember { mutableStateOf(checkNotification(context)) }
    LaunchedEffect(showNotificationAlert.value) {
        notificationState.value = checkNotification(context)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val showSetAddress = remember {
            mutableStateOf(false)
        }
        val showSetPhoneNumber = remember {
            mutableStateOf(false)
        }

        if (showSetAddress.value) {
            ChangeAddressDialog(showSetAddress, userInfo, myPageNavController)
        }

        if (showSetPhoneNumber.value) {
            ChangePhoneDialog(showSetPhoneNumber, userInfo, myPageNavController)
        }



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


        if (showNotificationAlert.value) {
            NotificationPermissionRequestAlways {
                showNotificationAlert.value = false
            }
        }

        // 프로필 이미지
        ImageLoading(userInfo.profileImageUrl)


        Spacer(modifier = Modifier.height(16.dp))

        // 닉네임
        Text(
            text = userInfo.nickname,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 버튼들
        UserActionButton(
            label = if (notificationState.value) "알림 허용됨" else "알림 차단됨",
            labelColor = MaterialTheme.colorScheme.primary,
            imageVector = Icons.Default.Notifications,
            imageTint = if (notificationState.value) Color.Green else Color.Red
        ) {
            showNotificationAlert.value = true
        }
        UserActionButton(
            "주소 변경",
            warningLabel = if (userInfo.address == null) "등록 필요!" else null
        ) { showSetAddress.value = true }
        UserActionButton(
            "휴대폰번호 변경",
            warningLabel = if (userInfo.phoneNumber == null) "등록 필요!" else null
        ) { showSetPhoneNumber.value = true }
        UserActionButton(
            label = "래플 이력",
            imageVector = Icons.AutoMirrored.Filled.List
        ) {
            myPageNavController.navigate("raffleHistory")
        }
        Spacer(modifier = Modifier.height(15.dp))
        LogoutButton()
        Spacer(modifier = Modifier.height(10.dp))
        BannersAds("ca-app-pub-7372592599478425/4301150857")
        Spacer(modifier = Modifier.height(100.dp))

        BottomInfo()
    }

}

fun checkNotification(context: Context): Boolean {
    val permission = android.Manifest.permission.POST_NOTIFICATIONS
    return ContextCompat.checkSelfPermission(
        context, permission
    ) == PackageManager.PERMISSION_GRANTED
}

@Composable
private fun ChangePhoneDialog(
    showPhoneNumber: MutableState<Boolean>,
    userInfo: UserInfoResponse,
    myPageNavController: NavHostController
) {
    AlertDialog(
        onDismissRequest = { showPhoneNumber.value = false },
        contentColor = MaterialTheme.colorScheme.onPrimary,
        backgroundColor = MaterialTheme.colorScheme.onPrimary,
        text = {
            if (userInfo.phoneNumber == null) {
                Text("휴대폰 번호를 새로 등록하시겠습니까?", color = MaterialTheme.colorScheme.primary)
            } else {
                Column {
                    androidx.compose.material3.Text(
                        text = "현재 번호",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    androidx.compose.material3.Text(
                        text = userInfo.phoneNumber ?: "없음",
                        fontSize = 25.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text("휴대폰 번호를 변경하시겠습니까?", color = MaterialTheme.colorScheme.primary)
                }
            }

        },
        confirmButton = {
            Button(
                onClick = {
                    showPhoneNumber.value = false
                    myPageNavController.navigate("changePhone")
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.tertiary),
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.onPrimary)
                    .padding(5.dp)
            ) {
                Text(
                    text = if (userInfo.phoneNumber == null) "등록하기" else "변경하기",
                    color = Color.White
                )
            }
        },
        dismissButton = {
            Button(
                onClick = { showPhoneNumber.value = false },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray),
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.onPrimary)
                    .padding(5.dp)
            ) {
                Text("취소")
            }
        }
    )
}

@Composable
private fun ChangeAddressDialog(
    showSetAddress: MutableState<Boolean>,
    userInfo: UserInfoResponse,
    myPageNavController: NavHostController
) {
    AlertDialog(
        onDismissRequest = { showSetAddress.value = false },
        contentColor = MaterialTheme.colorScheme.onPrimary,
        backgroundColor = MaterialTheme.colorScheme.onPrimary,
        text = {
            if (userInfo.address == null) {
                Text("주소를 새로 등록하시겠습니까?", color = MaterialTheme.colorScheme.primary)
            } else {
                Column(
                    modifier = Modifier.background(MaterialTheme.colorScheme.onPrimary)
                ) {
                    androidx.compose.material3.Text(
                        text = "현재 주소",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    androidx.compose.material3.Text(
                        text = userInfo.address?.address + " " + userInfo.address?.detail,
                        fontSize = 25.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    androidx.compose.material3.Text(
                        text = "우편번호",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    androidx.compose.material3.Text(
                        text = userInfo.address?.postalCode ?: "없음",
                        fontSize = 25.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text("주소를 변경하시겠습니까?", color = MaterialTheme.colorScheme.primary)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    showSetAddress.value = false
                    myPageNavController.navigate("userAddress")
//                        navController.navigate("changeAddress")
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.tertiary),
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.onPrimary)
                    .padding(5.dp)
            ) {
                Text(
                    text = if (userInfo.address == null) "등록하기" else "변경하기",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            Button(
                onClick = { showSetAddress.value = false },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray),
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.onPrimary)
                    .padding(5.dp)
            ) {
                Text("취소", fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun UserActionButton(
    label: String,
    labelColor: Color = MaterialTheme.colorScheme.primary,
    warningLabel: String? = null,
    imageVector: ImageVector = Icons.Default.Edit,
    imageTint: Color = MaterialTheme.colorScheme.onSecondary,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.onBackground)
    ) {
        Text(text = label, color = labelColor, textAlign = TextAlign.Start)

        if (warningLabel == null) {
            Icon(
                imageVector = imageVector,
                contentDescription = null,
                tint = imageTint,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        warningLabel?.let { Text(text = it, color = Color.Red, fontSize = 13.sp, modifier = Modifier.padding(start = 8.dp)) }
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
                LoadingScreen()
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
