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
import android.view.View
import android.widget.CompoundButton
import com.technoapps.vehiclemanager.R
import com.technoapps.vehiclemanager.common.CommonConstantAd
import com.technoapps.vehiclemanager.common.CommonConstants
import com.technoapps.vehiclemanager.common.CommonUtilities
import com.technoapps.vehiclemanager.common.DecimalDigitsInputFilter
import com.technoapps.vehiclemanager.databinding.ActivityServiceDetailBinding
import com.technoapps.vehiclemanager.interfaces.ServiceListUploadCallback
import com.technoapps.vehiclemanager.pojo.ServiceClass
import com.technoapps.vehiclemanager.services.SetServiceList

import java.text.SimpleDateFormat
import java.util.*

class ServiceDetailActivity : AppCompatActivity(), View.OnClickListener, ServiceListUploadCallback,
    CompoundButton.OnCheckedChangeListener {

    private var vehicleId = ""
    private var vehicleMinDate = ""
    private var serviceId = ""
    private var isDataUpdated = false
    private lateinit var binding: ActivityServiceDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServiceDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setContentView(R.layout.activity_service_detail)
        initViews()
        loadDataFromIntent()
//        CommonUtilities.loadBannerAd(findViewById(R.id.adViewBottom))
        if (CommonUtilities.getPref(this,CommonConstants.AD_TYPE_FB_GOOGLE,"") == CommonConstants.AD_GOOGLE &&
            CommonUtilities.getPref(this,CommonConstants.STATUS_ENABLE_DISABLE,"") == CommonConstants.ENABLE) {
            CommonConstantAd.loadBannerGoogleAd(this,binding.llAdView)
            binding.llAdViewFacebook.visibility= View.GONE
            binding.llAdView.visibility = View.VISIBLE
        } else if (CommonUtilities.getPref(this,CommonConstants.AD_TYPE_FB_GOOGLE,"") == CommonConstants.AD_FACEBOOK
            && CommonUtilities.getPref(this,CommonConstants.STATUS_ENABLE_DISABLE,"") == CommonConstants.ENABLE) {
            CommonConstantAd.loadBannerFacebookAd(this,binding.llAdViewFacebook)
            binding.llAdViewFacebook.visibility= View.VISIBLE
            binding.llAdView.visibility = View.GONE
        }else{
            binding.llAdView.visibility = View.GONE
            binding.llAdViewFacebook.visibility= View.GONE
        }
        CommonUtilities.addKeyboardDetectListener(this,binding.llAdView,binding.llAdViewFacebook)
    }

    private fun initViews() {
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


        binding.imgBack.setOnClickListener(this)
        binding.etDate.setOnClickListener(this)

        binding.chkBody.setOnCheckedChangeListener(this)
        binding.chkBrakes.setOnCheckedChangeListener(this)

        binding.chkClutch.setOnCheckedChangeListener(this)
        binding.chkCollingSystem.setOnCheckedChangeListener(this)

        binding.chkEngine.setOnCheckedChangeListener(this)
        binding.chkSparkPlug.setOnCheckedChangeListener(this)

        binding.chkGeneral.setOnCheckedChangeListener(this)
        binding.chkOther.setOnCheckedChangeListener(this)

        binding.chkOilChange.setOnCheckedChangeListener(this)
        binding.chkBattery.setOnCheckedChangeListener(this)

        binding.tvSave.setOnClickListener(this)
        binding.tvClear.setOnClickListener(this)
    }

    private fun loadDataFromIntent() {
        val intent = intent
        if (intent!!.hasExtra(CommonConstants.KeyVehicleId)) {
            try {
                vehicleId = intent.getStringExtra(CommonConstants.KeyVehicleId)!!
                vehicleMinDate = intent.getStringExtra(CommonConstants.KeyVehicleMinDate)!!
                if (intent.hasExtra(CommonConstants.KeyServiceDetail)) {
                    val aClass = intent.getSerializableExtra(CommonConstants.KeyServiceDetail) as ServiceClass
                    serviceId = aClass.serviceId

                    binding.etDate.setText(aClass.serviceDate)

                    binding.chkBody.isChecked = aClass.serviceBody == "1"
                    binding.chkBrakes.isChecked = aClass.serviceBrakes == "1"

                    binding.chkClutch.isChecked = aClass.serviceClutch == "1"
                    binding.chkCollingSystem.isChecked = aClass.serviceCollingSystem == "1"

                    binding.chkEngine.isChecked = aClass.serviceEngine == "1"
                    binding.chkSparkPlug.isChecked = aClass.serviceSparkPlug == "1"

                    binding.chkGeneral.isChecked = aClass.serviceGeneral == "1"
                    binding.chkOther.isChecked = aClass.serviceOther == "1"

                    binding.chkOilChange.isChecked = aClass.serviceOilChange == "1"
                    binding.chkBattery.isChecked = aClass.serviceBattery == "1"

                    binding.etGarage.setText(aClass.serviceGarageName)
                    binding.etContactNo.setText(aClass.serviceContactNo)
                    try {
                        binding.etTotalAmount.setText(aClass.serviceAmount)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    binding.etKmReading.setText(aClass.serviceKmReading)
                    binding.etDescription.setText(aClass.serviceDescription)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onClick(p0: View?) {
        val id = p0!!.id
        if (id == R.id.imgBack) {
            CommonUtilities.showAlertFinishOnClick(this, isDataUpdated)
        } else if (id == R.id.etDate) {
            openDatePickerDialog(this)
        } else if (id == R.id.tvSave) {
            checkValidation()
        } else if (id == R.id.tvClear) {
            showClearDataConfirmDialog(this)
        }
    }

    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {

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
                datePickerDialog!!.datePicker.minDate = CommonUtilities.stringDateToLongFormatForMinDate(vehicleMinDate)
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
        if (binding.etDate.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgServiceDateRequired)
        } else if (!checkAnyServiceChecked()) {
            CommonUtilities.showToast(this, CommonConstants.MsgSelectAnyService)
        } else if (binding.etTotalAmount.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgServiceTotalAmountRequired)
        } else {
            if (CommonUtilities.isOnline(this)) {
                callSetServiceMasterService()
            } else {
                startActivity(Intent(this, NoInternetActivity::class.java))
            }
        }
    }

    private fun checkAnyServiceChecked(): Boolean {
        var isChecked = false
        when {
            binding.chkBody.isChecked -> isChecked = true
            binding.chkBrakes.isChecked -> isChecked = true
            binding.chkClutch.isChecked -> isChecked = true
            binding.chkCollingSystem.isChecked -> isChecked = true
            binding.chkEngine.isChecked -> isChecked = true
            binding.chkSparkPlug.isChecked -> isChecked = true
            binding.chkGeneral.isChecked -> isChecked = true
            binding.chkOther.isChecked -> isChecked = true
            binding.chkOilChange.isChecked -> isChecked = true
            binding.chkBattery.isChecked -> isChecked = true
        }
        return isChecked
    }

    private fun callSetServiceMasterService() {
        val aClass = ServiceClass()
        try {
            aClass.serviceId = serviceId
            aClass.serviceDate = binding.etDate.text.toString()
            if (binding.chkBody.isChecked) {
                aClass.serviceBody = "1"
            } else {
                aClass.serviceBody = "0"
            }

            if (binding.chkBrakes.isChecked) {
                aClass.serviceBrakes = "1"
            } else {
                aClass.serviceBrakes = "0"
            }

            if (binding.chkClutch.isChecked) {
                aClass.serviceClutch = "1"
            } else {
                aClass.serviceClutch = "0"
            }

            if (binding.chkCollingSystem.isChecked) {
                aClass.serviceCollingSystem = "1"
            } else {
                aClass.serviceCollingSystem = "0"
            }

            if (binding.chkEngine.isChecked) {
                aClass.serviceEngine = "1"
            } else {
                aClass.serviceEngine = "0"
            }

            if (binding.chkSparkPlug.isChecked) {
                aClass.serviceSparkPlug = "1"
            } else {
                aClass.serviceSparkPlug = "0"
            }

            if (binding.chkGeneral.isChecked) {
                aClass.serviceGeneral = "1"
            } else {
                aClass.serviceGeneral = "0"
            }

            if (binding.chkOther.isChecked) {
                aClass.serviceOther = "1"
            } else {
                aClass.serviceOther = "0"
            }

            if (binding.chkOilChange.isChecked) {
                aClass.serviceOilChange = "1"
            } else {
                aClass.serviceOilChange = "0"
            }

            if (binding.chkBattery.isChecked) {
                aClass.serviceBattery = "1"
            } else {
                aClass.serviceBattery = "0"
            }


            aClass.serviceGarageName = binding.etGarage.text.toString()
            aClass.serviceContactNo = binding.etContactNo.text.toString()
            aClass.serviceAmount = binding.etTotalAmount.text.toString()
            aClass.serviceKmReading = binding.etKmReading.text.toString()
            aClass.serviceDescription = binding.etDescription.text.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        SetServiceList(this, vehicleId, aClass, this@ServiceDetailActivity).callSetServiceListService()
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

    private fun clearAllData() {
        if (serviceId.isEmpty()) {
            binding.etDate.setText("")
        }

        binding.chkBody.isChecked = false
        binding.chkBrakes.isChecked = false

        binding.chkClutch.isChecked = false
        binding.chkCollingSystem.isChecked = false

        binding.chkEngine.isChecked = false
        binding.chkSparkPlug.isChecked = false

        binding.chkGeneral.isChecked = false
        binding.chkOther.isChecked = false

        binding.chkOilChange.isChecked = false
        binding.chkBattery.isChecked = false

        binding.etGarage.setText("")
        binding.etTotalAmount.setText("")
        binding.etContactNo.setText("")
        binding.etKmReading.setText("")
        binding.etDescription.setText("")
    }


    override fun setServiceDetailUploadCallback(isSuccess: Boolean) {
        if (isSuccess) {
            isDataUpdated = true
            CommonUtilities.setResultAndFinish(this, isDataUpdated)
        }
    }

    override fun onBackPressed() {
        CommonUtilities.showAlertFinishOnClick(this, isDataUpdated)
    }
}