package com.allyouraffle.allyouraffle.android.util

import android.content.Context
import android.util.Log
import com.allyouraffle.allyouraffle.exception.NetworkException
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

//class GoogleAd(
//private val onAdFailed : () -> Unit, private val onAdLoad: (RewardedAd) -> Unit
//) {
//    var rewardedAd: RewardedAd? = null
//
////    init {
////        getRewardedAd(context = context)
////    }
//
//    fun refreshAd(context: Context) {
//
//        getRewardedAd(context)
//    }

fun getSubRewardedAd(context: Context,onAdFailed : () -> Unit, onAdLoad: (RewardedAd) -> Unit){
        val adRequest = AdRequest.Builder().build()

        RewardedAd.load(
            context,
            "ca-app-pub-7372592599478425/1055136453",
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("AD ERROR", adError.toString())
                    onAdFailed()
                }

                override fun onAdLoaded(ad: RewardedAd) {
                    Log.d("AD Success", "Ad was loaded.")
                    Log.d("AD SUB SUCCESS","AD SUB LOADED")
                    onAdLoad(
                        ad
                    )
                }
            })
    }

fun getRewardedAd(context: Context,onAdFailed : () -> Unit, onAdLoad: (RewardedAd) -> Unit) {
    val adRequest = AdRequest.Builder().build()

    RewardedAd.load(
        context,
        "ca-app-pub-7372592599478425/5652948903",
        adRequest,
        object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("AD ERROR", adError.toString())
                getSubRewardedAd(context,onAdFailed,onAdLoad)
//                    rewardedAd = null
//                    onAdFailed()
            }

            override fun onAdLoaded(ad: RewardedAd) {
                Log.d("AD Success", "Ad was loaded.")
                onAdLoad(
                    ad
                )
            }
        })

}
