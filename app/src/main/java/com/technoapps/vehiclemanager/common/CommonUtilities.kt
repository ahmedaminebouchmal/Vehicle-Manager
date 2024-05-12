package com.technoapps.vehiclemanager.common

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.preference.PreferenceManager
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.technoapps.vehiclemanager.R
import com.technoapps.vehiclemanager.interfaces.CallbackListener
import java.text.SimpleDateFormat
import java.util.*

object CommonUtilities {


    fun addKeyboardDetectListener(context: Context,llAdView:RelativeLayout,llAdViewFacebook:LinearLayout){
        val topView = (context as Activity).window.decorView.findViewById<View>(android.R.id.content)
        topView.viewTreeObserver.addOnGlobalLayoutListener {
            val heightDifference = topView.rootView.height - topView.height
            if(heightDifference > dpToPx(context, 200F)){
                // keyboard shown
                Log.e("TAG", "keyboard shown::::::::::")
                llAdView.visibility = View.GONE
                llAdViewFacebook.visibility = View.GONE
            } else {
                // keyboard hidden
                if (getPref(context,CommonConstants.AD_TYPE_FB_GOOGLE,"") == CommonConstants.AD_GOOGLE &&
                    getPref(context,CommonConstants.STATUS_ENABLE_DISABLE,"") == CommonConstants.ENABLE && CommonConstants.GOOGLE_AD_IS_LOADED) {
                    llAdViewFacebook.visibility= View.GONE
                    llAdView.visibility = View.VISIBLE
                } else if (getPref(context,CommonConstants.AD_TYPE_FB_GOOGLE,"") == CommonConstants.AD_FACEBOOK
                    && getPref(context,CommonConstants.STATUS_ENABLE_DISABLE,"") == CommonConstants.ENABLE && CommonConstants.FB_AD_IS_LOADED) {
                    llAdViewFacebook.visibility= View.VISIBLE
                    llAdView.visibility = View.GONE
                }else{
                    llAdView.visibility = View.GONE
                    llAdViewFacebook.visibility= View.GONE
                }
                Log.e("TAG", "keyboard hidden:::::::::")

            }
        }
    }


