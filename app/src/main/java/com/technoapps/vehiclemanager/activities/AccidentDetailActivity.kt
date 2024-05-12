package com.technoapps.vehiclemanager.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.view.View
import com.technoapps.vehiclemanager.R
import com.technoapps.vehiclemanager.common.CommonConstants
import com.technoapps.vehiclemanager.common.CommonUtilities
import com.technoapps.vehiclemanager.interfaces.AccidentListUploadCallback
import com.technoapps.vehiclemanager.pojo.AccidentClass
import com.technoapps.vehiclemanager.services.SetAccidentList
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import android.text.InputFilter
import android.text.TextWatcher
import com.technoapps.vehiclemanager.common.CommonConstantAd
import com.technoapps.vehiclemanager.common.DecimalDigitsInputFilter
import com.technoapps.vehiclemanager.databinding.ActivityAccidentDetailBinding


class AccidentDetailActivity : AppCompatActivity(), View.OnClickListener, AccidentListUploadCallback {

    private var vehicleId = ""
    private var vehicleMinDate = ""
    private var accidentId = ""
    private var isDataUpdated = false
    private lateinit var binding: ActivityAccidentDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccidentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setContentView(R.layout.activity_accident_detail)
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
        binding.etTime.setOnClickListener(this)
        binding.tvSave.setOnClickListener(this)
        binding.tvClear.setOnClickListener(this)
    }

    private fun loadDataFromIntent() {
        val intent = intent
        if (intent!!.hasExtra(CommonConstants.KeyVehicleId)) {
            try {
                vehicleId = intent.getStringExtra(CommonConstants.KeyVehicleId)!!
                vehicleMinDate = intent.getStringExtra(CommonConstants.KeyVehicleMinDate)!!
                if (intent.hasExtra(CommonConstants.KeyAccidentDetail)) {
                    val aClass = intent.getSerializableExtra(CommonConstants.KeyAccidentDetail) as AccidentClass
                    accidentId = aClass.accidentId

                    binding.etDate.setText(aClass.accidentDate)
                    binding.etTime.setText(aClass.accidentTime)

                    binding.etDriverName.setText(aClass.accidentDriverName)

                    try {
                        binding.etTotalAmount.setText(aClass.accidentAmount)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    binding.etKmReading.setText(aClass.accidentKmReading)
                    binding.etDescription.setText(aClass.accidentDescription)
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
        } else if (id == R.id.etTime) {
            openTimePickerDialog(this)
        } else if (id == R.id.tvSave) {
            checkValidation()
        } else if (id == R.id.tvClear) {
            showClearDataConfirmDialog(this)
        }
    }

    private var datePickerDialog: DatePickerDialog? = null

    private fun openDatePickerDialog(context: Context) {
        try {
            if (datePickerDialog == null) {
                val calendar = Calendar.getInstance()
                val cYear = calendar.get(Calendar.YEAR)
                val cMonth = calendar.get(Calendar.MONTH)
                val cDay = calendar.get(Calendar.DAY_OF_MONTH) - 1
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

    private var timePickerDialog: TimePickerDialog? = null

    private fun openTimePickerDialog(context: Context) {
        if (timePickerDialog == null) {
            try {
                val calendar = Calendar.getInstance()
                val cHour = calendar.get(Calendar.HOUR_OF_DAY)
                val cMin = calendar.get(Calendar.MINUTE)
                timePickerDialog =
                        TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                            val finalTime = "$hourOfDay:$minute"
                            binding.etTime.setText(CommonUtilities.get12HourFormatTime(finalTime))
                        }, cHour, cMin, true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        try {
            if (!timePickerDialog!!.isShowing) {
                timePickerDialog!!.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkValidation() {
        if (binding.etDate.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgAccidentDateRequired)
        } else if (binding.etTime.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgAccidentTimeRequired)
        } else if (binding.etTotalAmount.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgAccidentAmountRequired)
        } else if (binding.etKmReading.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgAccidentKmRequired)
        } else {
            if (CommonUtilities.isOnline(this)) {
                callSetAccidentMasterService()
            } else {
                startActivity(Intent(this, NoInternetActivity::class.java))
            }
        }
    }

    private fun callSetAccidentMasterService() {
        val aClass = AccidentClass()
        try {
            aClass.accidentId = accidentId
            aClass.accidentDate = binding.etDate.text.toString()
            aClass.accidentTime = binding.etTime.text.toString()
            aClass.accidentDriverName = binding.etDriverName.text.toString()
            aClass.accidentAmount = binding.etTotalAmount.text.toString()
            aClass.accidentKmReading = binding.etKmReading.text.toString()
            aClass.accidentDescription = binding.etDescription.text.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        SetAccidentList(this, vehicleId, aClass, this@AccidentDetailActivity).callSetAccidentListService()
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
        if (accidentId.isEmpty()) {
            binding.etDate.setText("")
            binding.etTime.setText("")
        }
        binding.etDriverName.setText("")
        binding.etTotalAmount.setText("")
        binding.etKmReading.setText("")
        binding.etDescription.setText("")
    }


    override fun setAccidentDetailUploadCallback(isSuccess: Boolean) {
        if (isSuccess) {
            isDataUpdated = true
            CommonUtilities.setResultAndFinish(this, isDataUpdated)
        }
    }

    override fun onBackPressed() {
        CommonUtilities.showAlertFinishOnClick(this, isDataUpdated)
    }
}