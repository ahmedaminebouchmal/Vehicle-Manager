package com.technoapps.vehiclemanager.activities

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import com.google.gson.Gson
import com.technoapps.vehiclemanager.R
import com.technoapps.vehiclemanager.common.CommonConstantAd
import com.technoapps.vehiclemanager.common.CommonConstants
import com.technoapps.vehiclemanager.common.CommonUtilities
import com.technoapps.vehiclemanager.common.DecimalDigitsInputFilter
import com.technoapps.vehiclemanager.databinding.ActivityVehicleDetailBinding
import com.technoapps.vehiclemanager.interfaces.AdsCallback
import com.technoapps.vehiclemanager.interfaces.VehicleListUploadCallback
import com.technoapps.vehiclemanager.pojo.VehicleClass
import com.technoapps.vehiclemanager.services.SetVehicleList
import java.text.SimpleDateFormat
import java.util.*

class VehicleDetailActivity : AppCompatActivity(), View.OnClickListener, VehicleListUploadCallback, AdsCallback {

    private var vehicleId = ""
    private var isDataUpdated = false
    private lateinit var binding: ActivityVehicleDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVehicleDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setContentView(R.layout.activity_vehicle_detail)
        initViews()
        loadDataFromIntent()

        if (CommonUtilities.getPref(this,CommonConstants.AD_TYPE_FB_GOOGLE,"") == CommonConstants.AD_GOOGLE
            && CommonUtilities.getPref(this,CommonConstants.STATUS_ENABLE_DISABLE,"") == CommonConstants.ENABLE) {
            CommonConstantAd.GooglebeforloadAd(this)
        } else if (CommonUtilities.getPref(this,CommonConstants.AD_TYPE_FB_GOOGLE,"") == CommonConstants.AD_FACEBOOK
            && CommonUtilities.getPref(this,CommonConstants.STATUS_ENABLE_DISABLE,"") == CommonConstants.ENABLE) {
            CommonConstantAd.FacebookbeforeloadFullAd(this)
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
        CommonUtilities.addKeyboardDetectListener(this,binding.llAdView,binding.llAdViewFacebook)
    }

