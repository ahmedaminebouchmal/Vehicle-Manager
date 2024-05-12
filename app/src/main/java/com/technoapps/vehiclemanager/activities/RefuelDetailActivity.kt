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
import android.view.View
import com.technoapps.vehiclemanager.R
import com.technoapps.vehiclemanager.common.CommonConstantAd
import com.technoapps.vehiclemanager.common.CommonConstants
import com.technoapps.vehiclemanager.common.CommonUtilities
import com.technoapps.vehiclemanager.common.DecimalDigitsInputFilter
import com.technoapps.vehiclemanager.databinding.ActivityRefuelDetailBinding
import com.technoapps.vehiclemanager.interfaces.RefuelListUploadCallback
import com.technoapps.vehiclemanager.pojo.RefuelClass
import com.technoapps.vehiclemanager.services.SetRefuelList
import java.text.SimpleDateFormat
import java.util.*

class RefuelDetailActivity : AppCompatActivity(), View.OnClickListener, RefuelListUploadCallback {

    private var vehicleId = ""
    private var vehicleMinDate = ""
    private var refuelId = ""
    private var isDataUpdated = false
    private lateinit var binding: ActivityRefuelDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRefuelDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setContentView(R.layout.activity_refuel_detail)
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

        binding.imgBack.setOnClickListener(this)

        binding.etDate.setOnClickListener(this)

        binding.etTotalAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                try {
                    val totalAmt = binding.etTotalAmount.text.toString()
                    if (totalAmt.isNotEmpty()) {
                        val intTotalAmt = totalAmt.toInt()
                        if (binding.etFuelPrice.text.toString().isNotEmpty()) {
                            try {
                                val intFuelPrice = binding.etFuelPrice.text.toString().toFloat()
                                val qty = (intTotalAmt / intFuelPrice)
                                binding.etQuantity.setText(String.format("%.2f", qty))
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    } else {
                        binding.etQuantity.setText("")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }
        })

        binding. etFuelPrice.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                try {
                    val totalAmt = binding.etTotalAmount.text.toString()
                    if (totalAmt.isNotEmpty()) {
                        val intTotalAmt = totalAmt.toInt()
                        if (binding.etFuelPrice.text.toString().isNotEmpty()) {
                            try {
                                val intFuelPrice = binding.etFuelPrice.text.toString().toFloat()
                                val qty = (intTotalAmt / intFuelPrice)
                                binding.etQuantity.setText(String.format("%.2f", qty))
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    } else {
                        binding.etQuantity.setText("")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
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


        binding.tvSave.setOnClickListener(this)
        binding.tvClear.setOnClickListener(this)
    }

    private fun loadDataFromIntent() {
        val intent = intent
        if (intent!!.hasExtra(CommonConstants.KeyVehicleId)) {
            try {
                vehicleId = intent.getStringExtra(CommonConstants.KeyVehicleId)!!
                vehicleMinDate = intent.getStringExtra(CommonConstants.KeyVehicleMinDate)!!
                val fuelType = intent.getStringExtra(CommonConstants.KeyIsFuelType)
                binding.etFuelType.setText(fuelType)
                Log.e("TAG", "loadDataFromIntent:::: $fuelType")
                if (intent.hasExtra(CommonConstants.KeyRefuelDetail)) {
                    val aClass = intent.getSerializableExtra(CommonConstants.KeyRefuelDetail) as RefuelClass
                    refuelId = aClass.refuelId

                    binding.etDate.setText(aClass.refuelDate)
                    binding.etFuelType.setText(aClass.refuelType)
                    Log.e("TAG", "loadDataFromIntent::::aClass    ${aClass.refuelType}")
                    try {
                        binding.etTotalAmount.setText(aClass.refuelAmount)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    binding.etFuelPrice.setText(aClass.refuelFuelPrice)
                    binding.etQuantity.setText(aClass.refuelQuantity)
                    binding.etFuelStation.setText(aClass.refuelStation)

                    binding.etKmReading.setText(aClass.refuelKmReading)
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

    private var datePickerDialog: DatePickerDialog? = null

    private fun openDatePickerDialog(context: Context) {
        try {
            if (datePickerDialog == null) {
                val calendar = Calendar.getInstance()
                val cYear = calendar.get(Calendar.YEAR)
                val cMonth = calendar.get(Calendar.MONTH)
                val cDay = calendar.get(Calendar.DAY_OF_MONTH)
                datePickerDialog =
                        DatePickerDialog(
                            context,
                            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                                try {
                                    try {
                                        calendar.set(Calendar.YEAR, year)
                                        calendar.set(Calendar.MONTH, month)
                                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                                        val sdf = SimpleDateFormat(
                                            CommonConstants.CapDateFormat,
                                            Locale.getDefault()
                                        )
                                        binding.etDate.setText(sdf.format(calendar.time))
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            },
                            cYear,
                            cMonth,
                            cDay
                        )
                datePickerDialog!!.datePicker.minDate =
                        CommonUtilities.stringDateToLongFormatForMinDate(vehicleMinDate)
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
            CommonUtilities.showToast(this, CommonConstants.MsgFuelDateRequired)
        } else if (binding.etFuelType.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgFuelTypeRequired)
        } else if (binding.etTotalAmount.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgFuelTotalAmountRequired)
        } else if (binding.etFuelPrice.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgFuelPriceRequired)
        } else if (binding.etKmReading.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgVehicleKmRequired)
        } else {
            if (CommonUtilities.isOnline(this)) {
                callSetFuelMasterService()
            } else {
                startActivity(Intent(this, NoInternetActivity::class.java))
            }
        }
    }

    private fun callSetFuelMasterService() {
        val aClass = RefuelClass()
        try {
            aClass.refuelId = refuelId
            aClass.refuelDate = binding.etDate.text.toString()
            aClass.refuelType = binding.etFuelType.text.toString()
            aClass.refuelAmount = binding.etTotalAmount.text.toString()
            aClass.refuelFuelPrice = binding.etFuelPrice.text.toString()
            aClass.refuelQuantity = binding.etQuantity.text.toString()
            aClass.refuelStation = binding.etFuelStation.text.toString()
            aClass.refuelKmReading = binding.etKmReading.text.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        SetRefuelList(this, vehicleId, aClass, this@RefuelDetailActivity).callSetRefuelListService()
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
        if (refuelId.isEmpty()) {
            binding.etDate.setText("")
            binding.etFuelType.setText("")
        }
        binding.etTotalAmount.setText("")
        binding.etFuelPrice.setText("")
        binding.etQuantity.setText("")
        binding.etFuelStation.setText("")
        binding.etKmReading.setText("")
    }

    override fun setRefuelDetailUploadCallback(isSuccess: Boolean) {
        if (isSuccess) {
            isDataUpdated = true
            CommonUtilities.setResultAndFinish(this, isDataUpdated)
        }
    }

    override fun onBackPressed() {
        CommonUtilities.showAlertFinishOnClick(this, isDataUpdated)
    }
}