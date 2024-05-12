package com.technoapps.vehiclemanager.services

import android.app.ProgressDialog
import android.content.Context
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.technoapps.vehiclemanager.common.CommonConstants
import com.technoapps.vehiclemanager.common.CommonUtilities
import com.technoapps.vehiclemanager.interfaces.SummaryDownloadCallback
import com.technoapps.vehiclemanager.pojo.SummaryClass
import com.technoapps.vehiclemanager.volley.VolleySingleton
import org.json.JSONObject


class GetSummaryList(
    private val context: Context,
    private val vehicleId: String,
    private val summaryDownloadCallback: SummaryDownloadCallback
) {

    fun callGetSummaryListService() {
        showProgress()
        val serviceUrl = CommonConstants.GetSummaryApi
        val jsonObjReq = object : StringRequest(Method.POST, serviceUrl, Response.Listener { data ->
            val summaryClassArrayList = ArrayList<SummaryClass>()
            try {
                if (data != null && data.isNotEmpty()) {
                    try {
                        val jsonObj = JSONObject(data)
                        if (jsonObj.length() > 0) {
                            val statusCode = jsonObj.getInt("status_code")
                            if (statusCode == 200) {
                                val jsonObjCat = jsonObj.getJSONObject("summary_details")
                                if (jsonObjCat.length() > 0) {
                                    try {
                                        val aClass = SummaryClass()

                                        aClass.vehiclePurchasePrice = jsonObjCat.getString("vehicle_purchase_price") ?: "0.00"
                                        aClass.refuelAmount = jsonObjCat.getString("refuel_amount") ?: "0.00"
                                        aClass.serviceAmount = jsonObjCat.getString("service_amout") ?: "0.00"
                                        aClass.expenseDetailAmount = jsonObjCat.getString("expense_detail_amount") ?: "0.00"
                                        aClass.insuranceAmount = jsonObjCat.getString("insurance_amount") ?: "0.00"
                                        aClass.permitCost = jsonObjCat.getString("permit_cost") ?: "0.00"
                                        aClass.totalExpenseWithPrice = jsonObjCat.getString("total_expence_with_price") ?: "0.00"
                                        aClass.totalExpenseWithoutPrice = jsonObjCat.getString("total_expence_without_price")?: "0.00"

                                        summaryClassArrayList.add(aClass)
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
            summaryDownloadCallback.setSummaryDetailDownloadCallback(summaryClassArrayList)
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