    private fun initViews() {
        binding.imgBack.setOnClickListener(this)
        binding.tvType.setOnClickListener(this)

        binding.etTitle.setOnClickListener(this)
        binding.etFuelType.setOnClickListener(this)

        binding.etBuildYear.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val calendar = Calendar.getInstance()
                val cYear = calendar.get(Calendar.YEAR)
                val data = p0!!.toString()
                if (data.isNotEmpty() && data.toInt() <= cYear) {
                    binding.etDisplayName.setText(data.plus(" - ").plus(binding.etRegistrationNo.text.toString()))
                } else {
                    binding.etDisplayName.setText("")
                }
            }

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }
        })

        binding.etRegistrationNo.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val data = p0!!.toString()
                binding.etDisplayName.setText(binding.etBuildYear.text.toString().plus(" - ").plus(data))
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        binding.etKmReading.filters = arrayOf<InputFilter>(DecimalDigitsInputFilter(7, 1));
        binding.etKmReading.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val data = CommonUtilities.getKmReadingData(p0)
                if (data.isNotEmpty()) {
                    binding.etKmReading.setText(data)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })

        binding.etDate.setOnClickListener(this)
        binding.tvSave.setOnClickListener(this)
        binding.tvClear.setOnClickListener(this)
    }

    private fun loadDataFromIntent() {
        val intent = intent
        if (intent!!.hasExtra(CommonConstants.KeyVehicleDetail)) {
            try {
                val aClass = intent.getSerializableExtra(CommonConstants.KeyVehicleDetail) as VehicleClass

                vehicleId = aClass.vehicleId
                binding.tvType.text = aClass.vehicleType
                binding.etTitle.setText(aClass.vehicleTitle)
                binding.etBrand.setText(aClass.vehicleBrand)
                binding.etModel.setText(aClass.vehicleModel)

                binding.etBuildYear.setText(aClass.vehicleBuildYear)
                binding.etRegistrationNo.setText(aClass.vehicleRegistrationNo)
                binding.etFuelType.setText(aClass.vehicleFuelType)

                binding.etTankCapacity.setText(aClass.vehicleTankCapacity)
                binding.etDisplayName.setText(aClass.vehicleDisplayName)
                binding.etDate.setText(aClass.vehiclePurchaseDate)

                binding.etPurchasePrice.setText(aClass.vehiclePurchasePrice)
                binding.etKmReading.setText(aClass.vehicleKmReading)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onClick(p0: View?) {
        val id = p0!!.id
        if (id == R.id.imgBack) {
            CommonUtilities.showAlertFinishOnClick(this, isDataUpdated)
        } else if (id == R.id.tvType) {
            openVehicleTypeMenu(p0)
        } else if (id == R.id.etTitle) {
            openVehicleTitleMenu(p0)
        } else if (id == R.id.etFuelType) {
            openFuelTypeMenu(p0)
        } else if (id == R.id.etDate) {
            openDatePickerDialog(this)
        } else if (id == R.id.tvSave) {
            checkValidation()
        } else if (id == R.id.tvClear) {
            showClearDataConfirmDialog(this)
        }
    }

    private fun openVehicleTypeMenu(view: View) {
        val menu = PopupMenu(this, view, Gravity.END)
        menu.setOnMenuItemClickListener { item ->
            binding.tvType.text = item.title.toString()
            false
        }
        menu.inflate(R.menu.menu_vehicle_type)
        menu.show()
    }

    private fun openVehicleTitleMenu(view: View) {
        val menu = PopupMenu(this, view, Gravity.END)
        menu.setOnMenuItemClickListener { item ->
            binding.etTitle.setText(item.title.toString())
            false
        }
        menu.inflate(R.menu.menu_vehicle_title)
        menu.show()
    }

    private fun openFuelTypeMenu(view: View) {
        val menu = PopupMenu(this, view, Gravity.END)
        menu.setOnMenuItemClickListener { item ->
            binding.etFuelType.setText(item.title.toString())
            false
        }
        menu.inflate(R.menu.menu_fuel_type)
        menu.show()
    }

    private var datePickerDialog: DatePickerDialog? = null

    private fun openDatePickerDialog(context: Context) {
        try {
            if (datePickerDialog == null) {
                val calendar = Calendar.getInstance()
                val cYear = calendar.get(Calendar.YEAR)
                val cMonth = calendar.get(Calendar.MONTH)
                val cDay = calendar.get(Calendar.DAY_OF_MONTH)
                datePickerDialog =
                    DatePickerDialog(context, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                        try {
                            try {
                                calendar.set(Calendar.YEAR, year)
                                calendar.set(Calendar.MONTH, month)
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                                val sdf = SimpleDateFormat(CommonConstants.CapDateFormat, Locale.getDefault())
                                binding.etDate.setText(sdf.format(calendar.time))
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, cYear, cMonth, cDay)
                datePickerDialog!!.datePicker.maxDate = Date().time
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            if (!datePickerDialog!!.isShowing) {
                datePickerDialog!!.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkValidation() {
        if (binding.etTitle.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgVehicleTypeRequired)
        } else if (binding.etBrand.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgVehicleBrandRequired)
        } else if (binding.etModel.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgVehicleModelRequired)
        } else if (binding.etBuildYear.text.toString().isEmpty() || binding.etBuildYear.text.toString().length != 4) {
            CommonUtilities.showToast(this, CommonConstants.MsgVehicleBuildYearRequired)
        } else if (binding.etBuildYear.text.toString().length != 4) {
            CommonUtilities.showToast(this, CommonConstants.MsgEnterCorrectBuildYear)
        } else if (binding.etRegistrationNo.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgVehicleRegNoRequired)
        } else if (binding.etFuelType.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgVehicleFuelTypeRequired)
        } else if (binding.etPurchasePrice.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgVehiclePurchasePriceRequired)
        } else {
            if (CommonUtilities.isOnline(this)) {
//                callSetVehicleMasterService()
                showFullAd()
            } else {
                startActivity(Intent(this, NoInternetActivity::class.java))
            }
        }
    }

    private fun showFullAd() {
        if (CommonUtilities.getPref(this, CommonConstants.EXIT_BTN_COUNT, 1) == 1) {
            if (CommonUtilities.getPref(this, CommonConstants.AD_TYPE_FB_GOOGLE, "") == CommonConstants.AD_GOOGLE
                && CommonUtilities.getPref(this, CommonConstants.STATUS_ENABLE_DISABLE, "") == CommonConstants.ENABLE
            ) {
                CommonConstantAd.showInterstitialAdsGoogle(this,this)
            } else if (CommonUtilities.getPref(this, CommonConstants.AD_TYPE_FB_GOOGLE, "") == CommonConstants.AD_FACEBOOK
                && CommonUtilities.getPref(this, CommonConstants.STATUS_ENABLE_DISABLE, "") == CommonConstants.ENABLE
            ) {
                CommonConstantAd.showInterstitialAdsFacebook(this)
            } else {
                callSetVehicleMasterService()
            }
            CommonUtilities.setPref(this, CommonConstants.EXIT_BTN_COUNT, 2)
        } else {
            CommonUtilities.setPref(this, CommonConstants.EXIT_BTN_COUNT, 1)
            callSetVehicleMasterService()
        }
    }

    private fun callSetVehicleMasterService() {
        val aClass = VehicleClass()
        try {
            aClass.deviceId = CommonUtilities.getAndroidId(this)
            aClass.vehicleId = vehicleId
            aClass.vehicleType = binding.tvType.text.toString()
            aClass.vehicleTitle = binding.etTitle.text.toString()
            aClass.vehicleBrand = binding.etBrand.text.toString()
            aClass.vehicleModel = binding.etModel.text.toString()
            aClass.vehicleBuildYear = binding.etBuildYear.text.toString()
            aClass.vehicleRegistrationNo = binding.etRegistrationNo.text.toString()
            aClass.vehicleFuelType = binding.etFuelType.text.toString()
            aClass.vehicleTankCapacity = binding.etTankCapacity.text.toString()
            aClass.vehicleDisplayName = binding.etDisplayName.text.toString()
            aClass.vehiclePurchaseDate = binding.etDate.text.toString()
            aClass.vehiclePurchasePrice = binding.etPurchasePrice.text.toString()
            aClass.vehicleKmReading = binding.etKmReading.text.toString()
            Log.e("TAG", "callSetVehicleMasterService:All Data::::::  " + Gson().toJson(aClass))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        SetVehicleList(this, aClass, this@VehicleDetailActivity).callSetVehicleListService()
    }

    private fun showClearDataConfirmDialog(context: Context) {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle(CommonConstants.CapConfirm)
        alertDialog.setMessage(CommonConstants.MsgDoYouWantToClear)
        alertDialog.setPositiveButton(CommonConstants.CapClear) { dialog, _ ->
            dialog.dismiss()
            clearAllData()
        }
        alertDialog.setNegativeButton(CommonConstants.CapCancel) { dialog, _ -> dialog.dismiss() }
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    override fun setVehicleDetailUploadCallback(isSuccess: Boolean) {
        if (isSuccess) {
            isDataUpdated = true
            CommonUtilities.setResultAndFinish(this, isDataUpdated)
        }
    }

    private fun clearAllData() {
        binding.tvType.text = resources.getString(R.string.vehicle_condition_old)
        binding.etTitle.setText("")
        binding.etBrand.setText("")
        binding.etModel.setText("")

        binding.etBuildYear.setText("")
        binding.etRegistrationNo.setText("")
        binding.etFuelType.setText("")

        binding.etTankCapacity.setText("")
        binding.etDisplayName.setText("")
        binding.etDate.setText("")

        binding.etPurchasePrice.setText("")
        binding.etKmReading.setText("")
    }

    override fun onBackPressed() {
        CommonUtilities.showAlertFinishOnClick(this, isDataUpdated)
    }



    override fun startNextScreen() {
        callSetVehicleMasterService()
    }

    override fun onLoaded() {

    }

}


/*
        fun popupWindowDogs(): PopupWindow {
                val popupWindow = PopupWindow(this)
                val listViewDogs = ListView(this)
                listViewDogs.adapter = dogsAdapter(popUpContents)
                listViewDogs.onItemClickListener = DogsDropdownOnItemClickListener()
                popupWindow.isFocusable = true
                popupWindow.width = 250
                popupWindow.height = WindowManager.LayoutParams.WRAP_CONTENT
                popupWindow.contentView = listViewDogs
                return popupWindow
         }

        private fun dogsAdapter(dogsArray: Array<String>): ArrayAdapter<String> {
            return object : ArrayAdapter<String>(this, R.layout.custom_spinner_layout, dogsArray) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

                    val item = getItem(position)
                    val itemArr = item!!.split("::".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val text = itemArr[0]
                    val id = itemArr[1]

                    val listItem = TextView(this@VehicleDetailActivity)
                    listItem.text = text
                    listItem.tag = id
                    listItem.textSize = 22f
                    listItem.setPadding(10, 10, 10, 10)
                    listItem.setTextColor(Color.WHITE)
                    return listItem
                }
            }
        }

    inner class DogsDropdownOnItemClickListener : OnItemClickListener {

        override fun onItemClick(arg0: AdapterView<*>, v: View, arg2: Int, arg3: Long) {

            val mContext = v.context
            val vdActivity = mContext as VehicleDetailActivity
            val fadeInAnimation = AnimationUtils.loadAnimation(v.context, android.R.anim.fade_in)
            fadeInAnimation.duration = 10
            v.startAnimation(fadeInAnimation)
            vdActivity.popupWindowDogs!!.dismiss()
            val selectedItemText = (v as TextView).text.toString()
            vdActivity.buttonShowDropDown.setText(selectedItemText)
            val selectedItemTag = v.tag.toString()
            Toast.makeText(mContext, "Dog ID is: $selectedItemTag", Toast.LENGTH_SHORT).show()
        }
    }






















    //    private var mDropdown: PopupWindow? = null
//    private fun initiatePopupWindow(view: View): PopupWindow {
//        try {
//            val mInflater: LayoutInflater = applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//            val layout = mInflater.inflate(R.layout.custom_spinner_layout, null)
//            val itema = layout.findViewById(R.id.ItemA) as TextView
//            val itemb = layout.findViewById(R.id.ItemB) as TextView
//            layout.measure(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//            )
//            val margin = resources.getDimension(R.dimen.twenty_dp).toInt()
//            val linearLayout = LinearLayout(this)
//            val layoutParams = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//            )
//            val layoutParams = layout.layoutParams as LinearLayout.LayoutParams
//            layoutParams.setMargins(margin, 0, margin, 0)
//            layout.layoutParams = layoutParams
//            layout.setPadding(margin, 0, margin, 0)
//            layout.minimumWidth = layout.width-100
//            layout.minimumHeight = layout.height-100
//            mDropdown = PopupWindow(
//                layout,
//                FrameLayout.LayoutParams.MATCH_PARENT,
//                FrameLayout.LayoutParams.WRAP_CONTENT,
//                true
//            )
//            val background = resources.getDrawable(android.R.drawable.editbox_dropdown_dark_frame)
//            mDropdown!!.setBackgroundDrawable(background)
//            mDropdown!!.showAsDropDown(view, 10, 10)
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        return mDropdown!!
//    }
*/