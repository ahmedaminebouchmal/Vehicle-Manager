package com.technoapps.vehiclemanager.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import com.technoapps.vehiclemanager.R
import com.technoapps.vehiclemanager.adapters.PermitListAdapter
import com.technoapps.vehiclemanager.common.CommonConstantAd
import com.technoapps.vehiclemanager.common.CommonConstants
import com.technoapps.vehiclemanager.common.CommonUtilities
import com.technoapps.vehiclemanager.databinding.ActivityPermitListBinding
import com.technoapps.vehiclemanager.interfaces.AdapterItemCallback
import com.technoapps.vehiclemanager.interfaces.PermitListDownloadCallback
import com.technoapps.vehiclemanager.pojo.PermitClass
import com.technoapps.vehiclemanager.pojo.VehicleClass
import com.technoapps.vehiclemanager.services.GetPermitList

class PermitListActivity : AppCompatActivity(), View.OnClickListener, PermitListDownloadCallback,
    AdapterItemCallback {

    private var permitClassArrayList = ArrayList<PermitClass>()
    private var vehicleId = ""
    private var vehicleMinDate = ""
    private var isDataUpdated = false
    private lateinit var binding: ActivityPermitListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermitListBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setContentView(R.layout.activity_permit_list)
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
        binding.rvPermitList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
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
                GetPermitList(this, vehicleId, this@PermitListActivity).callGetPermitListService()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun setPermitDetailDownloadCallback(permitClassArrayList: ArrayList<PermitClass>) {
        this.permitClassArrayList = permitClassArrayList
        if (permitClassArrayList.size > 0) {
            binding.rvPermitList.visibility = View.VISIBLE
            binding.llNoData.visibility = View.GONE
            val mLayoutManager = LinearLayoutManager(this)
            mLayoutManager.reverseLayout = true
            mLayoutManager.stackFromEnd = true
            binding.rvPermitList.layoutManager = mLayoutManager
            binding.rvPermitList.adapter = PermitListAdapter(this, permitClassArrayList, this@PermitListActivity)
        } else {
            binding.rvPermitList.visibility = View.GONE
            binding.llNoData.visibility = View.VISIBLE
        }
    }

    override fun onClick(p0: View?) {
        val id = p0!!.id
        if (id == R.id.imgBack) {
            onBackPressed()
        } else if (id == R.id.imgAdd) {
//            CommonUtilities.onClickAddVehicleDetail(this, this@PermitListActivity)
            openPermitDetailActivity(null)
        }
    }

    override fun onItemTypeClickCallback(mPos: Int) {
        if (CommonUtilities.isOnline(this)) {
            try {
                val aClass = permitClassArrayList[mPos]
                openPermitDetailActivity(aClass)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            startActivity(Intent(this, NoInternetActivity::class.java))
        }
    }

    /*override fun adLoadingFailed() {
        openPermitDetailActivity(null)
    }

    override fun adClose() {
        openPermitDetailActivity(null)
    }

    override fun startNextScreen() {
        openPermitDetailActivity(null)
    }*/

    private fun openPermitDetailActivity(aClass: PermitClass?) {
        val intent = Intent(this, PermitDetailActivity::class.java)
        intent.putExtra(CommonConstants.KeyVehicleId, vehicleId)
        intent.putExtra(CommonConstants.KeyVehicleMinDate, vehicleMinDate)
        if (aClass != null) {
            intent.putExtra(CommonConstants.KeyPermitDetail, aClass)
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
                    GetPermitList(this, vehicleId, this@PermitListActivity).callGetPermitListService()
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