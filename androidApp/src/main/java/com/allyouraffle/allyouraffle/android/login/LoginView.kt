package com.allyouraffle.allyouraffle.android.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.allyouraffle.allyouraffle.android.R
import com.allyouraffle.allyouraffle.android.util.ImageButton
import com.allyouraffle.allyouraffle.android.util.Logo
import com.allyouraffle.allyouraffle.android.util.SharedPreference
import com.allyouraffle.allyouraffle.network.UserInfoResponse
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task


@Composable
fun LoginPage(navController: NavHostController) {
    Log.d("START", "START")
    val context = LocalContext.current
    val loginViewModel = remember { LoginViewModel() }
    val sharedPreference = SharedPreference(context)
    var jwtState by remember { mutableStateOf(true) }
    key(jwtState) {
        if (checkJwtExist(sharedPreference, loginViewModel)) {
            val userInfoResponse = loginViewModel.getUserInfo(sharedPreference.getJwt())
            AddressNavHost(userInfoResponse, navController)
//        goMain(navController)
        } else {
            val authResultLauncher = rememberLauncherForActivityResult(
                contract = GoogleApiContract()
            ) { task ->
                Log.d("TTTTTTTTTTTTTTTTTTTTTTTTTTT", "TASK")
                loginViewModel.handleGoogleSignInResult(task)?.run {
                    get("JWT")?.let { sharedPreference.setJwt(it) }
                    get("REFRESH")?.let { sharedPreference.setRefresh(it) }
                    jwtState = !jwtState
//                val userInfoResponse = loginViewModel.getUserInfo(sharedPreference.getJwt())
//                AddressNavHost(userInfoResponse = userInfoResponse, navController = navController)
                }
            }
            LoginView(authResultLauncher, loginViewModel)
        }
    }

}

@Composable
private fun AddressNavHost(
    userInfoResponse: UserInfoResponse,
    navController: NavHostController
) {
    val loginNavController = rememberNavController()
    NavHost(loginNavController, startDestination = "userAddress") {
        composable("userAddress") { UserAddressView(loginNavController, userInfoResponse) }
        composable("userPhoneNumber") { UserPhoneNumberView(navController, userInfoResponse) }
    }
}


fun checkJwtExist(
    sharedPreference: SharedPreference,
    loginViewModel: LoginViewModel
): Boolean {
    Log.d("CHECK", "CHECK")
    try {
        val jwt = sharedPreference.getJwt()
        val refreshToken = sharedPreference.getRefreshToken()
        Log.d("JWT", jwt)
        Log.d("REFRESH", refreshToken)
        if (loginViewModel.jwtVerify(jwt)) {
            Log.d("JWTVERFY", "JWTVERFY")
            return true
        }
        Log.d("LLLLLLLLLLLLLLLLLLLLLLLLLL", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
        loginViewModel.refresh(refreshToken)?.run {
            Log.d("QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ", this)
            sharedPreference.setJwt(this)
            return true
        }
    } catch (e: RuntimeException) {
        return false
    }
    return false
}

@Composable
fun ErrorMessage(message: String = "로그인중 에러 발생") {
    Toast.makeText(LocalContext.current, message, Toast.LENGTH_SHORT).show()
}

@Composable
private fun ObserveLoading(
    isLoading: Boolean
) {
    Log.d("Loading", isLoading.toString())
    if (isLoading) {
        Dialog(
            onDismissRequest = { !isLoading },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
            )
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun LoginView(
    authResultLauncher: ManagedActivityResultLauncher<Int, Task<GoogleSignInAccount>?>,
    loginViewModel: LoginViewModel,
) {
    val isLoading by loginViewModel.isLoading.collectAsState()
    ObserveLoading(isLoading)
    ObserveError(loginViewModel)
    Column(
        modifier = Modifier
            .padding(top = 30.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Logo(60.sp)
        androidx.compose.material3.Text(
            "All You Raffle에 오신 것을 환영합니다! \n이제 광고를 보는 것만으로도\n특별한 보상을 받을 수 있습니다.\n다양한 상품이 여러분을 기다립니다!",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            lineHeight = 50.sp,
            fontSize = 18.sp
        )

        ImageButton(
            image = R.drawable.ic_google_login_4x,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 150.dp)
                .shadow(4.dp, ambientColor = Color.LightGray)
        ) {
            authResultLauncher.launch(1)
        }
    }
}

@Composable
fun ObserveError(viewModel: LoginViewModel) {
    val error by viewModel.error.collectAsState()
    if (error) {
        ErrorMessage()
        viewModel.endError()
    }
}

class GoogleApiContract : ActivityResultContract<Int, Task<GoogleSignInAccount>?>() {
    override fun createIntent(context: Context, input: Int): Intent {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(ContextCompat.getString(context, R.string.web_client_id))
            .requestId()
            .requestEmail()
            .build()

        val intent = GoogleSignIn.getClient(context, gso)
        return intent.signInIntent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Task<GoogleSignInAccount>? {
        return when (resultCode) {
            Activity.RESULT_OK -> {
                GoogleSignIn.getSignedInAccountFromIntent(intent)
            }

            else -> null
        }
    }
}