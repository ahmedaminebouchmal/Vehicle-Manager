package com.technoapps.vehiclemanager.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.technoapps.vehiclemanager.R
import com.technoapps.vehiclemanager.adapters.RefuelListAdapter
import com.technoapps.vehiclemanager.common.CommonConstantAd
import com.technoapps.vehiclemanager.common.CommonConstants
import com.technoapps.vehiclemanager.common.CommonUtilities
import com.technoapps.vehiclemanager.databinding.ActivityRefuelListBinding
import com.technoapps.vehiclemanager.interfaces.AdapterItemCallback
import com.technoapps.vehiclemanager.interfaces.RefuelListDownloadCallback
import com.technoapps.vehiclemanager.pojo.RefuelClass
import com.technoapps.vehiclemanager.pojo.VehicleClass
import com.technoapps.vehiclemanager.services.GetRefuelList
import java.util.*

class RefuelListActivity : AppCompatActivity(), View.OnClickListener, RefuelListDownloadCallback,
    AdapterItemCallback {

    private var refuelClassArrayList = ArrayList<RefuelClass>()
    private var vehicleId = ""
    private var vehicleMinDate = ""
    private var isDataUpdated = false
    private var fuelType = ""
    private lateinit var binding: ActivityRefuelListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRefuelListBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setContentView(R.layout.activity_refuel_list)
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
        binding.rvRefuelList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
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
                fuelType = aClass.vehicleFuelType

                GetRefuelList(this, vehicleId, this@RefuelListActivity).callGetRefuelListService()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun setRefuelDetailDownloadCallback(refuelClassArrayList: ArrayList<RefuelClass>) {
        this.refuelClassArrayList = refuelClassArrayList
        if (refuelClassArrayList.size > 0) {
            binding.rvRefuelList.visibility = View.VISIBLE
            binding.llNoData.visibility = View.GONE
            val mLayoutManager = LinearLayoutManager(this)
            mLayoutManager.reverseLayout = true
            mLayoutManager.stackFromEnd = true
            binding.rvRefuelList.layoutManager = mLayoutManager
            binding.rvRefuelList.adapter = RefuelListAdapter(this, refuelClassArrayList, this@RefuelListActivity)
        } else {
            binding.rvRefuelList.visibility = View.GONE
            binding.llNoData.visibility = View.VISIBLE
        }
    }

    override fun onClick(p0: View?) {
        val id = p0!!.id
        if (id == R.id.imgBack) {
            onBackPressed()
        } else if (id == R.id.imgAdd) {
//            CommonUtilities.onClickAddVehicleDetail(this, this@RefuelListActivity)
            openRefuelDetailActivity(null)
        }
    }

    override fun onItemTypeClickCallback(mPos: Int) {
        if (CommonUtilities.isOnline(this)) {
            try {
                val aClass = refuelClassArrayList[mPos]
                openRefuelDetailActivity(aClass)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            startActivity(Intent(this, NoInternetActivity::class.java))
        }
    }

   /* override fun adLoadingFailed() {
        openRefuelDetailActivity(null)
    }

    override fun adClose() {
        openRefuelDetailActivity(null)
    }

    override fun startNextScreen() {
        openRefuelDetailActivity(null)
    }*/

    private fun openRefuelDetailActivity(aClass: RefuelClass?) {
        val intent = Intent(this, RefuelDetailActivity::class.java)
        intent.putExtra(CommonConstants.KeyVehicleId, vehicleId)
        intent.putExtra(CommonConstants.KeyVehicleMinDate, vehicleMinDate)
        intent.putExtra(CommonConstants.KeyIsFuelType, fuelType)
        if (aClass != null) {
            intent.putExtra(CommonConstants.KeyRefuelDetail, aClass)
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
                    GetRefuelList(this, vehicleId, this@RefuelListActivity).callGetRefuelListService()
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