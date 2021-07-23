package com.example.badstore

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.badstore.model.Product
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.product_row_item.*
import retrofit2.Call
import retrofit2.Response

class HomeActivity : AppCompatActivity() {
    private lateinit var apiService: APIService
    private lateinit var productAdapter: ProductAdapter

    private var products = listOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)

        setSupportActionBar(toolbar)
        apiService = APIConfig.getRetrofitClient(this).create(APIService::class.java)

        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorPrimary))

        swipeRefreshLayout.isRefreshing = true

        // assign a layout manager to the recycler view
        products_recyclerview.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        val token = intent.getStringExtra("JWT")
        getProducts(token)

    }

    fun getProducts(token: String) {
        apiService.getProducts().enqueue(object : retrofit2.Callback<List<Product>> {
            override fun onFailure(call: Call<List<Product>>, t: Throwable) {

                print(t.message)
                Log.d("Server Error", t.message)
                Toast.makeText(this@HomeActivity, t.message, Toast.LENGTH_SHORT).show()

            }

            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {

                swipeRefreshLayout.isRefreshing = false
                products = response.body()!!

                productAdapter = ProductAdapter(this@HomeActivity, products, token)

                products_recyclerview.adapter = productAdapter
                productAdapter.notifyDataSetChanged()

            }

        })
    }
}