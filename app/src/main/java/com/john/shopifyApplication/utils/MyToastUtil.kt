package com.john.shopifyApplication.utils

import android.content.Context
import android.widget.Toast
import com.john.shopifyApplication.R
import com.john.shopifyApplication.components.Mytoast

class MyToastUtil {

    companion object {
        fun showWarning(context: Context, text: String) {
            Mytoast().make(
                context,
                text,
                Toast.LENGTH_SHORT,
                "#f44336",
                "#ffffff",
                R.drawable.ic_warning_black
            ).show()
        }

        fun showNotice(context: Context, text: String) {
            Mytoast().make(
                context,
                text,
                Toast.LENGTH_SHORT,
                "#f44336",
                "#ffffff",
                R.drawable.ic_add_circle_outline_black
            ).show()
        }

        fun showMessage(context: Context, text: String) {
            Mytoast().make(
                context,
                text,
                Toast.LENGTH_SHORT,
                "#DC873B",
                "#ffffff",
                R.drawable.ic_add_circle_outline_black
            ).show()
        }
    }
}