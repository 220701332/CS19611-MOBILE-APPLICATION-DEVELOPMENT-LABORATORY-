package com.example.easytrip

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.Locale

class AddTripActivity : AppCompatActivity() {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    private lateinit var destinationEditText: EditText
    private lateinit var startDateEditText: EditText
    private lateinit var endDateEditText: EditText
    private lateinit var locationButton: Button
    private lateinit var saveButton: Button
    private lateinit var locationTextView: TextView

    private lateinit var dbHelper: TripDatabaseHelper
    private lateinit var locationManager: LocationManager
    private var currentLocation: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_trip)

        // Initialize views using view binding or findViewById
        destinationEditText = findViewById(R.id.etDestination)
        startDateEditText = findViewById(R.id.etStartDate)
        endDateEditText = findViewById(R.id.etEndDate)
        locationButton = findViewById(R.id.btnGetLocation)
        saveButton = findViewById(R.id.btnSaveTrip)
        locationTextView = findViewById(R.id.tvLocation)

        dbHelper = TripDatabaseHelper(this)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationButton.setOnClickListener {
            requestLocation()
        }

        saveButton.setOnClickListener {
            saveTrip()
        }
    }

    private fun requestLocation() {
        // Check if location permission is granted
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request location permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission already granted, get location
            getLocation()
        }
    }

    private fun getLocation() {
        try {
            // Check if GPS is enabled
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(this, "Please enable GPS", Toast.LENGTH_SHORT).show()
                return
            }
            locationManager.requestSingleUpdate(
                LocationManager.GPS_PROVIDER,
                locationListener,
                null
            )
        } catch (e: SecurityException) {
            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show()
        }
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val geocoder = Geocoder(this@AddTripActivity, Locale.getDefault())
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            currentLocation = addresses?.get(0)?.getAddressLine(0) ?: "Unknown Location"
            locationTextView.text = currentLocation
        }

        override fun onProviderDisabled(provider: String) {
            Toast.makeText(this@AddTripActivity, "GPS disabled", Toast.LENGTH_SHORT).show()
        }

        override fun onProviderEnabled(provider: String) {
            Toast.makeText(this@AddTripActivity, "GPS enabled", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, get location
                getLocation()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveTrip() {
        val destination = destinationEditText.text.toString()
        val startDate = startDateEditText.text.toString()
        val endDate = endDateEditText.text.toString()

        if (destination.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
        } else {
            val trip = Trip(
                destination = destination,
                startDate = startDate,
                endDate = endDate,
                location = currentLocation
            )
            dbHelper.insertTrip(trip)
            Toast.makeText(this, "Trip Added Successfully!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}