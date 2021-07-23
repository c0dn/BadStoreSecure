package com.example.badstore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.badstore.model.CartCheckout
import com.example.badstore.model.Checkout
import retrofit2.Call
import retrofit2.Response

class Complete : AppCompatActivity() {

    private lateinit var apiService: APIService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete)
        val cartCheckout = CartCheckout()
        Log.v("[Complete]", "Data received")
        cartCheckout.product_id = intent.getIntExtra("PRODUCT_ID", 99999)
        cartCheckout.cc_number = intent.getStringExtra("cc_number")
        cartCheckout.cc_year = intent.getStringExtra("cc_year").toInt()
        cartCheckout.cc_month = intent.getStringExtra("cc_month").toInt()
        cartCheckout.cc_name = intent.getStringExtra("cc_name")
        cartCheckout.cc_cvv = intent.getStringExtra("cc_cvv").toInt()
        apiService = APIConfig.getRetrofitClient(this).create(APIService::class.java)
        apiService.checkout(intent.getStringExtra("JWT"), cartCheckout).enqueue(object: retrofit2.Callback<Checkout> {
            override fun onFailure(call: Call<Checkout>, t: Throwable) {
                Log.d("Server Error", t.message)
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Checkout>, response: Response<Checkout>) {
                Log.v("[CHECKOUT] Status code", response.code().toString())
                Toast.makeText(applicationContext, "Checkout Successful", Toast.LENGTH_LONG).show()
                val goHome = Intent(applicationContext, HomeActivity::class.java)
                goHome.putExtra("JWT", intent.getStringExtra("JWT"))
                startActivity(goHome)
            }
        })
    }
}