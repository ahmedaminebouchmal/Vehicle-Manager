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
import com.technoapps.vehiclemanager.databinding.ActivityPermitDetailBinding
import com.technoapps.vehiclemanager.interfaces.PermitListUploadCallback
import com.technoapps.vehiclemanager.pojo.PermitClass
import com.technoapps.vehiclemanager.services.SetPermitList
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class PermitDetailActivity : AppCompatActivity(), PermitListUploadCallback, View.OnClickListener {

    private var vehicleId = ""
    private var vehicleMinDate = ""
    private var permitId = ""
    private var isDataUpdated = false

    private var issueDatePickerDialog: DatePickerDialog? = null
    private var expiryDatePickerDialog: DatePickerDialog? = null

    private lateinit var binding: ActivityPermitDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermitDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setContentView(R.layout.activity_permit_detail)
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
        binding.etPermitType.setOnClickListener(this)
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
                if (intent.hasExtra(CommonConstants.KeyPermitDetail)) {
                    val aClass = intent.getSerializableExtra(CommonConstants.KeyPermitDetail) as PermitClass
                    permitId = aClass.permitId

                    binding.etPermitType.setText(aClass.permitType)

                    binding.etIssueDate.setText(aClass.permitIssueDate)
                    binding.etExpiryDate.setText(aClass.permitExpiryDate)

                    try {
                        binding.etPermitNo.setText(aClass.permitNo)
                        binding.etPermitCost.setText(aClass.permitCost)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    binding.etDescription.setText(aClass.permitDescription)
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
        } else if (id == R.id.etPermitType) {
            openPermitTypeDialog(p0)
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

    private fun openPermitTypeDialog(view: View) {
        val menu = PopupMenu(this, view, Gravity.END)
        menu.setOnMenuItemClickListener { item ->
            binding.etPermitType.setText(item.title.toString())
            false
        }
        menu.inflate(R.menu.menu_permit_type)
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
        if (binding.etPermitType.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgPermitPolicyTypeRequired)
        } else if (binding.etIssueDate.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgPermitIssueDateRequired)
        } else if (binding.etExpiryDate.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgPermitExpiryDateRequired)
        } else if (binding.etPermitNo.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgPermitNoRequired)
        } else if (binding.etPermitCost.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgPermitCostRequired)
        } else {
            if (CommonUtilities.isOnline(this)) {
                callSetPermitMasterService()
            } else {
                startActivity(Intent(this, NoInternetActivity::class.java))
            }
        }
    }

    private fun callSetPermitMasterService() {
        val aClass = PermitClass()
        try {
            aClass.permitId = permitId
            aClass.permitType = binding.etPermitType.text.toString()
            aClass.permitIssueDate= binding.etIssueDate.text.toString()
            aClass.permitExpiryDate= binding.etExpiryDate.text.toString()
            aClass.permitNo = binding.etPermitNo.text.toString()
            aClass.permitCost = binding.etPermitCost.text.toString()
            aClass.permitDescription= binding.etDescription.text.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        SetPermitList(this, vehicleId, aClass, this@PermitDetailActivity).callSetPermitListService()
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
        binding.etPermitType.setText("")
        if (permitId.isEmpty()) {
            binding.etIssueDate.setText("")
            binding.etExpiryDate.setText("")
        }
        binding.etPermitNo.setText("")
        binding.etPermitCost.setText("")
        binding.etDescription.setText("")
    }

    override fun setPermitDetailUploadCallback(isSuccess: Boolean) {
        if (isSuccess) {
            isDataUpdated = true
            CommonUtilities.setResultAndFinish(this, isDataUpdated)
        }
    }

    override fun onBackPressed() {
        CommonUtilities.showAlertFinishOnClick(this, isDataUpdated)
    }

}

