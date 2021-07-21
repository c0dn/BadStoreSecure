package com.example.badstore
import com.example.badstore.model.*
import retrofit2.Call
import retrofit2.http.*

interface APIService {
    @Headers("Content-Type: application/json", "Accept: application/json")
    @GET("products")
    fun getProducts(
    ): Call<List<Product>>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("cart/checkout")
    fun checkout(@Body card: Card): Call<Checkout>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("auth/login")
    fun login(@Body login: Login): Call<Auth>

}