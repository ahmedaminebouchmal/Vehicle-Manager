package com.technoapps.vehiclemanager.activities

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import com.technoapps.vehiclemanager.R
import com.technoapps.vehiclemanager.common.CommonConstantAd
import com.technoapps.vehiclemanager.common.CommonConstants
import com.technoapps.vehiclemanager.common.CommonUtilities
import com.technoapps.vehiclemanager.databinding.ActivityInsuranceDetailBinding
import com.technoapps.vehiclemanager.interfaces.InsuranceListUploadCallback
import com.technoapps.vehiclemanager.pojo.InsuranceClass
import com.technoapps.vehiclemanager.services.SetInsuranceList
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class InsuranceDetailActivity : AppCompatActivity(), View.OnClickListener, InsuranceListUploadCallback {

    private var vehicleId = ""
    private var vehicleMinDate = ""
    private var insuranceId = ""
    private var isDataUpdated = false

    private var issueDatePickerDialog: DatePickerDialog? = null
    private var expiryDatePickerDialog: DatePickerDialog? = null
    private lateinit var binding: ActivityInsuranceDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInsuranceDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setContentView(R.layout.activity_insurance_detail)
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
        binding.etPolicyType.setOnClickListener(this)
        binding.etIssueDate.setOnClickListener(this)
        binding.etExpiryDate.setOnClickListener(this)
        binding.etPaymentMode.setOnClickListener(this)

        binding.tvSave.setOnClickListener(this)
        binding.tvClear.setOnClickListener(this)
    }

    private fun loadDataFromIntent() {
        val intent = intent
        if (intent!!.hasExtra(CommonConstants.KeyVehicleId)) {
            try {
                vehicleId = intent.getStringExtra(CommonConstants.KeyVehicleId)!!
                vehicleMinDate = intent.getStringExtra(CommonConstants.KeyVehicleMinDate)!!
                if (intent.hasExtra(CommonConstants.KeyInsuranceDetail)) {
                    val aClass = intent.getSerializableExtra(CommonConstants.KeyInsuranceDetail) as InsuranceClass
                    insuranceId = aClass.insuranceId

                    binding.etCompany.setText(aClass.insuranceCompany)
                    binding.etPolicyType.setText(aClass.insurancePolicyType)
                    binding.etPolicyNo.setText(aClass.insurancePolicyNo)

                    binding.etIssueDate.setText(aClass.insuranceIssueDate)
                    binding.etExpiryDate.setText(aClass.insuranceExpiryDate)

                    binding.etPaymentMode.setText(aClass.insurancePaymentMode)

                    try {
                        binding.etInsuranceAmount.setText(aClass.insuranceAmount)
                        binding.etPremiumAmount.setText(aClass.insurancePremium)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    binding.etAgentName.setText(aClass.insuranceAgentName)
                    binding.etAgentContactNo.setText(aClass.insuranceAgentPhone)
                    binding.etDescription.setText(aClass.insuranceDescription)
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
        } else if (id == R.id.etPolicyType) {
            openPolicyTypeDialog(p0)
        } else if (id == R.id.etIssueDate) {
            openIssueDatePickerDialog(this)
        } else if (id == R.id.etExpiryDate) {
            openExpiryDatePickerDialog(this)
        } else if (id == R.id.etPaymentMode) {
            openPaymentModeMenu(p0)
        } else if (id == R.id.tvSave) {
            checkValidation()
        } else if (id == R.id.tvClear) {
            showClearDataConfirmDialog(this)
        }
    }

    private fun openPolicyTypeDialog(view: View) {
        val menu = PopupMenu(this, view, Gravity.END)
        menu.setOnMenuItemClickListener { item ->
            binding.etPolicyType.setText(item.title.toString())
            false
        }
        menu.inflate(R.menu.menu_policy_type)
        menu.show()
    }
    private fun openPaymentModeMenu(view: View) {
        val menu = PopupMenu(this, view, Gravity.END)
        menu.setOnMenuItemClickListener { item ->
            binding.etPaymentMode.setText(item.title.toString())
            false
        }
        menu.inflate(R.menu.menu_payment_mode)
        menu.show()
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
        if (binding.etCompany.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgInsuranceCompanyRequired)
        } else if (binding.etPolicyType.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgInsurancePolicyTypeRequired)
        } else if (binding.etPolicyNo.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgInsurancePolicyNoRequired)
        } else if (binding.etIssueDate.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgInsuranceIssueDateRequired)
        } else if (binding.etExpiryDate.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgInsuranceExpiryDateRequired)
        } else if (binding.etPaymentMode.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgInsurancePaymentModeRequired)
        }  else if (binding.etInsuranceAmount.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgInsuranceAmountRequired)
        } else if (binding.etPremiumAmount.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgInsurancePremiumRequired)
        } else {
            if (CommonUtilities.isOnline(this)) {
                callSetInsuranceMasterService()
            } else {
                startActivity(Intent(this, NoInternetActivity::class.java))
            }
        }
    }

    private fun callSetInsuranceMasterService() {
        val aClass = InsuranceClass()
        try {
            aClass.insuranceId = insuranceId
            aClass.insuranceCompany = binding.etCompany.text.toString()
            aClass.insurancePolicyType = binding.etPolicyType.text.toString()
            aClass.insurancePolicyNo = binding.etPolicyNo.text.toString()
            aClass.insuranceIssueDate = binding.etIssueDate.text.toString()
            aClass.insuranceExpiryDate = binding.etExpiryDate.text.toString()
            aClass.insurancePaymentMode = binding.etPaymentMode.text.toString()
            aClass.insuranceAmount = binding.etInsuranceAmount.text.toString()
            aClass.insurancePremium = binding.etPremiumAmount.text.toString()
            aClass.insuranceAgentName = binding.etAgentName.text.toString()
            aClass.insuranceAgentPhone = binding.etAgentContactNo.text.toString()
            aClass.insuranceDescription = binding.etDescription.text.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        SetInsuranceList(this, vehicleId, aClass, this@InsuranceDetailActivity).callSetInsuranceListService()
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
        binding.etCompany.setText("")
        binding.etPolicyType.setText("")
        binding.etPolicyNo.setText("")
        if (insuranceId.isEmpty()) {
            binding.etIssueDate.setText("")
            binding.etExpiryDate.setText("")
        }
        binding.etPaymentMode.setText("")
        binding.etInsuranceAmount.setText("")
        binding.etPremiumAmount.setText("")
        binding.etAgentName.setText("")
        binding.etAgentContactNo.setText("")
        binding.etDescription.setText("")
    }

    override fun setInsuranceDetailUploadCallback(isSuccess: Boolean) {
        if (isSuccess) {
            isDataUpdated = true
            CommonUtilities.setResultAndFinish(this, isDataUpdated)
        }
    }

    override fun onBackPressed() {
        CommonUtilities.showAlertFinishOnClick(this, isDataUpdated)
    }
}