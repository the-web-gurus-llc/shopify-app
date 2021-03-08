package com.john.shopifyApplication.fragment

import android.support.v4.app.Fragment
import java.io.IOException
import java.util.ArrayList
import java.io.InputStreamReader
import java.io.BufferedReader



open class BaseFragment: Fragment() {

    companion object {
        const val VALUE_ENTER = "<br>"
    }

    fun getRealString(ori: String): String {
        if (ori.isEmpty()) return ""
        return "$ori "
    }

    open fun getStringArray(assetsName: String): ArrayList<String> {

        if(activity == null) return arrayListOf()

        val values = ArrayList<String>()

        try {
            val bReader = BufferedReader(InputStreamReader(activity!!.assets.open("$assetsName.txt")))
            var line: String? = bReader.readLine()
            while (line != null) {
                values.add(line)
                line = bReader.readLine()
            }
            bReader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return values
    }

    open fun getTitle(jewelryType: String): String {
        return ""
    }

    open fun getDescription(ref: String): String {
        return ""
    }
}