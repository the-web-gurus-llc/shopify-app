package com.john.shopifyApplication.common

object URLProvider {

    private var base_url = "https://harrychadent-com-uae.myshopify.com/admin/api/2019-07/"

    val products = base_url + "products.json"
    val delProducts = base_url + "products/"
}