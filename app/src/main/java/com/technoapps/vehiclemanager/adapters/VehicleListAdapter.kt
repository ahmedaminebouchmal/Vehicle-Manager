package com.technoapps.vehiclemanager.adapters

import android.content.Context
import androidx.appcompat.widget.AppCompatTextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.technoapps.vehiclemanager.R
import com.technoapps.vehiclemanager.common.CommonUtilities
import com.technoapps.vehiclemanager.interfaces.AdapterItemCallback
import com.technoapps.vehiclemanager.pojo.VehicleClass
import java.util.ArrayList

class VehicleListAdapter(
    private val context: Context,
    private val vehicleClassArrayList: ArrayList<VehicleClass>,
    private val adapterItemCallback: AdapterItemCallback
) : androidx.recyclerview.widget.RecyclerView.Adapter<VehicleListAdapter.AdapterViewHolder>() {

    inner class AdapterViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view), View.OnClickListener {

        val llMain = view.findViewById(R.id.llMain) as LinearLayout
        val llMainView = view.findViewById(R.id.llMainView) as LinearLayout
        val imgVehicle = view.findViewById(R.id.imgVehicle) as ImageView
        val imgNext = view.findViewById(R.id.imgNext) as ImageView
        val tvBrandModel = view.findViewById(R.id.tvBrandModel) as AppCompatTextView
        val tvDisplayName = view.findViewById(R.id.tvDisplayName) as AppCompatTextView

        init {
            llMain.setOnClickListener(this)
//            imgVehicle.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            try {
                val id = p0!!.id
                if (id == R.id.llMain) {
                    adapterItemCallback.onItemTypeClickCallback(p0.tag as Int)
                }
               /* else if (id == R.id.imgVehicle) {
                    adapterItemCallback.onItemTypeClickCallback(p0.getTag(R.string.adapter_vehicle_detail_key) as Int)
                }*/
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): AdapterViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.cell_vehicle_list, p0, false)
        return AdapterViewHolder(view)
    }

    override fun onBindViewHolder(p0: AdapterViewHolder, p1: Int) {
        val aClass = vehicleClassArrayList[p1]

        try {
            Glide.with(context)
                .load(CommonUtilities.getVehicleImgFromType(context, aClass.vehicleTitle,p0.llMainView,p0.imgNext))
                .into(p0.imgVehicle)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            p0.tvBrandModel.text = aClass.vehicleBrand.plus(" ").plus(aClass.vehicleModel)
            p0.tvDisplayName.text = aClass.vehicleDisplayName
        } catch (e: Exception) {
            e.printStackTrace()
        }

        p0.llMain.tag = p1
        p0.imgVehicle.setTag(R.string.adapter_vehicle_detail_key,p1)

    }

    override fun getItemCount(): Int {
        return vehicleClassArrayList.size
    }

}
