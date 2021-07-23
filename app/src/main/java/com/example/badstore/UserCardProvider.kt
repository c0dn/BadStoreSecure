package com.example.badstore

import android.content.*
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri

class UserCardProvider : ContentProvider() {

    companion object {
        // defining authority so that other application can access it
        private const val PROVIDER_NAME = "com.example.badstore.card"

        // defining content URI
        private const val URL = "content://$PROVIDER_NAME/cards"

        // parsing the content URI
        val CONTENT_URI: Uri = Uri.parse(URL)
        const val uriCode = 1
        var uriMatcher: UriMatcher? = null
        private val values: HashMap<String, String>? = null

        // declaring name of the database
        const val DATABASE_NAME = "cards_db"

        // declaring table name of the database
        const val TABLE_NAME = "cards"

        // declaring version of the database
        const val DATABASE_VERSION = 2

        private const val CC_NUMBER = "cc_number"
        private const val CC_CCV = "cc_ccv"
        private const val CC_MONTH = "cc_month"
        private const val CC_YEAR = "cc_year"
        private const val CC_NAME = "cc_name"

        // sql query to create the table
        const val CREATE_DB_TABLE = "CREATE TABLE $TABLE_NAME " +
                "(id Integer PRIMARY KEY, $CC_NUMBER TEXT, $CC_CCV Integer, $CC_MONTH Integer, " +
                "$CC_YEAR Integer, $CC_NAME TEXT);"

        init {

            // to match the content URI
            // every time user access table under content provider
            uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

            // to access whole table
            uriMatcher!!.addURI(
                PROVIDER_NAME,
                "cards",
                uriCode
            )

            // to access a particular row
            // of the table
            uriMatcher!!.addURI(
                PROVIDER_NAME,
                "cards/*",
                uriCode
            )
        }
    }

    override fun getType(uri: Uri): String {
        return when (uriMatcher!!.match(uri)) {
            uriCode -> "vnd.android.cursor.dir/cards"
            else -> throw IllegalArgumentException("Unsupported URI: $uri")
        }
    }

    override fun onCreate(): Boolean {
        val context = context
        val dbHelper = DatabaseHelper(context)
        db = dbHelper.writableDatabase
        return db != null
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        var sortOrder = sortOrder
        val qb = SQLiteQueryBuilder()
        qb.tables = TABLE_NAME
        if (sortOrder == null || sortOrder === "") {
            sortOrder = "id"
        }
        val c = qb.query(
            db, projection, selection, selectionArgs, null,
            null, sortOrder
        )
        c.setNotificationUri(context!!.contentResolver, uri)
        return c
    }

    // adding data to the database
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val rowID = db!!.insert(TABLE_NAME, "", values)
        if (rowID > 0) {
            val _uri =
                ContentUris.withAppendedId(CONTENT_URI, rowID)
            context!!.contentResolver.notifyChange(_uri, null)
            return _uri
        }
        throw SQLiteException("Failed to add a record into $uri")
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        var count = 0
        count = when (uriMatcher!!.match(uri)) {
            uriCode -> db!!.update(TABLE_NAME, values, selection, selectionArgs)
            else -> throw IllegalArgumentException("Unknown URI $uri")
        }
        context!!.contentResolver.notifyChange(uri, null)
        return count
    }

    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        var count = 0
        count = when (uriMatcher!!.match(uri)) {
            uriCode -> db!!.delete(TABLE_NAME, selection, selectionArgs)
            else -> throw IllegalArgumentException("Unknown URI $uri")
        }
        context!!.contentResolver.notifyChange(uri, null)
        return count
    }

    // creating object of database
    // to perform query
    private var db: SQLiteDatabase? = null

    // creating a database
    private class DatabaseHelper  // defining a constructor
        (context: Context?) : SQLiteOpenHelper(
        context,
        DATABASE_NAME,
        null,
        DATABASE_VERSION
    ) {
        // creating a table in the database
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(CREATE_DB_TABLE)
        }

        override fun onUpgrade(
            db: SQLiteDatabase,
            oldVersion: Int,
            newVersion: Int
        ) {

            // sql query to drop a table
            // having similar name
            db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
            onCreate(db)
        }
    }


}