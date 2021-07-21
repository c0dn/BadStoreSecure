package com.example.badstore

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.badstore.model.Product
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.product_row_item.view.*
import com.example.badstore.HomeActivity


class ProductAdapter(var context: Context, var products: List<Product> = arrayListOf()) :
    RecyclerView.Adapter<ProductAdapter.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ProductAdapter.ViewHolder {
        // The layout design used for each list item
        val view = LayoutInflater.from(context).inflate(R.layout.product_row_item, null)
        return ViewHolder(view)

    }
    // This returns the size of the list.
    override fun getItemCount(): Int = products.size

    override fun onBindViewHolder(viewHolder: ProductAdapter.ViewHolder, position: Int) {
        //we simply call the `bindProduct` function here
        viewHolder.bindProduct(products[position], context)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        // This displays the product information for each item
        fun bindProduct(product: Product, context: Context) {
            val url = "http://127.0.0.1:5000/products/uploads/"
            itemView.product_name.text = product.name
            itemView.product_price.text = "$${product.price.toString()}"
            Picasso.get().load("$url${product.picture}").fit().into(itemView.product_image)

            itemView.buyItem.setOnClickListener {
                val intent = Intent(context, Payment::class.java)
                intent.putExtra("PRODUCT_ID", product.id)
                context.startActivity(intent)
            }
        }

    }

}