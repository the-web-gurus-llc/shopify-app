package com.john.shopifyApplication.common

interface HttpResonseCallback {

    fun onResponse200() {}

    fun onResponse201() {}

    fun onResponse204() {}

    fun onResponse400() {}

    fun onResponse401() {}

    fun onResponse405() {}
}