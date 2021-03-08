package com.john.shopifyApplication.common

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.john.shopifyApplication.config.Constants
import com.john.shopifyApplication.model.Product
import java.util.*
import kotlin.collections.ArrayList

class APIManager {

    companion object {
        const val TAG = Constants.BASICTAG + "APIManager"
        var share = APIManager()
    }

    private val gson = GsonBuilder().serializeNulls().setPrettyPrinting().create()
    private val basicAuth = "Basic ZDY3NzM1MzI2ZTdkMzlmNThjM2RjZDBiMjZiYWQ4NTU6YTQ3ZDBiMTU0YzViM2Q5ZTk2ZWI3ZWIwMzNmOWFlOWY="

    fun deleteProduct(id: Long, successCallback: () -> Unit, errorCallback: (message: String) -> Unit) {
        val url = URLProvider.delProducts + "${id}.json"
        HttpManager.share.delete(url, basicAuth, object : JSONCallback {
            override fun onResponseJSONObjectSuccess(obj: JsonObject) {
                successCallback()
            }

            override fun onFailed(message: String) {
                errorCallback(message)
            }
        })
    }

    fun postProduct(product: Product, successCallback: (productID: Long) -> Unit, errorCallback: (message: String) -> Unit) {
        val url = URLProvider.products
        val params = product.getJsonString()

        HttpManager.share.post(url, basicAuth, params, object : JSONCallback {
            override fun onResponseJSONObjectSuccess(obj: JsonObject) {

                val product = Product.parseObj(obj)
                var productID: Long = 0
                if(product != null) productID = product.id
                successCallback(productID)
            }

            override fun onFailed(message: String) {
                errorCallback(message)
            }
        })
    }

    fun getProducts(
        searchWith: String,
        successCallback: (list: List<Product>) -> Unit,
        headerCallback: (text: String) -> Unit,
        errorCallback: (message: String) -> Unit
    ) {
        var url = URLProvider.products
        if(searchWith.isNotEmpty())
            url += "?title=${searchWith}"
        getProducts(successCallback, headerCallback, errorCallback, url)
    }

    fun getProductsWithIDS(
        productIDs: String,
        successCallback: (list: List<Product>) -> Unit,
        headerCallback: (text: String) -> Unit,
        errorCallback: (message: String) -> Unit
    ) {
        var url = URLProvider.products
        if(productIDs.isNotEmpty())
            url += "?ids=$productIDs"
        getProducts(successCallback, headerCallback, errorCallback, url)
    }

    fun getProductsWithPagination(
        url: String,
        successCallback: (list: List<Product>) -> Unit,
        headerCallback: (text: String) -> Unit,
        errorCallback: (message: String) -> Unit
    ) {
        getProducts(successCallback, headerCallback, errorCallback, url)
    }

    fun getProducts(
        successCallback: (list: List<Product>) -> Unit,
        headerCallback: (text: String) -> Unit,
        errorCallback: (message: String) -> Unit,
        url: String
    ) {
        HttpManager.share.get(url, basicAuth, object : JSONCallback {
            override fun onResponseJSONObjectSuccess(obj: JsonObject) {
                val productList: ArrayList<Product> = Product.parseArray(obj)
                successCallback(productList)
            }

            override fun onResponseHeader(text: String) {
                Log.d(TAG, "header -> " + text)
                headerCallback(text)
            }

            override fun onFailed(message: String) {
                errorCallback(message)
            }
        })
    }

}