package com.technoapps.vehiclemanager.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import com.technoapps.vehiclemanager.R
import com.technoapps.vehiclemanager.adapters.InsuranceListAdapter
import com.technoapps.vehiclemanager.common.CommonConstantAd
import com.technoapps.vehiclemanager.common.CommonConstants
import com.technoapps.vehiclemanager.common.CommonUtilities
import com.technoapps.vehiclemanager.databinding.ActivityInsuranceListBinding
import com.technoapps.vehiclemanager.interfaces.AdapterItemCallback
import com.technoapps.vehiclemanager.interfaces.InsuranceListDownloadCallback
import com.technoapps.vehiclemanager.pojo.InsuranceClass
import com.technoapps.vehiclemanager.pojo.VehicleClass
import com.technoapps.vehiclemanager.services.GetInsuranceList

class InsuranceListActivity : AppCompatActivity(), View.OnClickListener, InsuranceListDownloadCallback,
    AdapterItemCallback {

    private var insuranceClassArrayList = ArrayList<InsuranceClass>()
    private var vehicleId = ""
    private var vehicleMinDate = ""
    private var isDataUpdated = false
    private lateinit var binding: ActivityInsuranceListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInsuranceListBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setContentView(R.layout.activity_insurance_list)
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
        binding.rvInsuranceList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
            this,
            androidx.recyclerview.widget.LinearLayoutManager.VERTICAL,
            false
        )

        binding.imgBack.setOnClickListener(this)
        binding.imgAdd.setOnClickListener(this)
    }

    private fun loadDataFromIntent() {
        val intent = intent
        if (intent!!.hasExtra(CommonConstants.KeyVehicleDetail)) {
            try {
                val aClass = intent.getSerializableExtra(CommonConstants.KeyVehicleDetail) as VehicleClass
                vehicleId = aClass.vehicleId
                vehicleMinDate = aClass.vehiclePurchaseDate
                GetInsuranceList(this, vehicleId, this@InsuranceListActivity).callGetInsuranceListService()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun setInsuranceDetailDownloadCallback(insuranceClassArrayList: ArrayList<InsuranceClass>) {
        this.insuranceClassArrayList = insuranceClassArrayList
        if (insuranceClassArrayList.size > 0) {
            binding.rvInsuranceList.visibility = View.VISIBLE
            binding.llNoData.visibility = View.GONE
            val mLayoutManager = LinearLayoutManager(this)
            mLayoutManager.reverseLayout = true
            mLayoutManager.stackFromEnd = true
            binding.rvInsuranceList.layoutManager = mLayoutManager
            binding.rvInsuranceList.adapter = InsuranceListAdapter(this, insuranceClassArrayList, this@InsuranceListActivity)
        } else {
            binding.rvInsuranceList.visibility = View.GONE
            binding.llNoData.visibility = View.VISIBLE
        }
    }

    override fun onClick(p0: View?) {
        val id = p0!!.id
        if (id == R.id.imgBack) {
            onBackPressed()
        } else if (id == R.id.imgAdd) {
//            CommonUtilities.onClickAddVehicleDetail(this, this@InsuranceListActivity)
            openInsuranceDetailActivity(null)
        }
    }

    override fun onItemTypeClickCallback(mPos: Int) {
        if (CommonUtilities.isOnline(this)) {
            try {
                val aClass = insuranceClassArrayList[mPos]
                openInsuranceDetailActivity(aClass)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            startActivity(Intent(this, NoInternetActivity::class.java))
        }
    }

   /* override fun adLoadingFailed() {
        openInsuranceDetailActivity(null)
    }

    override fun adClose() {
        openInsuranceDetailActivity(null)
    }

    override fun startNextScreen() {
        openInsuranceDetailActivity(null)
    }
*/
    private fun openInsuranceDetailActivity(aClass: InsuranceClass?) {
        val intent = Intent(this, InsuranceDetailActivity::class.java)
        intent.putExtra(CommonConstants.KeyVehicleId, vehicleId)
        intent.putExtra(CommonConstants.KeyVehicleMinDate, vehicleMinDate)
        if (aClass != null) {
            intent.putExtra(CommonConstants.KeyInsuranceDetail, aClass)
        }
        startActivityForResult(intent, CommonConstants.RequestDataUpdated)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val key = CommonConstants.KeyIsDataUpdated
        if (resultCode == Activity.RESULT_OK) {
            if (data!!.hasExtra(key)) {
                if (data.getBooleanExtra(key, false)) {
                    isDataUpdated = true
                    GetInsuranceList(this, vehicleId, this@InsuranceListActivity).callGetInsuranceListService()
                }
            }
        }
    }

    override fun onBackPressed() {
        if (isDataUpdated) {
            val intent = Intent()
            intent.putExtra(CommonConstants.KeyIsDataUpdated, true)
            setResult(Activity.RESULT_OK, intent)
        }
        finish()
    }
}