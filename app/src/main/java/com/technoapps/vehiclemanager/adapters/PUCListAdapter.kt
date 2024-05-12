package com.technoapps.vehiclemanager.adapters

import android.content.Context
import androidx.appcompat.widget.AppCompatTextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.technoapps.vehiclemanager.R
import com.technoapps.vehiclemanager.common.CommonUtilities
import com.technoapps.vehiclemanager.interfaces.AdapterItemCallback
import com.technoapps.vehiclemanager.pojo.PUCClass

class PUCListAdapter(
    private val context: Context, private val pucClassArrayList: ArrayList<PUCClass>,
    private val adapterItemCallback: AdapterItemCallback
) : androidx.recyclerview.widget.RecyclerView.Adapter<PUCListAdapter.AdapterViewHolder>() {

    inner class AdapterViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view), View.OnClickListener {

        val llMain = view.findViewById(R.id.llMain) as LinearLayout
        val tvIssueMonth = view.findViewById(R.id.tvIssueMonth) as AppCompatTextView
        val tvIssueDate = view.findViewById(R.id.tvIssueDate) as AppCompatTextView
        val tvIssueYear = view.findViewById(R.id.tvIssueYear) as AppCompatTextView
        val tvExpiryMonth = view.findViewById(R.id.tvExpiryMonth) as AppCompatTextView
        val tvExpiryDate = view.findViewById(R.id.tvExpiryDate) as AppCompatTextView
        val tvExpiryYear = view.findViewById(R.id.tvExpiryYear) as AppCompatTextView
        val tvPUCNo = view.findViewById(R.id.tvPUCNo) as AppCompatTextView

        init {
            llMain.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val id = p0!!.id
            if (id == R.id.llMain) {
                adapterItemCallback.onItemTypeClickCallback(p0.tag as Int)
            }
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): AdapterViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.cell_puc_list, p0, false)
        return AdapterViewHolder(view)
    }

    override fun onBindViewHolder(p0: AdapterViewHolder, p1: Int) {
        val aClass = pucClassArrayList[p1]
        val dateIssue = CommonUtilities.getDateWithMonthName(aClass.pucIssueDate).split("-")

        try {
            p0.tvIssueMonth.text = dateIssue[0]
            p0.tvIssueDate.text = dateIssue[1]
            p0.tvIssueYear.text = dateIssue[2]
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            p0.tvPUCNo.text = aClass.pucNo
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val dateExpiry = CommonUtilities.getDateWithMonthName(aClass.pucExpiryDate).split("-")

        try {
            p0.tvExpiryMonth.text = dateExpiry[0]
            p0.tvExpiryDate.text = dateExpiry[1]
            p0.tvExpiryYear.text = dateExpiry[2]
        } catch (e: Exception) {
            e.printStackTrace()
        }
        p0.llMain.tag = p1
    }

    override fun getItemCount(): Int {
        return pucClassArrayList.size
    }
}