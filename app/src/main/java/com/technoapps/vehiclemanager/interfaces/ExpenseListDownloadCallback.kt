package com.technoapps.vehiclemanager.interfaces

import com.technoapps.vehiclemanager.pojo.ExpenseClass
import java.util.ArrayList

interface ExpenseListDownloadCallback {
    fun setExpenseDetailDownloadCallback(expenseClassArrayList: ArrayList<ExpenseClass>)
}