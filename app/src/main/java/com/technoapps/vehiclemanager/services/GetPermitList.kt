package com.technoapps.vehiclemanager.services

import android.app.ProgressDialog
import android.content.Context
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.technoapps.vehiclemanager.common.CommonConstants
import com.technoapps.vehiclemanager.common.CommonUtilities
import com.technoapps.vehiclemanager.interfaces.PermitListDownloadCallback
import com.technoapps.vehiclemanager.pojo.PermitClass
import com.technoapps.vehiclemanager.volley.VolleySingleton
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap

class GetPermitList(
    private val context: Context,
    private val vehicleId: String,
    private val permitListDownloadCallback: PermitListDownloadCallback
) {

    fun callGetPermitListService() {
        showProgress()
        val serviceUrl = CommonConstants.GetPermitApi
        val jsonObjReq = object : StringRequest(Method.POST, serviceUrl, Response.Listener { data ->
            val permitClassArrayList = ArrayList<PermitClass>()
            try {
                if (data != null && data.isNotEmpty()) {
                    try {
                        val jsonObj = JSONObject(data)
                        if (jsonObj.length() > 0) {
                            val statusCode = jsonObj.getInt("status_code")
                            if (statusCode == 200) {
                                val jsonArrCat = jsonObj.getJSONArray("permit_details")
                                if (jsonArrCat.length() > 0) {
                                    for (i in 0 until jsonArrCat.length()) {
                                        try {
                                            val jsonObjCat = jsonArrCat.getJSONObject(i)
                                            val aClass = PermitClass()

                                            aClass.permitId = jsonObjCat.getString("permit_id")
                                            aClass.permitType = jsonObjCat.getString("permit_type")
                                            aClass.permitIssueDate = CommonUtilities.getDateInDigitWithoutTime(jsonObjCat.getString("permit_issue_date"))
                                            aClass.permitExpiryDate = CommonUtilities.getDateInDigitWithoutTime(jsonObjCat.getString("permit_expiry_date"))
                                            aClass.permitNo = jsonObjCat.getString("permit_no")
                                            aClass.permitCost = jsonObjCat.getString("permit_cost")
                                            aClass.permitDescription = jsonObjCat.getString("permit_description")

                                            permitClassArrayList.add(aClass)
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
            permitListDownloadCallback.setPermitDetailDownloadCallback(permitClassArrayList)
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