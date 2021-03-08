package com.john.shopifyApplication.model

data class Pagination (
    var count: Int,
    var next: String?,
    var previous: String?
)