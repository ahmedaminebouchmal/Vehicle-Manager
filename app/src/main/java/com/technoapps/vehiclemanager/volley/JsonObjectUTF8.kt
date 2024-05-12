package com.technoapps.vehiclemanager.volley

import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonRequest
import org.json.JSONObject

open class JsonObjectUTF8(method: Int,
                          url: String?,
                          requestBody: String?,
                          listener: Response.Listener<JSONObject>?,
                          errorListener: Response.ErrorListener?) : JsonRequest<JSONObject>(method,url,requestBody,listener,errorListener) {

    override fun parseNetworkResponse(response: NetworkResponse?): Response<JSONObject>? {
        try {
            val responseString = String(response!!.data, charset("UTF8"))
            return Response.success(JSONObject(responseString), HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}