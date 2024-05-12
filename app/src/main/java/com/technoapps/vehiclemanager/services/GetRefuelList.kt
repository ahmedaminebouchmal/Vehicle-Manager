package com.technoapps.vehiclemanager.services

import android.app.ProgressDialog
import android.content.Context
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.technoapps.vehiclemanager.common.CommonConstants
import com.technoapps.vehiclemanager.common.CommonUtilities
import com.technoapps.vehiclemanager.interfaces.RefuelListDownloadCallback
import com.technoapps.vehiclemanager.pojo.RefuelClass
import com.technoapps.vehiclemanager.volley.VolleySingleton
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap

class GetRefuelList(private val context: Context, private val vehicleId: String, private val refuelListDownloadCallback: RefuelListDownloadCallback) {

    fun callGetRefuelListService() {
        showProgress()
        val serviceUrl = CommonConstants.GetRefuelApi
        val jsonObjReq = object : StringRequest(Method.POST, serviceUrl, Response.Listener { data ->
            val refuelClassArrayList = ArrayList<RefuelClass>()
            try {
                if (data != null && data.isNotEmpty()) {
                    try {
                        val jsonObj = JSONObject(data)
                        if (jsonObj.length() > 0) {
                            val statusCode = jsonObj.getInt("status_code")
                            if (statusCode == 200) {
                                val jsonArrCat = jsonObj.getJSONArray("refuel_details")
                                if (jsonArrCat.length() > 0) {
                                    for (i in 0 until jsonArrCat.length()) {
                                        try {
                                            val jsonObjCat = jsonArrCat.getJSONObject(i)
                                            val aClass = RefuelClass()
                                            aClass.refuelId = jsonObjCat.getString("refuel_id")
                                            aClass.refuelDate = CommonUtilities.getDateInDigitWithoutTime(jsonObjCat.getString("refuel_date"))
                                            aClass.refuelType = jsonObjCat.getString("refuel_type")
                                            aClass.refuelAmount = jsonObjCat.getString("refuel_amount")
                                            aClass.refuelFuelPrice = jsonObjCat.getString("refuel_fuel_price")
                                            aClass.refuelQuantity = jsonObjCat.getString("refuel_quantity")
                                            aClass.refuelStation = jsonObjCat.getString("refuel_station")
                                            aClass.refuelKmReading = jsonObjCat.getString("refuel_km_reading")

                                            refuelClassArrayList.add(aClass)
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            CommonUtilities.showToast(context, CommonConstants.MsgSomethingWrong)
                                        }
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
            refuelListDownloadCallback.setRefuelDetailDownloadCallback(refuelClassArrayList)
        }, Response.ErrorListener { e ->
            e.printStackTrace()
            hideProgress()
        }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["vehicle_id"] = vehicleId
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