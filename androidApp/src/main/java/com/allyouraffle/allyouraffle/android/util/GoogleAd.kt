package com.allyouraffle.allyouraffle.android.util

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class GoogleAd(
    private var context: Context, private val onAdLoad: (RewardedAd) -> Unit
) {
    var rewardedAd: RewardedAd? = null

    init {
        getRewardedAd(context = context)
    }

    fun refreshAd() {

        getRewardedAd(context)
    }

    private fun getRewardedAd(context: Context) {
        val adRequest = AdRequest.Builder().build()

        RewardedAd.load(
            context,
            "ca-app-pub-7372592599478425/5652948903",
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("AD ERROR", adError.toString())
                    rewardedAd = null
                }

                override fun onAdLoaded(ad: RewardedAd) {
                    Log.d("AD Success", "Ad was loaded.")
                    onAdLoad(
                        ad
                    )
//                    ad.apply {
//                        fullScreenContentCallback = object : FullScreenContentCallback() {
//                            override fun onAdClicked() {
//                                // Called when a click is recorded for an ad.
//                                Log.d(TAG, "Ad was clicked.")
//                            }
//
//                            override fun onAdDismissedFullScreenContent() {
//                                // Called when ad is dismissed.
//                                // Set the ad reference to null so you don't show the ad a second time.
//                                Log.d(TAG, "Ad dismissed fullscreen content.")
//                                rewardedAd = null
//                            }
//
//                            override fun onAdImpression() {
//                                // Called when an impression is recorded for an ad.
//                                Log.d(TAG, "Ad recorded an impression.")
//                            }
//
//                            override fun onAdShowedFullScreenContent() {
//                                // Called when ad is shown.
//                                Log.d(TAG, "Ad showed fullscreen content.")
//                            }
//                        }
//                    }
                }
            })

        Log.d("AAAAAAAAAAAAAAAAAAAAAAAAAA", rewardedAd.toString())
    }
}