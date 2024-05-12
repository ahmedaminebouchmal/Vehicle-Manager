package com.technoapps.vehiclemanager.services

import android.app.ProgressDialog
import android.content.Context
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.technoapps.vehiclemanager.common.CommonConstants
import com.technoapps.vehiclemanager.common.CommonUtilities
import com.technoapps.vehiclemanager.interfaces.ServiceListUploadCallback
import com.technoapps.vehiclemanager.pojo.ServiceClass
import com.technoapps.vehiclemanager.volley.VolleySingleton
import org.json.JSONObject
import java.util.HashMap

class SetServiceList(private val context: Context, private val vehicleId: String, private val aClass:ServiceClass, private val serviceListUploadCallback:ServiceListUploadCallback) {

    fun callSetServiceListService() {

        showProgress()
        val serviceUrl = CommonConstants.SetServiceApi
        val jsonObjReq = object : StringRequest(Method.POST, serviceUrl, Response.Listener { data ->
            try {
                if (data != null && data.isNotEmpty()) {
                    try {
                        val jsonObj = JSONObject(data)
                        if (jsonObj.length() > 0) {
                            val statusCode = jsonObj.getInt("status_code")
                            if (statusCode == 200) {
                                hideProgress()
                                if (aClass.serviceId.isEmpty()) {
                                    CommonUtilities.showToast(context, CommonConstants.MsgRecordAddedSuccessfully)
                                } else {
                                    CommonUtilities.showToast(context, CommonConstants.MsgRecordUpdatedSuccessfully)
                                }
                                serviceListUploadCallback.setServiceDetailUploadCallback(true)
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

                if (aClass.serviceId.isEmpty()) {
                    params["action_type"] = CommonConstants.KeyActionTypeNew
                } else {
                    params["action_type"] = CommonConstants.KeyActionTypeUpdate
                }
                params["vehicle_id"] = vehicleId
                params["service_id"] = aClass.serviceId

                params["service_date"] = aClass.serviceDate
                params["service_body"] = aClass.serviceBody
                params["service_brakes"] = aClass.serviceBrakes
                params["service_clutch"] = aClass.serviceClutch
                params["service_colling_systerm"] = aClass.serviceCollingSystem
                params["service_engine"] = aClass.serviceEngine

                params["service_sparkplug"] = aClass.serviceSparkPlug
                params["service_general"] = aClass.serviceGeneral
                params["service_other"] = aClass.serviceOther
                params["service_oil_change"] = aClass.serviceOilChange
                params["service_battery"] = aClass.serviceBattery
                params["service_garrage_name"] = aClass.serviceGarageName
                params["service_contact_no"] = aClass.serviceContactNo
                params["service_amout"] = aClass.serviceAmount
                params["service_km_reading"] = aClass.serviceKmReading
                params["service_description"] = aClass.serviceDescription
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