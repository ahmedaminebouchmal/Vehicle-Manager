package com.technoapps.vehiclemanager.activities

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.technoapps.vehiclemanager.R
import com.technoapps.vehiclemanager.adapters.VehicleListAdapter
import com.technoapps.vehiclemanager.common.CommonConstantAd
import com.technoapps.vehiclemanager.common.CommonConstants
import com.technoapps.vehiclemanager.common.CommonUtilities
import com.technoapps.vehiclemanager.databinding.ActivityMainBinding
import com.technoapps.vehiclemanager.interfaces.AdapterItemCallback
import com.technoapps.vehiclemanager.interfaces.VehicleListDownloadCallback
import com.technoapps.vehiclemanager.notifications.AlarmReceiver
import com.technoapps.vehiclemanager.pojo.VehicleClass
import com.technoapps.vehiclemanager.services.GetVehicleList
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), View.OnClickListener, VehicleListDownloadCallback,
    AdapterItemCallback {

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var vehicleClassArrayList = ArrayList<VehicleClass>()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setContentView(R.layout.activity_main)
        initViews()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkPermission()
        }
        checkForNotification()
        subScribeToFirebaseTopic()
        callGetAdsId()
        val emailId = CommonUtilities.getEmailIdFromPref(this)
        if (emailId != null && emailId.isNotEmpty()) {
            binding.llSignOutMenu.visibility = View.VISIBLE
            binding.tvLogin.visibility = View.GONE
            binding.tvMsg.visibility = View.GONE

            binding.tvEmailId.visibility = View.VISIBLE
            binding.tvEmailId.text = emailId

            GetVehicleList(this, this@MainActivity).callGetVehicleListService()
        } else {
            binding.llSignOutMenu.visibility = View.GONE
            binding.tvLogin.visibility = View.VISIBLE
            binding.tvMsg.visibility = View.VISIBLE

            binding.tvEmailId.visibility = View.GONE
            binding.tvEmailId.text = ""

            googleSignIn()
        }
//        CommonUtilities.loadBannerAd(findViewById(R.id.adViewBottom))
        if (CommonUtilities.getPref(this, CommonConstants.AD_TYPE_FB_GOOGLE, "") == CommonConstants.AD_GOOGLE &&
            CommonUtilities.getPref(this, CommonConstants.STATUS_ENABLE_DISABLE, "") == CommonConstants.ENABLE
        ) {
            CommonConstantAd.loadBannerGoogleAd(this, binding.llAdView)
            binding.llAdViewFacebook.visibility = View.GONE
            binding.llAdView.visibility = View.VISIBLE
        } else if (CommonUtilities.getPref(this, CommonConstants.AD_TYPE_FB_GOOGLE, "") == CommonConstants.AD_FACEBOOK
            && CommonUtilities.getPref(this, CommonConstants.STATUS_ENABLE_DISABLE, "") == CommonConstants.ENABLE
        ) {
            CommonConstantAd.loadBannerFacebookAd(this, binding.llAdViewFacebook)
            binding.llAdViewFacebook.visibility = View.VISIBLE
            binding.llAdView.visibility = View.GONE
        } else {
            binding.llAdView.visibility = View.GONE
            binding.llAdViewFacebook.visibility = View.GONE
        }

        CommonUtilities.addKeyboardDetectListener(this, binding.llAdView, binding.llAdViewFacebook)
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkPermission() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED -> {
                Log.e("TAG", "User accepted the notifications!")
//                sendNotification(this)
            }

            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
