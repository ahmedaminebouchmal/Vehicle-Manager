package com.technoapps.vehiclemanager.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.technoapps.vehiclemanager.R
import com.technoapps.vehiclemanager.common.CommonConstantAd
import com.technoapps.vehiclemanager.common.CommonConstants
import com.technoapps.vehiclemanager.common.CommonUtilities
import com.technoapps.vehiclemanager.interfaces.AdsCallback
import com.technoapps.vehiclemanager.interfaces.CallbackListener

class SplashActivity : AppCompatActivity(), AdsCallback ,CallbackListener{

    private var isLoaded = false
    private var isStop = false
    internal var handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
//        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        checkAndroidId()
        checkInternet()
    }



    private fun checkAndroidId() {
        val androidId = CommonUtilities.getAndroidId(this)
        if (androidId.isNotEmpty()) {
            Log.e("<><> AndroidId", androidId)
        }
    }

 /*   override fun onResume() {
        super.onResume()
        checkInternet()
    }*/

    private fun startNextActivity(time:Long) {
        if (CommonUtilities.isOnline(this)) {
            Handler().postDelayed({
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                finish()
            }, time)
        } else {
            val intent = Intent(this, NoInternetActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onResume() {
        super.onResume()
        if (isStop) {
            checkInternet()
            isStop = false
        }

    }

    @SuppressLint("HardwareIds")
    private fun checkInternet() {
        try {
            Log.e("Splash", "checkInternet")
            if (CommonUtilities.isOnline(this)) {
                successCall()
            } else {
                Toast.makeText(this, "No Internet access", Toast.LENGTH_SHORT).show()
//                startNextActivity(1000)
                CommonUtilities.openInternetDialog(this, this, true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun successCall() {
        if (!CommonUtilities.getPref(this, CommonConstants.IS_FIRST_TIME, false)) {
            CommonUtilities.setPref(this, CommonConstants.IS_FIRST_TIME, true)
            startNextActivity(1000)
            CommonUtilities.setPref(this, CommonConstants.SPLASH_SCREEN_COUNT, 2)
        } else {
            if (CommonUtilities.getPref(this, CommonConstants.SPLASH_SCREEN_COUNT, 1) == 1) {
                CommonUtilities.setPref(this, CommonConstants.SPLASH_SCREEN_COUNT, 2)
                startNextActivity(1000)
            } else {
                checkAd()
            }
        }
    }


    private fun checkAd() {
        if (CommonUtilities.getPref(this, CommonConstants.STATUS_ENABLE_DISABLE, "") == CommonConstants.ENABLE) {
            Log.e(
                "TAG",
                "checkAd:L::::IFF:: " + CommonUtilities.getPref(this, CommonConstants.AD_TYPE_FB_GOOGLE, "")
            )
            if (CommonUtilities.getPref(this, CommonConstants.AD_TYPE_FB_GOOGLE, "") == CommonConstants.AD_GOOGLE) {
                CommonConstantAd.GooglebeforloadAd(this)
            } else if (CommonUtilities.getPref(
                    this,
                    CommonConstants.AD_TYPE_FB_GOOGLE,
                    ""
                ) == CommonConstants.AD_FACEBOOK
            ) {
                CommonConstantAd.FacebookbeforeloadFullAd(this)
            }

            if (CommonUtilities.getPref(this, CommonConstants.STATUS_ENABLE_DISABLE, "") == CommonConstants.ENABLE) {
                Handler().postDelayed({
                    when (CommonUtilities.getPref(this, CommonConstants.AD_TYPE_FB_GOOGLE, "")) {
                        CommonConstants.AD_GOOGLE -> {
                            if (!isStop) {
                                CommonConstantAd.showInterstitialAdsGoogle(this,this)
                            }
                        }
                        CommonConstants.AD_FACEBOOK -> {
                            if (!isStop) {
                                CommonConstantAd.showInterstitialAdsFacebook(this)
                            }
                        }
                        else -> {
                            if (!isStop) {
                                startNextActivity(0)
                            }
                        }
                    }
                    CommonUtilities.setPref(this, CommonConstants.SPLASH_SCREEN_COUNT, 1)
                }, 3000)
            } else {
                startNextActivity(1000)
            }


        } else {
            startNextActivity(1000)
        }

    }



    override fun startNextScreen() {
        startNextActivity(0)
    }

    override fun onLoaded() {
        isLoaded = true
    }

    private val myRunnable = Runnable {
        if (CommonUtilities.isOnline(this)) {
            Log.e("TAG", "myRunnable:::::: ")
            if (!isLoaded && !isStop) {
                startNextActivity(0)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        Log.e("TAG", "onStop:myRunnable:::::: ")
        isStop = true
        handler.removeCallbacks(myRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("TAG", "onDestroy:myRunnable::::  ")
        handler.removeCallbacks(myRunnable)
    }

    override fun onSuccess() {

    }

    override fun onCancel() {

    }

    override fun onRetry() {
        checkInternet()
    }

}