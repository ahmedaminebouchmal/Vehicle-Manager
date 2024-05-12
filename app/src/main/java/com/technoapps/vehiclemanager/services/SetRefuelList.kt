package com.technoapps.vehiclemanager.services

import android.app.ProgressDialog
import android.content.Context
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.technoapps.vehiclemanager.common.CommonConstants
import com.technoapps.vehiclemanager.common.CommonUtilities
import com.technoapps.vehiclemanager.interfaces.RefuelListUploadCallback
import com.technoapps.vehiclemanager.pojo.RefuelClass
import com.technoapps.vehiclemanager.volley.VolleySingleton
import org.json.JSONObject
import java.util.HashMap

class SetRefuelList(private val context: Context, private val vehicleId: String,
                    private val aClass:RefuelClass, private val refuelListUploadCallback:RefuelListUploadCallback) {

    fun callSetRefuelListService() {

        showProgress()
        val serviceUrl = CommonConstants.SetRefuelApi
        val jsonObjReq = object : StringRequest(Method.POST, serviceUrl, Response.Listener { data ->
            try {
                if (data != null && data.isNotEmpty()) {
                    try {
                        val jsonObj = JSONObject(data)
                        if (jsonObj.length() > 0) {
                            val statusCode = jsonObj.getInt("status_code")
                            if (statusCode == 200) {
                                hideProgress()
                                if (aClass.refuelId.isEmpty()) {
                                    CommonUtilities.showToast(context, CommonConstants.MsgRecordAddedSuccessfully)
                                } else {
                                    CommonUtilities.showToast(context, CommonConstants.MsgRecordUpdatedSuccessfully)
                                }
                                refuelListUploadCallback.setRefuelDetailUploadCallback(true)
                            } else {
                                hideProgress()
                                CommonUtilities.showToast(context, CommonConstants.MsgSomethingWrong)
                            }
                        } else {
                            hideProgress()
                            CommonUtilities.showToast(context, CommonConstants.MsgSomethingWrong)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        hideProgress()
                        CommonUtilities.showToast(context, CommonConstants.MsgSomethingWrong)
                    }
                } else {
                    hideProgress()
                    CommonUtilities.showToast(context, CommonConstants.MsgSomethingWrong)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                hideProgress()
                CommonUtilities.showToast(context, CommonConstants.MsgSomethingWrong)
            }
        }, Response.ErrorListener { e ->
            e.printStackTrace()
            hideProgress()
            CommonUtilities.showToast(context, CommonConstants.MsgSomethingWrong)
        }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["device_id"] = CommonUtilities.getAndroidId(context)
                params["email_id"] = CommonUtilities.getEmailIdFromPref(context)

                if (aClass.refuelId.isEmpty()) {
                    params["action_type"] = CommonConstants.KeyActionTypeNew
                } else {
                    params["action_type"] = CommonConstants.KeyActionTypeUpdate
                }
                params["vehicle_id"] = vehicleId
                params["refuel_id"] = aClass.refuelId

                params["refuel_date"] = aClass.refuelDate
                params["refuel_type"] = aClass.refuelType
                params["refuel_amount"] = aClass.refuelAmount
                params["refuel_fuel_price"] = aClass.refuelFuelPrice
                params["refuel_quantity"] = aClass.refuelQuantity
                params["refuel_station"] = aClass.refuelStation

                params["refuel_km_reading"] = aClass.refuelKmReading
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