package com.example.badstore.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.badstore.model.Card

class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSIOM) {

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE = "CREATE TABLE $TABLE_NAME " +
                "($ID Integer PRIMARY KEY, $JWT TEXT, $CC_NUMBER TEXT, $CC_CCV Integer, $CC_MONTH Integer, " +
                "$CC_YEAR Integer, $CC_NAME TEXT);"
        db?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    fun setJWT(token: String): Boolean {
        //Create and/or open a database that will be used for reading and writing.
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(JWT,token)
        val success = db.insert(TABLE_NAME, null, values)
        db.close()
        Log.v("setJWTToken", "$success")
        return (Integer.parseInt("$success") != -1)
    }

    fun saveCreditCardInfo(card: Card): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(CC_NUMBER, card.cc_number)
        values.put(CC_YEAR, card.cc_year)
        values.put(CC_CCV, card.cc_cvv)
        values.put(CC_NAME, card.cc_name)
        values.put(CC_MONTH, card.cc_month)
        val success = db.insert(TABLE_NAME, null, values)
        db.close()
        Log.v("setCC", "$success")
        return (Integer.parseInt("$success") != -1)
    }

    fun retrieveCardInfo(): Card {
        val card = Card()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                card.cc_cvv = cursor.getInt(cursor.getColumnIndex(CC_CCV))
                card.cc_month = cursor.getInt(cursor.getColumnIndex(CC_MONTH))
                card.cc_name = cursor.getString(cursor.getColumnIndex(CC_NAME))
                card.cc_year = cursor.getInt(cursor.getColumnIndex(CC_YEAR))
                card.cc_number = cursor.getString(cursor.getColumnIndex(CC_NUMBER))
            }
        }
        cursor.close()
        db.close()
        return card
    }

    fun retrieveJWT(): String {
        var token: String = "";
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                token = cursor.getString(cursor.getColumnIndex(JWT))
            }
        }
        cursor.close()
        db.close()
        return token
    }

    companion object {
        private val DB_NAME = "store_db"
        private val DB_VERSIOM = 1;
        private val TABLE_NAME = "storage"
        private val ID = "id"
        private val JWT = "jwt"
        private val CC_NUMBER = "cc_number"
        private val CC_CCV = "cc_ccv"
        private val CC_MONTH = "cc_month"
        private val CC_YEAR = "cc_year"
        private val CC_NAME = "cc_name"
    }
}