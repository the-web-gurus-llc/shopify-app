package com.john.shopifyApplication.model

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import org.json.JSONException

data class Product (
    var id: Long,
    var title: String,
    var image: Image,
    var variant: Variants,
    var body_html: String
){

    companion object {
        fun parseArray(json: JsonObject): ArrayList<Product> {
            val productArr: ArrayList<Product> = arrayListOf()

            try {
                val arr = json["products"].asJsonArray
                for (obj in arr) {
                    val product = parse(obj.asJsonObject)
                    productArr.add(product)
                }
            } catch (exception: JSONException) {
                Log.e("Product Parse", exception.message)
            }

            return productArr
        }

        fun parseObj(json: JsonObject): Product? {
            try {
                val obj = json["product"].asJsonObject
                val product = parse(obj)
                return product
            } catch (exception: JSONException) {
                Log.e("Product Parse", exception.message)
                return null
            }

        }

        fun parse(json: JsonObject): Product {
            val gson = GsonBuilder().serializeNulls().setPrettyPrinting().create()
            val id = json["id"].asLong
            val title = json["title"].asString
            var imgObj: JsonObject?= null
            var image = Image(0,0,"")
            if(json["image"].isJsonObject)
            {
                imgObj = json["image"].asJsonObject
                image = gson.fromJson(imgObj, Image::class.java)
            }
            val bodyObj = json["body_html"].asString
            val variantArr = json["variants"].asJsonArray
            var variantObj: JsonObject? = null
            if(variantArr.size() != 0) {
                variantObj = variantArr[0].asJsonObject
            }
            if(variantObj != null) {
                val variant = gson.fromJson(variantObj, Variants::class.java)
                return Product(id, title, image,variant,bodyObj)
            }
            return Product(id, title, image,Variants("free"),bodyObj)
        }
    }

    fun txtContain(txt: String): Boolean {
        val temp = txt.toLowerCase()
        if( title.toLowerCase().contains(temp)) return true
        return false
    }

    fun getJsonString(): String {
        val gson = GsonBuilder().serializeNulls().setPrettyPrinting().create()

        val map_variants: ArrayList<Variants> = arrayListOf()
        map_variants.add(variant)
        val imageOne: MutableMap<String, Any> = mutableMapOf()
        imageOne["attachment"] = image.src
        val images: ArrayList<Any> = arrayListOf()
        images.add(imageOne)

        val map: MutableMap<String, Any> = mutableMapOf()
        map["title"] = title
        map["body_html"] = body_html
        map["variants"] = map_variants
        map["images"] = images

        val map_products: MutableMap<String, Any> = mutableMapOf()
        map_products["product"] = map

        return gson.toJson(map_products)
    }


}