    fun dpToPx(context: Context, valueInDp: Float) : Float{
        val displayMetrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, displayMetrics)
    }


    val retryPolicy = DefaultRetryPolicy(
        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 20,
        0,
        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
    )

    fun isOnline(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }

    fun showToast(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    /*fun getVehicleImgFromType(context: Context, type: String): Drawable {
        var vehicleImg: Drawable? = null
        if (type == context.resources.getString(R.string.vehicle_title_bike)) {
            vehicleImg = ContextCompat.getDrawable(context, R.drawable.ic_honda)
        } else if (type == context.resources.getString(R.string.vehicle_title_car)) {
            vehicleImg = ContextCompat.getDrawable(context, R.drawable.ic_car)
        } else if (type == context.resources.getString(R.string.vehicle_title_three_wheeler)) {
            vehicleImg = ContextCompat.getDrawable(context, R.drawable.ic_auto)
        } else if (type == context.resources.getString(R.string.vehicle_title_bus)) {
            vehicleImg = ContextCompat.getDrawable(context, R.drawable.ic_bus)
        } else if (type == context.resources.getString(R.string.vehicle_title_truck)) {
            vehicleImg = ContextCompat.getDrawable(context, R.drawable.ic_truck)
        } else if (type == context.resources.getString(R.string.vehicle_title_tractor)) {
            vehicleImg = ContextCompat.getDrawable(context, R.drawable.ic_tector)
        }
        return vehicleImg!!
    }*/

    fun getVehicleImgFromType(context: Context, type: String,linearLayout: LinearLayout,imgNext:ImageView): Drawable {
        var vehicleImg: Drawable? = null
        if (type == context.resources.getString(R.string.vehicle_title_bike)) {
            vehicleImg = ContextCompat.getDrawable(context, R.drawable.ic_bike_new_png)
            linearLayout.setBackgroundResource(R.drawable.bg_vehicle_bike)
            imgNext.setColorFilter(ContextCompat.getColor(context, R.color.ShadowbgBike), android.graphics.PorterDuff.Mode.SRC_IN);

        } else if (type == context.resources.getString(R.string.vehicle_title_car)) {
            vehicleImg = ContextCompat.getDrawable(context, R.drawable.ic_car_new_png)
            linearLayout.setBackgroundResource(R.drawable.bg_vehicle_car)
            imgNext.setColorFilter(ContextCompat.getColor(context, R.color.ShadowbgCar), android.graphics.PorterDuff.Mode.SRC_IN);

        } else if (type == context.resources.getString(R.string.vehicle_title_three_wheeler)) {
            vehicleImg = ContextCompat.getDrawable(context, R.drawable.ic_threewheeler_new_png)
            linearLayout.setBackgroundResource(R.drawable.bg_vehicle_threewhell)
            imgNext.setColorFilter(ContextCompat.getColor(context, R.color.ShadowbgThreeWheel), android.graphics.PorterDuff.Mode.SRC_IN);

        } else if (type == context.resources.getString(R.string.vehicle_title_bus)) {
            vehicleImg = ContextCompat.getDrawable(context, R.drawable.ic_bus_new_png)
            linearLayout.setBackgroundResource(R.drawable.bg_vehicle_bus)
            imgNext.setColorFilter(ContextCompat.getColor(context, R.color.ShadowbgBus), android.graphics.PorterDuff.Mode.SRC_IN);

        } else if (type == context.resources.getString(R.string.vehicle_title_truck)) {
            vehicleImg = ContextCompat.getDrawable(context, R.drawable.ic_truck_new_png)
            linearLayout.setBackgroundResource(R.drawable.bg_vehicle_truck)
            imgNext.setColorFilter(ContextCompat.getColor(context, R.color.ShadowbgTruck), android.graphics.PorterDuff.Mode.SRC_IN);

        } else if (type == context.resources.getString(R.string.vehicle_title_tractor)) {
            vehicleImg = ContextCompat.getDrawable(context, R.drawable.ic_tractor_new_png)
            linearLayout.setBackgroundResource(R.drawable.bg_vehicle_tractor)
            imgNext.setColorFilter(ContextCompat.getColor(context, R.color.ShadowbgTractor), android.graphics.PorterDuff.Mode.SRC_IN);

        }
        return vehicleImg!!
    }

//    fun showAlert123FinishOnClick123(
//        context: Context, finishAct: Boolean,
//        title: String, msg: String,
//        posBtnVis: Boolean, posBtnTitle: String,
//        negBtnVis: Boolean, negBtnTitle: String
//    ) {
//        val alertDialog = AlertDialog.Builder(context)
//        alertDialog.setTitle(title)
//        alertDialog.setMessage(msg)
//        if (posBtnVis) {
//            alertDialog.setPositiveButton(posBtnTitle) { dialog, _ ->
//                if (finishAct) {
//                    (context as AppCompatActivity).finish()
//                } else {
//                    dialog.cancel()
//                }
//            }
//        }
//        if (negBtnVis) {
//            alertDialog.setNegativeButton(negBtnTitle) { dialog, _ -> dialog.cancel() }
//        }
//        alertDialog.setCancelable(false)
//        alertDialog.show()
//    }

    fun showAlertFinishOnClick(context: Context, isDataUpdated: Boolean) {
        val alertDialog = AlertDialog.Builder(context,R.style.AlertDialogTheme)
        alertDialog.setTitle(CommonConstants.CapConfirm)
        alertDialog.setMessage(CommonConstants.MsgDoYouWantToExit)
        alertDialog.setPositiveButton(CommonConstants.CapExit) { dialog, _ ->
            dialog.dismiss()
            setResultAndFinish(context, isDataUpdated)
        }
        alertDialog.setNegativeButton(CommonConstants.CapCancel) { dialog, _ -> dialog.dismiss() }
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    fun setResultAndFinish(context: Context, isDataUpdated: Boolean) {
        if (isDataUpdated) {
            val intent = Intent()
            intent.putExtra(CommonConstants.KeyIsDataUpdated, true)
            (context as AppCompatActivity).setResult(Activity.RESULT_OK, intent)
        }
        (context as AppCompatActivity).finish()
    }

    fun stringDateToLongFormatForMinDate(stringDate: String): Long {
        var milliseconds = Date().time
//        val stringDate = "12-December-2012";
        val f = SimpleDateFormat(CommonConstants.CapDateFormat, Locale.getDefault());
        try {
            val d = f.parse(stringDate);
            milliseconds = d.getTime();
        } catch (e: java.lang.Exception) {
            e.printStackTrace();
        }
        return milliseconds
    }

    fun getDateInDigitWithoutTime(alarmDate: String): String {
        val displayFormat = SimpleDateFormat(CommonConstants.CapServerDateFormat, Locale.getDefault())
        val parseFormat = SimpleDateFormat(CommonConstants.CapDateFormat, Locale.getDefault())
        var date = Date()
        try {
            date = displayFormat.parse(alarmDate)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return parseFormat.format(date)
    }

    fun getDateWithMonthName(alarmDate: String): String {
        val displayFormat = SimpleDateFormat(CommonConstants.CapDateFormat, Locale.getDefault())
        val parseFormat = SimpleDateFormat(CommonConstants.CapDateWithMonthName, Locale.getDefault())
        var date = Date()
        try {
            date = displayFormat.parse(alarmDate)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return parseFormat.format(date)
    }

    fun get12HourFormatTime(fullTime: String): String {
        val displayFormat = SimpleDateFormat(CommonConstants.CapTimeFullFormat, Locale.getDefault())
        val parseFormat = SimpleDateFormat(CommonConstants.CapTimeShortFormat, Locale.getDefault())
        var date = Date()
        try {
            date = displayFormat.parse(fullTime)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return parseFormat.format(date)
//        10:30 PM = 22:30
//        println(parseFormat.format(date) + " = " + displayFormat.format(date))
    }

    fun getEmailIdFromPref(context: Context): String {
        var emailId = ""
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        if (pref.contains(CommonConstants.KeyEmailId)) {
            emailId = pref.getString(CommonConstants.KeyEmailId, "")!!
        }
        Log.e("<><> EmailId", emailId)
        return emailId
    }

    fun setEmailIdInPref(context: Context, emailId: String) {
        Log.e("<><> EmailId", emailId)
        PreferenceManager
            .getDefaultSharedPreferences(context)
            .edit()
            .putString(CommonConstants.KeyEmailId, emailId)
            .apply()
    }

    @SuppressLint("HardwareIds")
    fun getAndroidId(context: Context): String {
        var androidId = getPref(context, CommonConstants.KeyDeviceId, "")
        if (androidId != null && androidId.isNotEmpty()) {
            Log.e("<><> AndroidId", androidId)
        } else {
            try {
                androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
                if (androidId != null && !androidId.isEmpty()) {
                    setPref(context, CommonConstants.KeyDeviceId, androidId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return androidId!!
    }

    fun getKmReadingData(p0: Editable?): String {
        var data = ""
        if (p0!!.isNotEmpty() && !p0.contains(".")) {
            try {
                val dataFloat = p0.toString().toFloat()
                val dataLength = p0.toString().length
                val finalDataFloat: Float
                if (dataLength > 6) {
                    finalDataFloat = (dataFloat / 10)
                    data = String.format("%.1f", finalDataFloat)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return data
    }


    //Todo--------------------------------------------Preferences Methods--------------------------------------------------
    fun getPref(context: Context, key: String, defVal: String): String? {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, defVal)
    }

    fun setPref(context: Context, key: String, `val`: String) {
        val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putString(key, `val`)
        editor.apply()
    }

    fun getPref(context: Context, key: String, defVal: Int): Int {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(key, defVal)
    }

    fun setPref(context: Context, key: String, `val`: Int) {
        val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putInt(key, `val`)
        editor.apply()
    }

    fun getPref(context: Context, key: String, defVal: Boolean): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, defVal)
    }

    fun setPref(context: Context, key: String, `val`: Boolean) {
        val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putBoolean(key, `val`)
        editor.apply()
    }

    fun getPref(context: Context, key: String, defVal: Long): Long {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(key, defVal)
    }

    fun setPref(context: Context, key: String, `val`: Long) {
        val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putLong(key, `val`)
        editor.apply()
    }
    internal var adsDialog: Dialog? = null

    private fun showAdsDialog(context: Context) {
        adsDialog = Dialog(context)
        adsDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        adsDialog!!.setContentView(R.layout.dialog_full_screen_ad)
        adsDialog!!.setCancelable(false)
        adsDialog!!.show()
    }

    private var deleteVehicle = 0

    private var addVehicleDetail = 0


    fun openInternetDialog(context: Context, callbackListener: CallbackListener, isSplash: Boolean) {
        if (!isOnline(context)) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("No internet Connection")
            builder.setCancelable(false)
            builder.setMessage("Please turn on internet connection to continue")
            builder.setNegativeButton("Retry") { dialog, _ ->
                if (!isSplash) {
                    openInternetDialog(context,callbackListener, false)
                }
                dialog!!.dismiss()
                callbackListener.onRetry()

            }
            builder.setPositiveButton("Close") { dialog, _ ->
                dialog!!.dismiss()
                val homeIntent = Intent(Intent.ACTION_MAIN)
                homeIntent.addCategory(Intent.CATEGORY_HOME)
                homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                context.startActivity(homeIntent)
                (context as Activity).finishAffinity()
            }
            val alertDialog = builder.create()
            alertDialog.show()
        }
    }
}