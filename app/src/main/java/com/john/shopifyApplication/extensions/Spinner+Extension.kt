package com.john.shopifyApplication.extensions

import android.widget.Spinner
import android.widget.ArrayAdapter
import com.john.shopifyApplication.base.BaseActivity


fun Spinner.init(activity: BaseActivity, array: ArrayList<String>) {
    val spinnerArrayAdapter = ArrayAdapter<String>(
        activity, android.R.layout.simple_spinner_item, array)
    spinnerArrayAdapter.setDropDownViewResource(
        android.R.layout
            .simple_spinner_dropdown_item
    )
    this.adapter = spinnerArrayAdapter
}