package com.technoapps.vehiclemanager.services

import android.app.ProgressDialog
import android.content.Context
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.technoapps.vehiclemanager.common.CommonConstants
import com.technoapps.vehiclemanager.common.CommonUtilities
import com.technoapps.vehiclemanager.interfaces.VehicleListDownloadCallback
import com.technoapps.vehiclemanager.pojo.VehicleClass
import com.technoapps.vehiclemanager.volley.VolleySingleton
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap

class GetVehicleData(
    private val context: Context,
    private val vehicleId: String,
    private val vehicleListDownloadCallback: VehicleListDownloadCallback
) {

    fun callGetVehicleDataService() {

        showProgress()
        val serviceUrl = CommonConstants.GetVehicleData
        val jsonObjReq = object : StringRequest(Method.POST, serviceUrl, Response.Listener { data ->
            val vehicleClassArrayList = ArrayList<VehicleClass>()
            try {
                if (data != null && data.isNotEmpty()) {
                    try {
                        val jsonObj = JSONObject(data)
                        if (jsonObj.length() > 0) {
                            val statusCode = jsonObj.getInt("status_code")
                            if (statusCode == 200) {
                                val jsonObjCat = jsonObj.getJSONObject("vehicle_details")
                                if (jsonObjCat.length() > 0) {
                                    try {
                                        val aClass = VehicleClass()
                                        aClass.vehicleId = jsonObjCat.getString("Vehicle_id")
                                        aClass.vehicleType = jsonObjCat.getString("Vehicle_type")
                                        aClass.vehicleTitle = jsonObjCat.getString("vehicle_title")
                                        aClass.vehicleBrand = jsonObjCat.getString("vehicle_brand")
                                        aClass.vehicleModel = jsonObjCat.getString("vehicle_model")
                                        aClass.vehicleBuildYear = jsonObjCat.getString("vehicle_builde_year")
                                        aClass.vehicleRegistrationNo = jsonObjCat.getString("vehicle_regi_no")
                                        aClass.vehicleFuelType = jsonObjCat.getString("vehicle_fuel_type")
                                        aClass.vehicleTankCapacity = jsonObjCat.getString("vehicle_tank_capacity")
                                        aClass.vehicleDisplayName = jsonObjCat.getString("vehicle_disply_name")
                                        aClass.vehiclePurchaseDate = CommonUtilities.getDateInDigitWithoutTime(jsonObjCat.getString("vehicle_purchase_date"))
                                        aClass.vehiclePurchasePrice = jsonObjCat.getString("vehicle_purchase_price")
                                        aClass.vehicleKmReading = jsonObjCat.getString("vehicle_km_reading")
                                        vehicleClassArrayList.add(aClass)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        CommonUtilities.showToast(context, CommonConstants.MsgSomethingWrong)
                                    }
                                }
                            } else {
                                CommonUtilities.showToast(context, CommonConstants.MsgSomethingWrong)
                            }
                        } else {
                            CommonUtilities.showToast(context, CommonConstants.MsgSomethingWrong)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        CommonUtilities.showToast(context, CommonConstants.MsgSomethingWrong)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                CommonUtilities.showToast(context, CommonConstants.MsgSomethingWrong)
            }
            hideProgress()
            vehicleListDownloadCallback.setVehicleDetailDownloadCallback(vehicleClassArrayList)
        }, Response.ErrorListener { e ->
            e.printStackTrace()
            hideProgress()
        }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Vehicle_id"] = vehicleId
                return params
            }
        }
        jsonObjReq.retryPolicy = CommonUtilities.retryPolicy
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjReq)
    }

    private var pDialog: ProgressDialog? = null

    private fun showProgress() {
        try {
            pDialog = ProgressDialog(context)
            pDialog!!.setMessage(CommonConstants.CapPleaseWait)
            pDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            pDialog!!.setCancelable(false)
            pDialog!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun hideProgress() {
        try {
            if (pDialog != null && pDialog!!.isShowing) {
                pDialog!!.dismiss()
            }
        } catch (e: Exception) {
        }
    }
}