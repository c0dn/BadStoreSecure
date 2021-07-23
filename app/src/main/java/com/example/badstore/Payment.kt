package com.example.badstore

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.braintreepayments.cardform.view.CardForm
import kotlinx.android.synthetic.main.activity_payment.*
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit
import kotlin.jvm.internal.Intrinsics


class Payment : AppCompatActivity() {

    companion object {
        private const val CC_NUMBER = "cc_number"
        private const val CC_CCV = "cc_ccv"
        private const val CC_MONTH = "cc_month"
        private const val CC_YEAR = "cc_year"
        private const val CC_NAME = "cc_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        card_form.cardRequired(true)
            .expirationRequired(true)
            .cvvRequired(true)
            .cardholderName(CardForm.FIELD_REQUIRED)
            .actionLabel("Purchase")
            .saveCardCheckBoxVisible(true)
            .setup(this)

        val cursor = contentResolver.query(
            Uri.parse("content://com.example.badstore.card/cards"),
            null,
            null,
            null,
            null
        )
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                if (cursor.getString(cursor.getColumnIndex(CC_NAME)) != null) {
                    Log.v("PAYMENT", "using saved card")
                    Toast.makeText(applicationContext, "Using saved credit card info", Toast.LENGTH_LONG).show()
                    val productID = intent.getIntExtra("PRODUCT_ID", 99999)
                    val payIntent = Intent(applicationContext, Complete::class.java)
                    payIntent.addCategory("com.example.category.pay")
                    payIntent.putExtra("JWT", intent.getStringExtra("JWT"))
                    payIntent.putExtra("PRODUCT_ID", productID)
                    payIntent.putExtra("cc_number", cursor.getString(cursor.getColumnIndex(CC_NUMBER)))
                    payIntent.putExtra("cc_year", cursor.getInt(cursor.getColumnIndex(CC_YEAR)).toString())
                    payIntent.putExtra("cc_month", cursor.getInt(cursor.getColumnIndex(CC_MONTH)).toString())
                    payIntent.putExtra("cc_cvv", cursor.getInt(cursor.getColumnIndex(CC_CCV)).toString())
                    payIntent.putExtra("cc_name", cursor.getString(cursor.getColumnIndex(CC_NAME)))
                    cursor.close()
                    CoroutineScope(Dispatchers.IO).launch {
                        delay(TimeUnit.SECONDS.toMillis(1))
                        withContext(Dispatchers.Main) {
                            startActivity(payIntent)
                        }
                    }
                }
            }
        }
        submit_payment.setOnClickListener {
            Log.v("PAYMENT", "asking for card")
            if (card_form.isValid) {
                if (card_form.isSaveCardCheckBoxChecked) {
                    val contentValues = ContentValues()
                    contentValues.put(CC_NUMBER, card_form.cardNumber)
                    contentValues.put(CC_YEAR, card_form.expirationYear.toInt())
                    contentValues.put(CC_MONTH, card_form.expirationMonth.toInt())
                    contentValues.put(CC_CCV, card_form.cvv.toInt())
                    contentValues.put(CC_NAME, card_form.cardholderName)
                    Intrinsics.checkExpressionValueIsNotNull(
                        applicationContext,
                        "applicationContext"
                    )
                    contentResolver.insert(Uri.parse("content://com.example.badstore.card/cards"), contentValues)
                    val expiry = card_form.expirationMonth + "/" + card_form.expirationYear
                    Log.v("[PAYMENT]", "Credit Card Saved")
                    Toast.makeText(applicationContext, "Card saved!", Toast.LENGTH_LONG).show()
                }
                val productID = intent.getIntExtra("PRODUCT_ID", 99999)
                val payIntent = Intent(applicationContext, Complete::class.java)
                payIntent.putExtra("JWT", intent.getStringExtra("JWT"))
                payIntent.putExtra("PRODUCT_ID", productID)
                payIntent.putExtra("cc_number", card_form.cardNumber)
                payIntent.putExtra("cc_year", card_form.expirationYear)
                payIntent.putExtra("cc_month", card_form.expirationMonth)
                payIntent.putExtra("cc_cvv", card_form.cvv)
                payIntent.putExtra("cc_name", card_form.cardholderName)
                startActivity(payIntent)
                Log.v("PAYMENT", "Intent sent")
            } else {
                card_form.validate()
                Toast.makeText(this, "Card not valid", Toast.LENGTH_SHORT).show()
                Toast.makeText(this, "Use 4242424242424242 as card number", Toast.LENGTH_LONG).show()
            }
        }
    }

}