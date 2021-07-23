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
                "($ID Integer PRIMARY KEY, $JWT TEXT);"
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

    fun retrieveJWT(): String {
        var token: String = "";
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                if (cursor.getString(cursor.getColumnIndex(JWT)) != null) {
                    token = cursor.getString(cursor.getColumnIndex(JWT))
                }
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
    }
}