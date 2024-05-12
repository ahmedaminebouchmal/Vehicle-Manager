package com.technoapps.vehiclemanager.common

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.facebook.ads.Ad
import com.facebook.ads.InterstitialAdListener
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.technoapps.vehiclemanager.interfaces.AdsCallback


object CommonConstantAd {


    fun loadBannerGoogleAd(context: Context, llAdview: RelativeLayout) {
        val adViewBottom = AdView(context)
        adViewBottom.setAdSize( AdSize.BANNER)
        adViewBottom.adUnitId = CommonUtilities.getPref(context, CommonConstants.GOOGLE_BANNER, "")!!
        llAdview.addView(adViewBottom)
        val adRequest = AdRequest.Builder().build()
        adViewBottom.loadAd(adRequest)
        adViewBottom.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                adViewBottom.visibility = View.VISIBLE
                llAdview.visibility = View.VISIBLE
                CommonConstants.GOOGLE_AD_IS_LOADED = true
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                Log.e("TAG", "onAdFailedToLoad:::Failed:::  " + p0.toString())
                super.onAdFailedToLoad(p0)
                llAdview.visibility = View.GONE
                adViewBottom.visibility = View.GONE
                CommonConstants.GOOGLE_AD_IS_LOADED = false
            }
        }
    }

    fun loadBannerFacebookAd(context: Context, banner_container: LinearLayout) {

        var adView: com.facebook.ads.AdView? = null

        adView = com.facebook.ads.AdView(
            context, CommonUtilities.getPref(context, CommonConstants.FB_BANNER, ""),
            com.facebook.ads.AdSize.BANNER_HEIGHT_50
        )


        banner_container.addView(adView)

        val adListener: com.facebook.ads.AdListener = object : com.facebook.ads.AdListener {
            override fun onError(ad: Ad?, adError: com.facebook.ads.AdError) {
                // Ad error callback
                Log.e("TAG", "onError:Fb:::: ${adError.errorCode}  ${adError.errorMessage}")
                banner_container.visibility = View.GONE
                CommonConstants.FB_AD_IS_LOADED = false
            }

            override fun onAdLoaded(ad: Ad?) {
                // Ad loaded callback
                Log.e("TAG", "onAdLoaded:::::: ")
                banner_container.visibility = View.VISIBLE
                CommonConstants.FB_AD_IS_LOADED = true
            }

            override fun onAdClicked(ad: Ad?) {
                // Ad clicked callback
            }

            override fun onLoggingImpression(ad: Ad?) {
                // Ad impression logged callback
            }


        }

        adView!!.loadAd(adView.buildLoadAdConfig().withAdListener(adListener).build())
    }


    /*private fun getAdRequest(): AdRequest {
        return AdRequest.Builder().build()
    }*/


    /*Google Full Ad*/
  /*  var mInterstitialAdGoogle: InterstitialAd? = null
    private lateinit var adsCallback: AdsCallback
    fun GooglebeforloadAd(context: Context) {
        mInterstitialAdGoogle = InterstitialAd(context)
//        mInterstitialAdGoogle!!.adUnitId = _root_ide_package_.com.technoapps.vehiclemanager.common.CommonConstants.GOOGLE_INTERSTITIAL
        mInterstitialAdGoogle!!.adUnitId =
            CommonUtilities.getPref(context, CommonConstants.GOOGLE_INTERSTITIAL, "")
        mInterstitialAdGoogle!!.loadAd(getAdRequest())

        mInterstitialAdGoogle!!.adListener = object : AdListener() {
            override fun onAdLoaded() {
                Log.e("TAG", "onAdLoaded:::::Before:: ")

                // Code to be executed when an ad finishes loading.
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                // Code to be executed when an ad request fails.
                Log.e("TAG", "onAdFailedToLoad:::::Before::: $adError")
            }

            override fun onAdOpened() {
                Log.e("TAG", "onAdOpened:::::Before::: ")
                // Code to be executed when the ad is displayed.
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            override fun onAdClosed() {
                adsCallback.startNextScreen()
                Log.e("TAG", "onAdClosed:::::Before::::   ")
            }
        }

    }
    fun showInterstitialAdsGoogle(adsCallback: AdsCallback) {

        this.adsCallback = adsCallback
        if (mInterstitialAdGoogle != null) {
            if (mInterstitialAdGoogle!!.isLoaded) {
                mInterstitialAdGoogle!!.show()
                mInterstitialAdGoogle = null
                adsCallback.onLoaded()
            } else {
                adsCallback.startNextScreen()
                Log.e("TAG", "The interstitial wasn't loaded yet.")
            }
        } else {
            adsCallback.startNextScreen()
        }

    }
*/

    private val adRequest: AdRequest
        private get() = AdRequest.Builder().build()
    var googleinterstitial: InterstitialAd? = null
    fun GooglebeforloadAd(context: Context?) {
        try {
            InterstitialAd.load(context!!,
                CommonUtilities.getPref(context, CommonConstants.GOOGLE_INTERSTITIAL, "")!!,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        super.onAdLoaded(interstitialAd)
                        Log.e("TAG", "onAdLoaded::::INTERS " )
                        googleinterstitial = interstitialAd
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        super.onAdFailedToLoad(loadAdError)
                        Log.e("TAG", "onAdFailedToLoad::::INTERS $loadAdError" )
                        googleinterstitial = null
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun showInterstitialAdsGoogle(activity: Activity?, adsCallback: AdsCallback) {
        try {
            if (googleinterstitial != null) {
                googleinterstitial!!.fullScreenContentCallback = object :
                    FullScreenContentCallback() {
                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        super.onAdFailedToShowFullScreenContent(adError)
                        googleinterstitial = null
                        adsCallback.startNextScreen()
                    }

                    override fun onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent()
                        adsCallback.onLoaded()
                    }

                    override fun onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent()
                        googleinterstitial = null
                        adsCallback.startNextScreen()
                    }

                }
                googleinterstitial!!.show(activity!!)
            } else {
                adsCallback.startNextScreen()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /*Facebook Full Ad*/
    var interstitialAdFb: com.facebook.ads.InterstitialAd? = null
    private var adsCallbackFb: AdsCallback? = null
    fun FacebookbeforeloadFullAd(context: Context) {
//        interstitialAdFb = com.facebook.ads.InterstitialAd(context, Constant.FB_INTERSTITIAL)
        interstitialAdFb = com.facebook.ads.InterstitialAd(
            context,
            CommonUtilities.getPref(context, CommonConstants.FB_INTERSTITIAL, "")
        )
        val interstitialAdListener: InterstitialAdListener = object : InterstitialAdListener {
            override fun onInterstitialDisplayed(ad: Ad?) {
                Log.e("TAG", "Interstitial ad displayed.")

            }

            override fun onInterstitialDismissed(ad: Ad?) {
                Log.e("TAG", "Interstitial ad dismissed.")
                adsCallbackFb!!.startNextScreen()
            }

            override fun onError(ad: Ad?, adError: com.facebook.ads.AdError) {
//                adsCallbackFb!!.adLoadingFailed()
            }

            override fun onAdLoaded(ad: Ad?) {
                Log.e("TAG", "Interstitial ad is loaded and ready to be displayed!")
                // Show the ad
            }

            override fun onAdClicked(ad: Ad?) {
                Log.e("TAG", "Interstitial ad clicked!")
            }

            override fun onLoggingImpression(ad: Ad?) {
                Log.e("TAG", "Interstitial ad impression logged!")
            }
        }

        interstitialAdFb!!.loadAd(
            interstitialAdFb!!.buildLoadAdConfig()
                .withAdListener(interstitialAdListener)
                .build()
        )


    }
    fun showInterstitialAdsFacebook(adsCallbackFb: AdsCallback?) {
        this.adsCallbackFb = adsCallbackFb
        if (interstitialAdFb != null) {

            if (interstitialAdFb!!.isAdLoaded) {
                interstitialAdFb!!.show()
                interstitialAdFb = null
                adsCallbackFb!!.onLoaded()
            } else {
                adsCallbackFb!!.startNextScreen()
            }
        } else {
            adsCallbackFb!!.startNextScreen()
        }

    }


}
