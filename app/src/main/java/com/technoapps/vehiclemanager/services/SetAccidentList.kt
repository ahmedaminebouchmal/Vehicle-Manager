package com.technoapps.vehiclemanager.services

import android.app.ProgressDialog
import android.content.Context
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.technoapps.vehiclemanager.common.CommonConstants
import com.technoapps.vehiclemanager.common.CommonUtilities
import com.technoapps.vehiclemanager.interfaces.AccidentListUploadCallback
import com.technoapps.vehiclemanager.pojo.AccidentClass
import com.technoapps.vehiclemanager.volley.VolleySingleton
import org.json.JSONObject
import java.util.HashMap

class SetAccidentList(
    private val context: Context,
    private val vehicleId: String,
    private val aClass: AccidentClass,
    private val accidentListUploadCallback: AccidentListUploadCallback ) {

    fun callSetAccidentListService() {

        showProgress()
        val serviceUrl = CommonConstants.SetAccidentApi
        val jsonObjReq = object : StringRequest(Method.POST, serviceUrl, Response.Listener { data ->
            try {
                if (data != null && data.isNotEmpty()) {
                    try {
                        val jsonObj = JSONObject(data)
                        if (jsonObj.length() > 0) {
                            val statusCode = jsonObj.getInt("status_code")
                            if (statusCode == 200) {
                                hideProgress()
                                if (aClass.accidentId.isEmpty()) {
                                    CommonUtilities.showToast(context, CommonConstants.MsgRecordAddedSuccessfully)
                                } else {
                                    CommonUtilities.showToast(context, CommonConstants.MsgRecordUpdatedSuccessfully)
                                }
                                accidentListUploadCallback.setAccidentDetailUploadCallback(true)
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

                if (aClass.accidentId.isEmpty()) {
                    params["action_type"] = CommonConstants.KeyActionTypeNew
                } else {
                    params["action_type"] = CommonConstants.KeyActionTypeUpdate
                }
                params["vehicle_id"] = vehicleId

                params["accident_id"] = aClass.accidentId

                params["accident_date"] = aClass.accidentDate
                params["accident_time"] = aClass.accidentTime
                params["accident_driver_name"] = aClass.accidentDriverName
                params["accident_amount"] = aClass.accidentAmount
                params["accident_km_reading"] = aClass.accidentKmReading
                params["accident_description"] = aClass.accidentDescription
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