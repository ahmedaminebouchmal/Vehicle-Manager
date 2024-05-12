package com.technoapps.vehiclemanager.services

import android.app.ProgressDialog
import android.content.Context
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.technoapps.vehiclemanager.common.CommonConstants
import com.technoapps.vehiclemanager.common.CommonUtilities
import com.technoapps.vehiclemanager.interfaces.InsuranceListDownloadCallback
import com.technoapps.vehiclemanager.pojo.InsuranceClass
import com.technoapps.vehiclemanager.volley.VolleySingleton
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap

class GetInsuranceList (private val context: Context, private val vehicleId: String, private val insuranceListDownloadCallback: InsuranceListDownloadCallback) {

    fun callGetInsuranceListService() {
        showProgress()
        val serviceUrl = CommonConstants.GetInsuranceApi
        val jsonObjReq = object : StringRequest(Method.POST, serviceUrl, Response.Listener { data ->
            val insuranceClassArrayList = ArrayList<InsuranceClass>()
            try {
                if (data != null && data.isNotEmpty()) {
                    try {
                        val jsonObj = JSONObject(data)
                        if (jsonObj.length() > 0) {
                            val statusCode = jsonObj.getInt("status_code")
                            if (statusCode == 200) {
                                val jsonArrCat = jsonObj.getJSONArray("insurance_details")
                                if (jsonArrCat.length() > 0) {
                                    for (i in 0 until jsonArrCat.length()) {
                                        try {
                                            val jsonObjCat = jsonArrCat.getJSONObject(i)
                                            val aClass = InsuranceClass()

                                            aClass.insuranceId = jsonObjCat.getString("insurance_id")
                                            aClass.insuranceCompany = jsonObjCat.getString("insurance_company")
                                            aClass.insurancePolicyType = jsonObjCat.getString("insurance_policy_type")
                                            aClass.insurancePolicyNo = jsonObjCat.getString("insurance_policy_no")
                                            aClass.insuranceIssueDate = CommonUtilities.getDateInDigitWithoutTime(jsonObjCat.getString("insurance_issue_date"))
                                            aClass.insuranceExpiryDate = CommonUtilities.getDateInDigitWithoutTime(jsonObjCat.getString("insurance_expiry_date"))

                                            aClass.insurancePaymentMode = jsonObjCat.getString("insurance_payment_mode")
                                            aClass.insuranceAmount = jsonObjCat.getString("insurance_amount")
                                            aClass.insurancePremium = jsonObjCat.getString("insurance_premium")
                                            aClass.insuranceAgentName = jsonObjCat.getString("insurance_agent_name")
                                            aClass.insuranceAgentPhone = jsonObjCat.getString("insurance_agent_phone")
                                            aClass.insuranceDescription = jsonObjCat.getString("insurance_description")

                                            insuranceClassArrayList.add(aClass)
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
            insuranceListDownloadCallback.setInsuranceDetailDownloadCallback(insuranceClassArrayList)
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