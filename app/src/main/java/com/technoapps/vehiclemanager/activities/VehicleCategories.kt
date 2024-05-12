package com.technoapps.vehiclemanager.activities

import android.app.Activity
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
import com.technoapps.vehiclemanager.databinding.ActivityVehicleCategoriesBinding
import com.technoapps.vehiclemanager.interfaces.DeleteVehicleCallback
import com.technoapps.vehiclemanager.interfaces.VehicleListDownloadCallback
import com.technoapps.vehiclemanager.pojo.VehicleClass
import com.technoapps.vehiclemanager.services.DeleteVehicleData
import com.technoapps.vehiclemanager.services.GetVehicleData
import java.util.ArrayList

class VehicleCategories : AppCompatActivity(), View.OnClickListener, VehicleListDownloadCallback,
    DeleteVehicleCallback {

    private var aClass: VehicleClass? = null
    private var isDataUpdated = false
    private var isEditClick = true
    private lateinit var binding: ActivityVehicleCategoriesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVehicleCategoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setContentView(R.layout.activity_vehicle_categories)

        initViews()
        loadDataFromIntent()

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
        binding.imgOptionMenu.setOnClickListener(this)

        binding.llRefuel.setOnClickListener(this)
        binding.llService.setOnClickListener(this)
        binding.llExpense.setOnClickListener(this)
        binding.llInsurance.setOnClickListener(this)

        binding.llPermit.setOnClickListener(this)
        binding.llPUC.setOnClickListener(this)
        binding.llAccident.setOnClickListener(this)
        binding.llSummary.setOnClickListener(this)
    }

    private fun loadDataFromIntent() {
        val intent = intent
        if (intent!!.hasExtra(CommonConstants.KeyVehicleDetail)) {
            try {
                aClass = intent.getSerializableExtra(CommonConstants.KeyVehicleDetail) as VehicleClass
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onClick(p0: View?) {
        val id = p0!!.id
        if (id == R.id.imgBack) {
            onBackPressed()
        } else if (id == R.id.imgOptionMenu) {
            openEditDeleteMenu(p0)
        } else {
            if (CommonUtilities.isOnline(this)) {
                if (id == R.id.llRefuel) {
                    openNextActivity(Intent(this, RefuelListActivity::class.java))
                } else if (id == R.id.llService) {
                    openNextActivity(Intent(this, ServiceListActivity::class.java))
                } else if (id == R.id.llExpense) {
                    openNextActivity(Intent(this, ExpenseListActivity::class.java))
                } else if (id == R.id.llInsurance) {
                    openNextActivity(Intent(this, InsuranceListActivity::class.java))
                } else if (id == R.id.llPermit) {
                    openNextActivity(Intent(this, PermitListActivity::class.java))
                } else if (id == R.id.llPUC) {
                    openNextActivity(Intent(this, PUCListActivity::class.java))
                } else if (id == R.id.llAccident) {
                    openNextActivity(Intent(this, AccidentListActivity::class.java))
                } else if (id == R.id.llSummary) {
                    openNextActivity(Intent(this, SummaryActivity::class.java))
                }
            } else {
                startActivity(Intent(this, NoInternetActivity::class.java))
            }
        }
    }

    override fun setVehicleDetailDownloadCallback(vehicleClassArrayList: ArrayList<VehicleClass>) {
        if (vehicleClassArrayList.size > 0) {
            aClass = vehicleClassArrayList[0]
        }
    }

    private fun openEditDeleteMenu(view: View) {
        val menu = PopupMenu(this, view, Gravity.END)
        menu.setOnMenuItemClickListener { item ->
            val id = item.itemId
            if (id == R.id.editData) {
                isEditClick = true
//                CommonUtilities.initFullAdd(this, this@VehicleCategories)
                if (isEditClick) {
                    openNextActivity(Intent(this, VehicleDetailActivity::class.java))
                } else {
                    onBackPressed()
                }
            } else if (id == R.id.deleteData) {
                isEditClick = false
                openDeleteConfirmDialog(this)
            }
            false
        }
        menu.inflate(R.menu.menu_edit_del_options)
        menu.show()
    }

    private fun openNextActivity(intent: Intent) {
        intent.putExtra(CommonConstants.KeyVehicleDetail, aClass)
        startActivityForResult(intent, CommonConstants.RequestDataUpdated)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val key = CommonConstants.KeyIsDataUpdated
        if (resultCode == Activity.RESULT_OK) {
            if (data!!.hasExtra(key)) {
                if (data.getBooleanExtra(key, false)) {
                    isDataUpdated = true
                    GetVehicleData(this, aClass!!.vehicleId,this@VehicleCategories).callGetVehicleDataService()
                }
            }
        }
    }

   /* override fun adLoadingFailed() {
        if (isEditClick) {
            openNextActivity(Intent(this, VehicleDetailActivity::class.java))
        } else {
            onBackPressed()
        }
    }

    override fun adClose() {
        if (isEditClick) {
            openNextActivity(Intent(this, VehicleDetailActivity::class.java))
        } else {
            onBackPressed()
        }
    }

    override fun startNextScreen() {
        if (isEditClick) {
            openNextActivity(Intent(this, VehicleDetailActivity::class.java))
        } else {
            onBackPressed()
        }
    }*/

    private fun openDeleteConfirmDialog(context: Context) {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle(CommonConstants.CapConfirm)
        alertDialog.setMessage(CommonConstants.MsgDoYouWantToDeleteItem)
        alertDialog.setPositiveButton(CommonConstants.CapDelete) { dialog, _ ->
            dialog.dismiss()
            if (CommonUtilities.isOnline(this)) {
                DeleteVehicleData(this, aClass!!.vehicleId, this@VehicleCategories).callDeleteVehicleDataService()
            } else {
                startActivity(Intent(this, NoInternetActivity::class.java))
            }
        }
        alertDialog.setNegativeButton(CommonConstants.CapCancel) { dialog, _ -> dialog.dismiss() }
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    override fun setDeleteVehicleCallback(isSuccess: Boolean) {
        if (isSuccess) {
            isDataUpdated = true
//            CommonUtilities.onClickDeleteVehicle(this,this@VehicleCategories)
            if (isEditClick) {
                openNextActivity(Intent(this, VehicleDetailActivity::class.java))
            } else {
                onBackPressed()
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