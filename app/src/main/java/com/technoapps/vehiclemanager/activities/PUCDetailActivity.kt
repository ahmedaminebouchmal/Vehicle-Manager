package com.technoapps.vehiclemanager.activities

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import com.technoapps.vehiclemanager.R
import com.technoapps.vehiclemanager.common.CommonConstantAd
import com.technoapps.vehiclemanager.common.CommonConstants
import com.technoapps.vehiclemanager.common.CommonUtilities
import com.technoapps.vehiclemanager.databinding.ActivityPucDetailBinding
import com.technoapps.vehiclemanager.interfaces.PUCListUploadCallback
import com.technoapps.vehiclemanager.pojo.PUCClass
import com.technoapps.vehiclemanager.services.SetPUCList
import java.text.SimpleDateFormat
import java.util.*

class PUCDetailActivity  : AppCompatActivity(), View.OnClickListener, PUCListUploadCallback {

    private var vehicleId = ""
    private var vehicleMinDate = ""
    private var pucId = ""
    private var isDataUpdated = false

    private var issueDatePickerDialog: DatePickerDialog? = null
    private var expiryDatePickerDialog: DatePickerDialog? = null

    private lateinit var binding: ActivityPucDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPucDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setContentView(R.layout.activity_puc_detail)
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
        binding.etIssueDate.setOnClickListener(this)
        binding.etExpiryDate.setOnClickListener(this)

        binding.tvSave.setOnClickListener(this)
        binding.tvClear.setOnClickListener(this)
    }

    private fun loadDataFromIntent() {
        val intent = intent
        if (intent!!.hasExtra(CommonConstants.KeyVehicleId)) {
            try {
                vehicleId = intent.getStringExtra(CommonConstants.KeyVehicleId)!!
                vehicleMinDate = intent.getStringExtra(CommonConstants.KeyVehicleMinDate)!!
                if (intent.hasExtra(CommonConstants.KeyPUCDetail)) {
                    val aClass = intent.getSerializableExtra(CommonConstants.KeyPUCDetail) as PUCClass
                    pucId = aClass.pucId

                    binding.etPUCNo.setText(aClass.pucNo)

                    binding.etIssueDate.setText(aClass.pucIssueDate)
                    binding.etExpiryDate.setText(aClass.pucExpiryDate)

                    try {
                        binding.etAmount.setText(aClass.pucAmount)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    binding.etDescription.setText(aClass.pucDescription)
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
        } else if (id == R.id.etIssueDate) {
            openIssueDatePickerDialog(this)
        } else if (id == R.id.etExpiryDate) {
            openExpiryDatePickerDialog(this)
        } else if (id == R.id.tvSave) {
            checkValidation()
        } else if (id == R.id.tvClear) {
            showClearDataConfirmDialog(this)
        }
    }

    private fun openIssueDatePickerDialog(context: Context) {
        try {
            if (issueDatePickerDialog == null) {
                val calendar = Calendar.getInstance()
                val cYear = calendar.get(Calendar.YEAR)
                val cMonth = calendar.get(Calendar.MONTH)
                val cDay = calendar.get(Calendar.DAY_OF_MONTH)
                issueDatePickerDialog =
                        DatePickerDialog(context, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                            try {
                                try {
                                    calendar.set(Calendar.YEAR, year)
                                    calendar.set(Calendar.MONTH, month)
                                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                                    val sdf = SimpleDateFormat(CommonConstants.CapDateFormat, Locale.getDefault())
                                    binding.etIssueDate.setText(sdf.format(calendar.time))
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }, cYear, cMonth, cDay)
                issueDatePickerDialog!!.datePicker.minDate = CommonUtilities.stringDateToLongFormatForMinDate(vehicleMinDate)
                issueDatePickerDialog!!.datePicker.maxDate = Date().time
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            if (!issueDatePickerDialog!!.isShowing) {
                issueDatePickerDialog!!.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openExpiryDatePickerDialog(context: Context) {
        try {
            if (expiryDatePickerDialog == null) {
                val calendar = Calendar.getInstance()
                val cYear = calendar.get(Calendar.YEAR)
                val cMonth = calendar.get(Calendar.MONTH)
                val cDay = calendar.get(Calendar.DAY_OF_MONTH)
                expiryDatePickerDialog =
                        DatePickerDialog(context, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                            try {
                                try {
                                    calendar.set(Calendar.YEAR, year)
                                    calendar.set(Calendar.MONTH, month)
                                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                                    val sdf = SimpleDateFormat(CommonConstants.CapDateFormat, Locale.getDefault())
                                    binding.etExpiryDate.setText(sdf.format(calendar.time))
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }, cYear, cMonth, cDay)
                expiryDatePickerDialog!!.datePicker.minDate = Date().time
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            if (!expiryDatePickerDialog!!.isShowing) {
                expiryDatePickerDialog!!.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkValidation() {
        if (binding.etPUCNo.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgPUCNoRequired)
        } else if (binding.etIssueDate.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgPUCIssueDateRequired)
        } else if (binding.etExpiryDate.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgPUCExpiryDateRequired)
        }  else if (binding.etAmount.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgPUCAmountRequired)
        } else {
            if (CommonUtilities.isOnline(this)) {
                callSetPUCMasterService()
            } else {
                startActivity(Intent(this, NoInternetActivity::class.java))
            }
        }
    }

    private fun callSetPUCMasterService() {
        val aClass = PUCClass()
        try {
            aClass.pucId = pucId
            aClass.pucNo = binding.etPUCNo.text.toString()
            aClass.pucIssueDate= binding.etIssueDate.text.toString()
            aClass.pucExpiryDate= binding.etExpiryDate.text.toString()
            aClass.pucAmount = binding.etAmount.text.toString()
            aClass.pucDescription = binding.etDescription.text.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        SetPUCList(this, vehicleId, aClass, this@PUCDetailActivity).callSetPUCListService()
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
        binding.etPUCNo.setText("")
        if (pucId.isEmpty()) {
           binding.etIssueDate.setText("")
           binding.etExpiryDate.setText("")
        }
        binding.etAmount.setText("")
        binding.etDescription.setText("")
    }

    override fun setPUCDetailUploadCallback(isSuccess: Boolean) {
        if (isSuccess) {
            isDataUpdated = true
            CommonUtilities.setResultAndFinish(this, isDataUpdated)
        }
    }

    override fun onBackPressed() {
        CommonUtilities.showAlertFinishOnClick(this, isDataUpdated)
    }
}