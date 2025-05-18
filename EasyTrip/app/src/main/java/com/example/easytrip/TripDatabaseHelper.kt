package com.example.easytrip

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TripDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "TripDB"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "Trip"
        private const val COLUMN_ID = "id"
        private const val COLUMN_DESTINATION = "destination"
        private const val COLUMN_START_DATE = "startDate"
        private const val COLUMN_END_DATE = "endDate"
        private const val COLUMN_LOCATION = "location"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_DESTINATION TEXT, " +
                "$COLUMN_START_DATE TEXT, " +
                "$COLUMN_END_DATE TEXT, " +
                "$COLUMN_LOCATION TEXT)"
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertTrip(trip: Trip): Long {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_DESTINATION, trip.destination)
            put(COLUMN_START_DATE, trip.startDate)
            put(COLUMN_END_DATE, trip.endDate)
            put(COLUMN_LOCATION, trip.location)
        }
        val result = db.insert(TABLE_NAME, null, contentValues)
        db.close()
        return result
    }

    fun getAllTrips(): List<Trip> {
        val trips = mutableListOf<Trip>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)

        cursor.use {
            while (it.moveToNext()) {
                val id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID))
                val destination = it.getString(it.getColumnIndexOrThrow(COLUMN_DESTINATION))
                val startDate = it.getString(it.getColumnIndexOrThrow(COLUMN_START_DATE))
                val endDate = it.getString(it.getColumnIndexOrThrow(COLUMN_END_DATE))
                val location = it.getString(it.getColumnIndexOrThrow(COLUMN_LOCATION))
                val trip = Trip(id, destination, startDate, endDate, location)
                trips.add(trip)
            }
        }
        db.close()
        return trips
    }
}
