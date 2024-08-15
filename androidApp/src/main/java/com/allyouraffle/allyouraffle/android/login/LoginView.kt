package com.allyouraffle.allyouraffle.android.login

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.allyouraffle.allyouraffle.android.R
import com.allyouraffle.allyouraffle.android.util.ImageButton
import com.allyouraffle.allyouraffle.android.util.Logo
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task

@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun LoginPage(navController: NavHostController) {
    val context = LocalContext.current
    val loginViewModel = LoginViewModel(context)
    val authResultLauncher = rememberLauncherForActivityResult(
        contract = GoogleApiContract()
    ) { task ->
        loginViewModel.handleGoogleSignInResult(task)
    }

    val loginState by loginViewModel.loginApiState.collectAsState()
    Log.d("AAAAAAAAAAAAAAAAAA",loginState.toString())

    when (loginState){
        LoginViewModel.ApiState.Loading ->{
            println("==============================")
            Dialog(
                onDismissRequest = { loginState != LoginViewModel.ApiState.Loading },
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true,
                )
            ) {
                CircularProgressIndicator()
            }
        }
        LoginViewModel.ApiState.Error ->{
            Dialog(
                onDismissRequest = { loginState != LoginViewModel.ApiState.Error },
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true,
                )
            ) {
                Text("로그인 실패")
            }
        }

        LoginViewModel.ApiState.Success ->{
            navController.navigate("main")
        }
        else->{

        }
    }


    Column(
        modifier = Modifier
            .padding(top = 30.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Logo(60.sp)
        Text(
            "All You Raffle에 오신 것을 환영합니다! \n이제 광고를 보는 것만으로도\n특별한 보상을 받을 수 있습니다.\n다양한 상품이 여러분을 기다립니다!",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            lineHeight = 50.sp,
            fontSize=18.sp
        )

        ImageButton(
            image = R.drawable.ic_google_login_4x,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 150.dp)
                .shadow(4.dp, ambientColor = Color.LightGray)
        ) {
            println("XXXXXXXXXXXXXXXXXXXXXXXXXXXX")

            authResultLauncher.launch(1)
        }
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
        Log.d("XXXXXXXXXXXXXXXXXXXXXXXXXXXXX", intent.toString())
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