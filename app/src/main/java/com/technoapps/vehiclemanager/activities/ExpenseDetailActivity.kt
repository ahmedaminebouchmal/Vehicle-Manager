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
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import com.technoapps.vehiclemanager.R
import com.technoapps.vehiclemanager.common.CommonConstantAd
import com.technoapps.vehiclemanager.common.CommonConstants
import com.technoapps.vehiclemanager.common.CommonUtilities
import com.technoapps.vehiclemanager.common.DecimalDigitsInputFilter
import com.technoapps.vehiclemanager.databinding.ActivityExpenseDetailBinding
import com.technoapps.vehiclemanager.interfaces.ExpenseListUploadCallback
import com.technoapps.vehiclemanager.pojo.ExpenseClass
import com.technoapps.vehiclemanager.services.SetExpenseList
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class ExpenseDetailActivity : AppCompatActivity(), View.OnClickListener, ExpenseListUploadCallback {

    private var vehicleId = ""
    private var vehicleMinDate = ""
    private var expenseId = ""
    private var isDataUpdated = false
    private lateinit var binding: ActivityExpenseDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setContentView(R.layout.activity_expense_detail)
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

        binding.etExpenseType.setOnClickListener(this)

        binding.tvSave.setOnClickListener(this)
        binding.tvClear.setOnClickListener(this)
    }

    private fun loadDataFromIntent() {
        val intent = intent
        if (intent!!.hasExtra(CommonConstants.KeyVehicleId)) {
            try {
                vehicleId = intent.getStringExtra(CommonConstants.KeyVehicleId)!!
                vehicleMinDate = intent.getStringExtra(CommonConstants.KeyVehicleMinDate)!!
                if (intent.hasExtra(CommonConstants.KeyExpenseDetail)) {
                    val aClass = intent.getSerializableExtra(CommonConstants.KeyExpenseDetail) as ExpenseClass
                    expenseId = aClass.expenseId

                    binding.etDate.setText(aClass.expenseDate)
                    binding.etExpenseType.setText(aClass.expenseType)

                    try {
                        binding.etTotalAmount.setText(aClass.expenseAmount)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    binding.etKmReading.setText(aClass.expenseKmReading)
                    binding.etDescription.setText(aClass.expenseDescription)
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
        }  else if (id == R.id.etExpenseType) {
            openExpenseTypeMenu(p0)
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

    private fun openExpenseTypeMenu(view: View) {
        val menu = PopupMenu(this, view, Gravity.END)
        menu.setOnMenuItemClickListener { item ->
            binding.etExpenseType.setText(item.title.toString())
            false
        }
        menu.inflate(R.menu.menu_expense_type)
        menu.show()
    }

    private fun checkValidation() {
        if (binding.etDate.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgExpenseDateRequired)
        } else if (binding.etExpenseType.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgExpenseTypeRequired)
        } else if (binding.etTotalAmount.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgExpenseTotalAmountRequired)
        } else {
            if (CommonUtilities.isOnline(this)) {
                callSetExpenseMasterService()
            } else {
                startActivity(Intent(this, NoInternetActivity::class.java))
            }
        }
    }

    private fun callSetExpenseMasterService() {
        val aClass = ExpenseClass()
        try {
            aClass.expenseId = expenseId
            aClass.expenseDate = binding.etDate.text.toString()
            aClass.expenseType = binding.etExpenseType.text.toString()
            aClass.expenseAmount = binding.etTotalAmount.text.toString()
            aClass.expenseKmReading = binding.etKmReading.text.toString()
            aClass.expenseDescription = binding.etDescription.text.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        SetExpenseList(this, vehicleId, aClass, this@ExpenseDetailActivity).callSetExpenseListService()
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
        if (expenseId.isEmpty()) {
            binding.etDate.setText("")
        }

        binding.etTotalAmount.setText("")
        binding.etKmReading.setText("")
        binding.etDescription.setText("")
    }


    override fun setExpenseDetailUploadCallback(isSuccess: Boolean) {
        if (isSuccess) {
            isDataUpdated = true
            CommonUtilities.setResultAndFinish(this, isDataUpdated)
        }
    }

    override fun onBackPressed() {
        CommonUtilities.showAlertFinishOnClick(this, isDataUpdated)
    }
}