package com.example.badstore.model

data class Product(
    var description: String? = null,

    var id: Int? = null,

    var name: String? = null,

    var price: Int? = null,

    var stock: Int? = null,

    var picture: String? = null,
)