package com.technoapps.vehiclemanager.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import com.technoapps.vehiclemanager.R
import com.technoapps.vehiclemanager.adapters.AccidentListAdapter
import com.technoapps.vehiclemanager.common.CommonConstantAd
import com.technoapps.vehiclemanager.common.CommonConstants
import com.technoapps.vehiclemanager.common.CommonUtilities
import com.technoapps.vehiclemanager.databinding.ActivityAccidentListBinding
import com.technoapps.vehiclemanager.interfaces.AccidentListDownloadCallback
import com.technoapps.vehiclemanager.interfaces.AdapterItemCallback
import com.technoapps.vehiclemanager.pojo.AccidentClass
import com.technoapps.vehiclemanager.pojo.VehicleClass
import com.technoapps.vehiclemanager.services.GetAccidentList

class AccidentListActivity : AppCompatActivity(), View.OnClickListener, AccidentListDownloadCallback,
    AdapterItemCallback {

    private var accidentClassArrayList = ArrayList<AccidentClass>()
    private var vehicleId = ""
    private var vehicleMinDate = ""
    private var isDataUpdated = false
    private lateinit var binding: ActivityAccidentListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccidentListBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setContentView(R.layout.activity_accident_list)
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
        binding.rvAccidentList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
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
                GetAccidentList(this, vehicleId, this@AccidentListActivity).callGetAccidentListService()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun setAccidentDetailDownloadCallback(accidentClassArrayList: ArrayList<AccidentClass>) {
        this.accidentClassArrayList = accidentClassArrayList
        if (accidentClassArrayList.size > 0) {
            binding.rvAccidentList.visibility = View.VISIBLE
            binding.llNoData.visibility = View.GONE
            val mLayoutManager = LinearLayoutManager(this)
            mLayoutManager.reverseLayout = true
            mLayoutManager.stackFromEnd = true
            binding.rvAccidentList.layoutManager = mLayoutManager
            binding.rvAccidentList.adapter = AccidentListAdapter(this, accidentClassArrayList, this@AccidentListActivity)
        } else {
            binding.rvAccidentList.visibility = View.GONE
            binding.llNoData.visibility = View.VISIBLE
        }
    }

    override fun onClick(p0: View?) {
        val id = p0!!.id
        if (id == R.id.imgBack) {
            onBackPressed()
        } else if (id == R.id.imgAdd) {
//            CommonUtilities.onClickAddVehicleDetail(this, this@AccidentListActivity)
            openAccidentDetailActivity(null)
        }
    }

    override fun onItemTypeClickCallback(mPos: Int) {
        if (CommonUtilities.isOnline(this)) {
            try {
                val aClass = accidentClassArrayList[mPos]
                openAccidentDetailActivity(aClass)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            startActivity(Intent(this, NoInternetActivity::class.java))
        }
    }


   /* override fun adLoadingFailed() {
        openAccidentDetailActivity(null)
    }

    override fun adClose() {
        openAccidentDetailActivity(null)
    }

    override fun startNextScreen() {
        openAccidentDetailActivity(null)
    }*/

    private fun openAccidentDetailActivity(aClass: AccidentClass?) {
        val intent = Intent(this, AccidentDetailActivity::class.java)
        intent.putExtra(CommonConstants.KeyVehicleId, vehicleId)
        intent.putExtra(CommonConstants.KeyVehicleMinDate, vehicleMinDate)
        if (aClass != null) {
            intent.putExtra(CommonConstants.KeyAccidentDetail, aClass)
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
                    GetAccidentList(this, vehicleId, this@AccidentListActivity).callGetAccidentListService()
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