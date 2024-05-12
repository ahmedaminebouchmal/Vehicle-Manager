package com.technoapps.vehiclemanager.services

import android.app.ProgressDialog
import android.content.Context
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.technoapps.vehiclemanager.common.CommonConstants
import com.technoapps.vehiclemanager.common.CommonUtilities
import com.technoapps.vehiclemanager.interfaces.ServiceListDownloadCallback
import com.technoapps.vehiclemanager.pojo.ServiceClass
import com.technoapps.vehiclemanager.volley.VolleySingleton
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap

class GetServiceList(private val context: Context, private val vehicleId: String, private val serviceListDownloadCallback: ServiceListDownloadCallback) {

    fun callGetServiceListService() {
        showProgress()
        val serviceUrl = CommonConstants.GetServiceApi
        val jsonObjReq = object : StringRequest(Method.POST, serviceUrl, Response.Listener { data ->
            val serviceClassArrayList = ArrayList<ServiceClass>()
            try {
                if (data != null && data.isNotEmpty()) {
                    try {
                        val jsonObj = JSONObject(data)
                        if (jsonObj.length() > 0) {
                            val statusCode = jsonObj.getInt("status_code")
                            if (statusCode == 200) {
                                val jsonArrCat = jsonObj.getJSONArray("service_details")
                                if (jsonArrCat.length() > 0) {
                                    for (i in 0 until jsonArrCat.length()) {
                                        try {
                                            val jsonObjCat = jsonArrCat.getJSONObject(i)
                                            val aClass = ServiceClass()
                                            aClass.serviceId = jsonObjCat.getString("service_id")
                                            aClass.serviceDate = CommonUtilities.getDateInDigitWithoutTime(jsonObjCat.getString("service_date"))

                                            aClass.serviceSelected = jsonObjCat.getString("sevice_selected")

                                            aClass.serviceBody = jsonObjCat.getString("service_body")
                                            aClass.serviceBrakes = jsonObjCat.getString("service_brakes")

                                            aClass.serviceClutch = jsonObjCat.getString("service_clutch")
                                            aClass.serviceCollingSystem = jsonObjCat.getString("service_colling_systerm")

                                            aClass.serviceEngine = jsonObjCat.getString("service_engine")
                                            aClass.serviceSparkPlug = jsonObjCat.getString("service_sparkplug")

                                            aClass.serviceGeneral = jsonObjCat.getString("service_general")
                                            aClass.serviceOther = jsonObjCat.getString("service_other")

                                            aClass.serviceOilChange = jsonObjCat.getString("service_oil_change")
                                            aClass.serviceBattery = jsonObjCat.getString("service_battery")

                                            aClass.serviceGarageName = jsonObjCat.getString("service_garrage_name")
                                            aClass.serviceContactNo = jsonObjCat.getString("service_contact_no")
                                            aClass.serviceAmount = jsonObjCat.getString("service_amout")
                                            aClass.serviceKmReading = jsonObjCat.getString("service_km_reading")
                                            aClass.serviceDescription = jsonObjCat.getString("service_description")

                                            serviceClassArrayList.add(aClass)
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
            serviceListDownloadCallback.setServiceDetailDownloadCallback(serviceClassArrayList)
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
