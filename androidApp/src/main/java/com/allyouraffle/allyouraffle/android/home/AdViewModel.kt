package com.allyouraffle.allyouraffle.android.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.android.gms.ads.rewarded.RewardedAd
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AdViewModel {

    private val _rewardedAd : MutableStateFlow<RewardedAd?> = MutableStateFlow(null)
    val rewardedAd = _rewardedAd.asStateFlow()
    private val _buttonClicked = MutableStateFlow(false)
    val buttonClicked = _buttonClicked.asStateFlow()
    fun loadRewardedAd(ad:RewardedAd){
        _rewardedAd.update { ad }
    }

    fun setButtonClickedTrue() {
        _buttonClicked.update { true }
    }

    fun setButtonClickedFalse() {
        _buttonClicked.update { false }
    }
}