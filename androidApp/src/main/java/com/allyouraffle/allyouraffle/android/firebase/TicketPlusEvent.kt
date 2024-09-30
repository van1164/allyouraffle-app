package com.allyouraffle.allyouraffle.android.firebase

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.allyouraffle.allyouraffle.android.home.AdViewModel
import com.allyouraffle.allyouraffle.android.util.LoadingScreen
import com.allyouraffle.allyouraffle.android.util.SharedPreference
import com.allyouraffle.allyouraffle.android.util.errorToast
import com.allyouraffle.allyouraffle.android.util.getRewardedAd
import com.allyouraffle.allyouraffle.viewModel.TicketPlusEventViewModel
import kotlinx.coroutines.runBlocking

@Composable
fun TicketPlusEvent(navController: NavController, rewardInt: Int) {
    val context = LocalContext.current
    val sharedPreference = SharedPreference(context)
    val jwt = sharedPreference.getJwt()
    val adViewModel = remember {
        AdViewModel()
    }
    val viewModel = remember {
        TicketPlusEventViewModel()
    }
    val rewardedAd = adViewModel.rewardedAd.collectAsState()
    val isLoading = viewModel.loading.collectAsState()
    val error = viewModel.error
    if (error.value != null) {
        errorToast(context, error.value!!, viewModel)
    }
    LaunchedEffect(Unit) {
        getRewardedAd(context,{
            viewModel.setLoadingFalse()
            viewModel.setError("광고가 모두 소진되었습니다.")
        }) { ad ->
            adViewModel.loadRewardedAd(ad)
            viewModel.setLoadingFalse()
        }
    }

    if (rewardedAd.value != null && !isLoading.value) {
        rewardedAd.value?.show(context as Activity) { reward ->
            runBlocking {
                Log.d("ZZZZZZZZZZZZZZZZZZZZZZZZZZZz",rewardInt.toString())
                viewModel.ticketPlusMany(jwt,rewardInt)
                adViewModel.setAdNull()
                navController.navigate("홈")
            }
        }?: viewModel.setError("광고 로드중에 오류가 발생했습니다.")
    }
    else{
        LoadingScreen()
    }
}