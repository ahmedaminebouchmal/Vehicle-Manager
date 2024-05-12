package com.technoapps.vehiclemanager.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.CompoundButton
import com.technoapps.vehiclemanager.R
import com.technoapps.vehiclemanager.common.CommonConstantAd
import com.technoapps.vehiclemanager.common.CommonConstants
import com.technoapps.vehiclemanager.common.CommonUtilities
import com.technoapps.vehiclemanager.databinding.ActivitySummaryBinding
import com.technoapps.vehiclemanager.interfaces.SummaryDownloadCallback
import com.technoapps.vehiclemanager.pojo.SummaryClass
import com.technoapps.vehiclemanager.pojo.VehicleClass
import com.technoapps.vehiclemanager.services.GetSummaryList

class SummaryActivity : AppCompatActivity(), SummaryDownloadCallback, View.OnClickListener,
    CompoundButton.OnCheckedChangeListener {

    private var summaryClassArrayList = ArrayList<SummaryClass>()
    private var vehicleId = ""
    private var isDataUpdated = false
    private lateinit var binding: ActivitySummaryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setContentView(R.layout.activity_summary)

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
        binding.chkIncludePurchasePrice.setOnCheckedChangeListener(this)
    }

    private fun loadDataFromIntent() {
        val intent = intent
        if (intent!!.hasExtra(CommonConstants.KeyVehicleDetail)) {
            try {
                val aClass = intent.getSerializableExtra(CommonConstants.KeyVehicleDetail) as VehicleClass
                vehicleId = aClass.vehicleId
                GetSummaryList(this, vehicleId, this@SummaryActivity).callGetSummaryListService()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun setSummaryDetailDownloadCallback(summaryClassArrayList: ArrayList<SummaryClass>) {
        this.summaryClassArrayList = summaryClassArrayList
        try {
            if (summaryClassArrayList.size > 0) {
                val aClass = summaryClassArrayList[0]
                binding.tvTotalExpenditurePrice.text = aClass.totalExpenseWithoutPrice
                binding.tvPurchasePrice.text = aClass.vehiclePurchasePrice
                binding.tvRefuelPrice.text = aClass.refuelAmount
                binding.tvServicePrice.text = aClass.serviceAmount
                binding.tvExpensePrice.text = aClass.expenseDetailAmount
                binding.tvInsurancePrice.text = aClass.insuranceAmount
                binding.tvPermitPrice.text = aClass.permitCost
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(p0: View?) {
        val id = p0!!.id
        if (id == R.id.imgBack) {
            CommonUtilities.showAlertFinishOnClick(this, isDataUpdated)
        }
    }

    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
        try {
            if (summaryClassArrayList.size > 0) {
                val aClass = summaryClassArrayList[0]
                if (p1) {
                    binding.tvTotalExpenditurePrice.text = aClass.totalExpenseWithPrice
                } else {
                    binding.tvTotalExpenditurePrice.text = aClass.totalExpenseWithoutPrice
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
