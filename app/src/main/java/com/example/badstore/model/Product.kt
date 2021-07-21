package com.example.badstore.model
import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("description")
    var description: String? = null,

    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("price")
    var price: Int? = null,

    @SerializedName("stock")
    var stock: Int? = null,

    @SerializedName("picture")
    var picture: String? = null,
)