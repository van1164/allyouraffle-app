package com.allyouraffle.allyouraffle.android.login

import android.annotation.SuppressLint
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.TextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.allyouraffle.allyouraffle.android.util.LoadingScreen
import com.allyouraffle.allyouraffle.android.util.LogoutButton
import com.allyouraffle.allyouraffle.android.util.MainButton
import com.allyouraffle.allyouraffle.android.util.SharedPreference
import com.allyouraffle.allyouraffle.android.util.errorToast
import com.allyouraffle.allyouraffle.network.AddressInfo
import com.allyouraffle.allyouraffle.network.UserInfoResponse
import com.allyouraffle.allyouraffle.viewModel.AddressViewModel
import kotlinx.coroutines.launch

@Composable
fun UserAddressView(loginNavController: NavHostController, userInfoResponse: UserInfoResponse) {
    if (userInfoResponse.address != null) {
        println("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH")
        goPhoneNumberView(loginNavController)
    } else {
        SetAddressView(loginNavController)
    }
}

@Composable
fun SetAddressView(loginNavController: NavHostController) {
    val addressNavController = rememberNavController()
    val addressViewModel = remember { AddressViewModel() }
    val userAddress by addressViewModel.userAddress.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val loading = addressViewModel.loading.collectAsState()
    val error = addressViewModel.error.collectAsState()

    if (loading.value) {
        LoadingScreen()
    }

    if (error.value != null) {
        errorToast(LocalContext.current, error.value!!, addressViewModel)
    }

    NavHost(addressNavController, startDestination = "main") {
        composable("main") { InputMain(addressNavController) }
        composable("address") {
            AddressSearchWebView() { info ->
                addressViewModel.setUserAddress(info)
                println(info)
                coroutineScope.launch {
                    addressNavController.navigate("addressDetail")
                }
            }
        }
        composable("addressDetail") {
            AddressDetail(
                addressViewModel = addressViewModel,
                userAddress = userAddress,
                loginNavController = loginNavController
            )
        }
    }
}

@Composable
fun AddressDetail(
    addressViewModel: AddressViewModel,
    userAddress: AddressInfo?,
    loginNavController: NavHostController
) {
    val detail = addressViewModel.detail.collectAsState()
    if (userAddress == null) {
//        Toast.makeText(LocalContext.current, "주소 입력에 실패하였습니다.", Toast.LENGTH_LONG).show()
//        addressNavController.navigate("address")
    } else {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.onPrimary) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 50.dp, start = 20.dp, end = 20.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = "주소 정보",
                    fontSize = 38.sp,
                    color = Color(0xFF1E88E5),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "주소",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = userAddress.address,
                    fontSize = 30.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "우편번호",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text =userAddress.postalCode,
                    fontSize = 30.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                TextField(
                    value = detail.value,
                    onValueChange = { addressViewModel.setDetail(it) },
                    label = { Text("상세 주소 입력", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSecondary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 20.sp),
                    colors = androidx.compose.material.TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                        focusedLabelColor = MaterialTheme.colorScheme.onSecondary,
                        cursorColor = MaterialTheme.colorScheme.onSecondary,
                        textColor = MaterialTheme.colorScheme.primary
                    ),
                )

                Spacer(modifier = Modifier.height(16.dp))

                val context = LocalContext.current
                val jwt = SharedPreference(context).getJwt()
                val keyboardController = LocalSoftwareKeyboardController.current
                Button(
                    onClick = {
                        if (addressViewModel.saveUserAddress(jwt)) {
                            keyboardController?.hide()
                            goPhoneNumberView(loginNavController)
                        } else {
                            Toast.makeText(context, "주소 저장에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1E88E5)),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFF1E88E5))
                ) {
                    Text(
                        text = "저장", fontSize = 18.sp, color = Color.White
                    )
                }
            }
        }
    }
}
//}

@Composable
fun InputMain(addressNavController: NavHostController) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.onPrimary) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "당첨 상품 배송을 위해 주소지 입력이 필요합니다.",
                fontSize = 29.sp,
                color = Color(0xFF1E88E5),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "배송을 위해 주소를 입력해 주세요.",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            MainButton(
                onClick = { addressNavController.navigate("address") },
                modifier = Modifier.width(400.dp)
            ) {
                Text(
                    "주소 입력하기",
                    color = Color.White,
                    fontSize = 30.sp,
                    modifier = Modifier.padding(10.dp)
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
            LogoutButton()
        }
    }

}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun AddressSearchWebView(onAddressLoaded: (AddressInfo) -> Unit) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        loadUrl("javascript:sample2_execDaumPostcode();")
                        Log.d("AAAAAAAAAAAAAAAAAAAAAAAAAA", "ZZZZZZZZZZZZZZZZZZZZZZZZZ")
                    }
                }
                webChromeClient = WebChromeClient()
                clearCache(true)
                settings.javaScriptEnabled = true
                addJavascriptInterface(BridgeInterface(onAddressLoaded), "Android")
                loadUrl("https://allyouraffle-f271f.web.app")
            }
        }, modifier = Modifier.fillMaxSize()
    )
}

class BridgeInterface(private val onAddressLoaded: (AddressInfo) -> Unit) {
    @JavascriptInterface
    @SuppressWarnings("unused")
    fun loadUserData(
        address: String,
        addressEnglish: String,
        bname: String,
        jibunAddress: String,
        jibunAddressEnglish: String,
        roadAddress: String,
        sido: String,
        sigungu: String,
        postalCode: String,
        country: String
    ) {
        val addressInfo = AddressInfo(
            address,
            addressEnglish,
            bname,
            jibunAddress,
            jibunAddressEnglish,
            roadAddress,
            sido,
            sigungu,
            postalCode,
            country
        )
        onAddressLoaded(addressInfo) // 주소 정보를 호출한 쪽으로 전달
    }
}

fun goPhoneNumberView(loginNavController: NavHostController) {
    loginNavController.navigate("userPhoneNumber") {
        popUpTo("userAddress") { inclusive = true } // 로그인 화면을 스택에서 제거
    }
}