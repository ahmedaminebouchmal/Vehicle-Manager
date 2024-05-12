package com.technoapps.vehiclemanager.services

import android.app.ProgressDialog
import android.content.Context
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.technoapps.vehiclemanager.common.CommonConstants
import com.technoapps.vehiclemanager.common.CommonUtilities
import com.technoapps.vehiclemanager.interfaces.PUCListDownloadCallback
import com.technoapps.vehiclemanager.pojo.PUCClass
import com.technoapps.vehiclemanager.volley.VolleySingleton
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap

class GetPUCList(
    private val context: Context,
    private val vehicleId: String,
    private val pucListDownloadCallback: PUCListDownloadCallback
) {

    fun callGetPUCListService() {
        showProgress()
        val serviceUrl = CommonConstants.GetPUCApi
        val jsonObjReq = object : StringRequest(Method.POST, serviceUrl, Response.Listener { data ->
            val pucClassArrayList = ArrayList<PUCClass>()
            try {
                if (data != null && data.isNotEmpty()) {
                    try {
                        val jsonObj = JSONObject(data)
                        if (jsonObj.length() > 0) {
                            val statusCode = jsonObj.getInt("status_code")
                            if (statusCode == 200) {
                                val jsonArrCat = jsonObj.getJSONArray("puc_details")
                                if (jsonArrCat.length() > 0) {
                                    for (i in 0 until jsonArrCat.length()) {
                                        try {
                                            val jsonObjCat = jsonArrCat.getJSONObject(i)
                                            val aClass = PUCClass()

                                            aClass.pucId = jsonObjCat.getString("puc_id")
                                            aClass.pucNo = jsonObjCat.getString("puc_no")
                                            aClass.pucIssueDate = CommonUtilities.getDateInDigitWithoutTime(jsonObjCat.getString("puc_issue_date"))
                                            aClass.pucExpiryDate = CommonUtilities.getDateInDigitWithoutTime(jsonObjCat.getString("puc_expiry_date"))
                                            aClass.pucAmount = jsonObjCat.getString("puc_amount")
                                            aClass.pucDescription = jsonObjCat.getString("puc_description")

                                            pucClassArrayList.add(aClass)
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
            pucListDownloadCallback.setPUCDetailDownloadCallback(pucClassArrayList)
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