//                Snackbar.make(
//                    binding.root,
//                    "The user denied the notifications ):",
//                    Snackbar.LENGTH_LONG
//                ).show()
            }

            else -> {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
            } else {
                // Explain to the user that the feature is unavailable because the
                // feature requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            }
        }
    private fun initViews() {

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        vehicleClassArrayList = ArrayList()

        binding.rvCategoryList.layoutManager = LinearLayoutManager(
            this,
            androidx.recyclerview.widget.LinearLayoutManager.VERTICAL,
            false
        )

        binding.imgToggle.setOnClickListener(this)
        binding.imgAdd.setOnClickListener(this)
        binding.tvLogin.setOnClickListener(this)

        binding.llHomeMenu.setOnClickListener(this)
        binding.llContactMenu.setOnClickListener(this)
        binding.llRateMenu.setOnClickListener(this)
        binding.llShareMenu.setOnClickListener(this)
        binding.llMoreMenu.setOnClickListener(this)
        binding.llSignOutMenu.setOnClickListener(this)
        binding.llExitMenu.setOnClickListener(this)
    }

    private fun checkForNotification() {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        if (!pref.getBoolean(CommonConstants.KeyIsFirstTime, false)) {
            setAlarmManager()
            val editor = pref.edit()
            editor.putBoolean(CommonConstants.KeyIsFirstTime, true)
            editor.apply()
        }
    }

    override fun onClick(view: View) {
        val id = view.id
        when (id) {
            R.id.imgToggle -> openDrawer()
            R.id.imgAdd -> {
                val emailId = CommonUtilities.getEmailIdFromPref(this)
                if (emailId != null && emailId.isNotEmpty()) {
//                    CommonUtilities.initFullAdd(this, this@MainActivity)
                    openVehicleDetailsActivity()
                } else {
                    googleSignIn()
                }
            }
            R.id.tvLogin -> googleSignIn()
            R.id.llHomeMenu -> closeDrawer()
            R.id.llContactMenu -> contactUs()
            R.id.llRateMenu -> rateUs()
            R.id.llShareMenu -> shareAppLink()
            R.id.llSignOutMenu -> googleSignOut()
            R.id.llExitMenu -> confirmationDialog(this, getString(R.string.exit_confirmation))
        }
    }


    private fun googleSignIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, CommonConstants.RC_GOOGLE_SIGN_IN)
    }

    private fun googleSignOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this) {

            CommonUtilities.setEmailIdInPref(this, "")
            CommonUtilities.showToast(this@MainActivity, CommonConstants.MsgYouAreSignOut)

            binding.llSignOutMenu.visibility = View.GONE
            binding.tvLogin.visibility = View.VISIBLE
            binding.tvMsg.visibility = View.VISIBLE

            binding.tvEmailId.visibility = View.GONE
            binding.tvEmailId.text = ""

            closeDrawer()
            vehicleClassArrayList.clear()
            vehicleClassArrayList = ArrayList()
            setVehicleDetailDownloadCallback(vehicleClassArrayList)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                val emailId = account.email
                if (emailId != null) {
                    CommonUtilities.setEmailIdInPref(this, emailId)
                    CommonUtilities.showToast(this, CommonConstants.MsgSignInSuccessfully)

                    binding.llSignOutMenu.visibility = View.VISIBLE
                    binding.tvLogin.visibility = View.GONE
                    binding.tvMsg.visibility = View.GONE

                    binding.tvEmailId.visibility = View.VISIBLE
                    binding.tvEmailId.text = emailId

                    GetVehicleList(this, this@MainActivity).callGetVehicleListService()
                } else {
                    CommonUtilities.showToast(this, CommonConstants.MsgSomethingWrong)
                }
            } else {
                CommonUtilities.showToast(this, CommonConstants.MsgSomethingWrong)
            }
        } catch (e: ApiException) {
            e.printStackTrace()
        }
    }

    override fun setVehicleDetailDownloadCallback(categoryClassArrayList: ArrayList<VehicleClass>) {
        this.vehicleClassArrayList = categoryClassArrayList
        if (vehicleClassArrayList.size > 0) {
            binding.rvCategoryList.visibility = View.VISIBLE
            binding.llNoData.visibility = View.GONE
            val mLayoutManager = LinearLayoutManager(this)
            mLayoutManager.reverseLayout = true
            mLayoutManager.stackFromEnd = true
            binding.rvCategoryList.layoutManager = mLayoutManager
            binding.rvCategoryList.adapter = VehicleListAdapter(this, vehicleClassArrayList, this@MainActivity)
        } else {
            binding.rvCategoryList.visibility = View.GONE
            binding.llNoData.visibility = View.VISIBLE
        }
    }

    private fun openDrawer() {

        try {
            if (!binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun closeDrawer() {

        try {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openVehicleDetailsActivity() {
        val intent = Intent(this, VehicleDetailActivity::class.java)
        startActivityForResult(intent, CommonConstants.RequestDataUpdated)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val key = CommonConstants.KeyIsDataUpdated
        if (requestCode == CommonConstants.RC_GOOGLE_SIGN_IN) {
//            if (resultCode == Activity.RESULT_OK) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
            task.addOnCompleteListener {
                OnCompleteListener<GoogleSignInClient> {
                    Log.e("TAG", "onActivityResult:Complete:::::: ")
                }
            }.addOnFailureListener { exception: java.lang.Exception ->
                Log.e("TAG", "onActivityResult:::Exception::: $exception")
                CommonUtilities.showToast(this, CommonConstants.MsgSignInCancelled)
            }
//            } else if (resultCode == Activity.RESULT_CANCELED) {
//                CommonUtilities.showToast(this, CommonConstants.MsgSignInCancelled)
//            }
            Log.e("TAG", "onActivityResult::::::::Google::: " + resultCode)
        } else if (resultCode == Activity.RESULT_OK) {
            if (data!!.hasExtra(key)) {
                if (data.getBooleanExtra(key, false)) {
                    GetVehicleList(this, this@MainActivity).callGetVehicleListService()
                }
            }
        }
    }

    override fun onItemTypeClickCallback(mPos: Int) {
        if (CommonUtilities.isOnline(this)) {
            val aClass = vehicleClassArrayList[mPos]
            val intent = Intent(this, VehicleCategories::class.java)
            intent.putExtra(CommonConstants.KeyVehicleDetail, aClass)
            startActivityForResult(intent, CommonConstants.RequestDataUpdated)
        } else {
            startActivity(Intent(this, NoInternetActivity::class.java))
        }
    }

    /*override fun adLoadingFailed() {
        openVehicleDetailsActivity()
    }

    override fun adClose() {
        openVehicleDetailsActivity()
    }

    override fun startNextScreen() {
        openVehicleDetailsActivity()
    }*/

    //Todo--------------------------------------------Menu Items--------------------------------------------------

    private fun rateUs() {
        val appPackageName = packageName
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store")))
        } catch (anfe: android.content.ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store")))
        }
    }

    private fun shareAppLink() {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        val link = "https://play.google.com/store/apps/details?id=${"your_package_name"}"
        shareIntent.putExtra(Intent.EXTRA_TEXT, link)
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name))
        shareIntent.type = "text/plain"
        startActivity(Intent.createChooser(shareIntent, resources.getString(R.string.app_name)))
    }

    private fun contactUs() {
        try {
            val sendIntentGmail = Intent(Intent.ACTION_SEND)
            sendIntentGmail.type = "plain/text"
            sendIntentGmail.setPackage("com.google.android.gm")
            sendIntentGmail.putExtra(Intent.EXTRA_EMAIL, arrayOf("your_email_id"))
            sendIntentGmail.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name) + " - Android")
            startActivity(sendIntentGmail)
        } catch (e: Exception) {
            val sendIntentIfGmailFail = Intent(Intent.ACTION_SEND)
            sendIntentIfGmailFail.type = "*/*"
            sendIntentIfGmailFail.putExtra(Intent.EXTRA_EMAIL, arrayOf("your_email_id"))
            sendIntentIfGmailFail.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name) + " - Android")
            if (sendIntentIfGmailFail.resolveActivity(packageManager) != null) {
                startActivity(sendIntentIfGmailFail)
            }
        }
    }


    //Todo--------------------------------------------Menu Items--------------------------------------------------
    private var doublePressToExit = false

    override fun onBackPressed() {
        try {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                return
            }
            if (doublePressToExit) {
                super.onBackPressed()
                return
            }
            doublePressToExit = true
            CommonUtilities.showToast(this, CommonConstants.MsgDoubleBackToExit)
            Handler().postDelayed({ doublePressToExit = false }, 2000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setAlarmManager() {
        try {
            val alarmStartTime = Calendar.getInstance()
            alarmStartTime.set(Calendar.HOUR_OF_DAY, 12)
            alarmStartTime.set(Calendar.MINUTE, 0)
            alarmStartTime.set(Calendar.SECOND, 0)
            val alarmIntent = Intent(this, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE)

            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                alarmStartTime.timeInMillis,
                pendingIntent
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun confirmationDialog(
        content: Context,
        strMsg: String
    ): Boolean {

        val builder1 = AlertDialog.Builder(content, R.style.AlertDialogTheme)
        builder1.setMessage(strMsg)
        builder1.setCancelable(true)

        builder1.setPositiveButton("Yes") { dialog, _ ->
            dialog.cancel()
            val homeIntent = Intent(Intent.ACTION_MAIN)
            homeIntent.addCategory(Intent.CATEGORY_HOME)
            homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(homeIntent)
            finishAffinity()
        }

        builder1.setNegativeButton("No") { dialog, _ ->
            dialog.cancel()
        }

        val alert11 = builder1.create()
        alert11.show()

        return false
    }


    fun isNetworkConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    }


    private fun subScribeToFirebaseTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("arms_workout_topic")
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e("subScribeFirebaseTopic", ": Fail")
                } else {
                    Log.e("subScribeFirebaseTopic", ": Success")
                }
            }
    }

    fun callGetAdsId() {
        try {
            if (isNetworkConnected()) {
                if (CommonConstants.ENABLE_DISABLE == CommonConstants.ENABLE) {
                    CommonUtilities.setPref(this, CommonConstants.AD_TYPE_FB_GOOGLE, CommonConstants.GOOGLE_FACEBOOK_Ad)
                    CommonUtilities.setPref(this, CommonConstants.FB_BANNER, CommonConstants.FB_BANNER_ID)
                    CommonUtilities.setPref(this, CommonConstants.FB_INTERSTITIAL, CommonConstants.FB_INTERSTITIAL_ID)
                    CommonUtilities.setPref(this, CommonConstants.GOOGLE_BANNER, CommonConstants.GOOGLE_BANNER_ID)
                    CommonUtilities.setPref(this, CommonConstants.GOOGLE_INTERSTITIAL, CommonConstants.GOOGLE_INTERSTITIAL_ID)
                    CommonUtilities.setPref(this, CommonConstants.STATUS_ENABLE_DISABLE, CommonConstants.ENABLE_DISABLE)
                    setAppAdId(CommonConstants.GOOGLE_ADMOB_APP_ID)
                } else {
                    CommonUtilities.setPref(this, CommonConstants.STATUS_ENABLE_DISABLE, CommonConstants.ENABLE_DISABLE)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    fun setAppAdId(id: String?) {
        try {
            val applicationInfo =
                packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            val bundle = applicationInfo.metaData
            val beforeChangeId = bundle.getString("com.google.android.gms.ads.APPLICATION_ID")
            Log.e("TAG", "setAppAdId:BeforeChange:::::  $beforeChangeId")
            applicationInfo.metaData.putString("com.google.android.gms.ads.APPLICATION_ID", id)
            val AfterChangeId = bundle.getString("com.google.android.gms.ads.APPLICATION_ID")
            Log.e("TAG", "setAppAdId:AfterChange::::  $AfterChangeId")
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

}