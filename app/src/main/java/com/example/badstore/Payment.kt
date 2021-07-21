package com.example.badstore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.braintreepayments.cardform.view.CardForm
import com.example.badstore.db.DatabaseHandler
import com.example.badstore.model.Card
import kotlinx.android.synthetic.main.activity_payment.*

class Payment : AppCompatActivity() {

    var db: DatabaseHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        db = DatabaseHandler(this)
        card_form.cardRequired(true)
            .expirationRequired(true)
            .cvvRequired(true)
            .cardholderName(CardForm.FIELD_REQUIRED)
            .actionLabel("Purchase")
            .saveCardCheckBoxVisible(true)
            .setup(this)
        card_form.setOnCardFormSubmitListener{
            if (card_form.isValid) {
                val card = Card()
                card.cc_number = card_form.cardNumber
                card.cc_year = card_form.expirationYear.toInt()
                card.cc_month = card_form.expirationMonth.toInt()
                card.cc_name = card_form.cardholderName
                card.cc_cvv = card_form.cvv.toInt()
                if (card_form.isSaveCardCheckBoxChecked) {
                    db!!.saveCreditCardInfo(card)
                    val expiry = card_form.expirationMonth + "/" + card_form.expirationYear
                    Log.v("[PAYMENT]", "Credit Card Saved")
                    Log.v("[PAYMENT] Card Number", card.cc_number)
                    Log.v("[PAYMENT] Card expiry", expiry)
                    Log.v("[PAYMENT] Card Name", card.cc_name)
                    Log.v("[PAYMENT] CVV", card_form.cvv)
                }
            } else {
                card_form.validate();
                Toast.makeText(this, "Card not valid", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Use 42424242424242... as card number", Toast.LENGTH_LONG).show()
            }
        };
    }

}