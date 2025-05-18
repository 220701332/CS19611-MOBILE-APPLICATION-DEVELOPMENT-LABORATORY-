package com.example.easytrip

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var addTripButton: Button
    private lateinit var tripsRecyclerView: RecyclerView
    private lateinit var tripAdapter: TripAdapter
    private lateinit var dbHelper: TripDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        dbHelper = TripDatabaseHelper(this)

        addTripButton = findViewById(R.id.btnAddTrip)
        tripsRecyclerView = findViewById(R.id.rvTrips)

        addTripButton.setOnClickListener {
            val intent = Intent(this, AddTripActivity::class.java)
            startActivity(intent)
        }

        // Set up RecyclerView
        tripsRecyclerView.layoutManager = LinearLayoutManager(this)
        tripAdapter = TripAdapter(dbHelper.getAllTrips())
        tripsRecyclerView.adapter = tripAdapter
    }

    override fun onResume() {
        super.onResume()
        tripAdapter.updateTrips(dbHelper.getAllTrips())
    }

    class TripAdapter(private var trips: List<Trip>) :
        RecyclerView.Adapter<TripAdapter.TripViewHolder>() {

        class TripViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val destinationTextView: TextView = itemView.findViewById(R.id.tvDestination)
            val startDateTextView: TextView = itemView.findViewById(R.id.tvStartDate)
            val endDateTextView: TextView = itemView.findViewById(R.id.tvEndDate)
            val locationTextView: TextView = itemView.findViewById(R.id.tvLocation)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.trip_item, parent, false)
            return TripViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
            val currentTrip = trips[position]
            holder.destinationTextView.text = currentTrip.destination
            holder.startDateTextView.text = currentTrip.startDate
            holder.endDateTextView.text = currentTrip.endDate
            holder.locationTextView.text = currentTrip.location
        }

        override fun getItemCount() = trips.size

        @SuppressLint("NotifyDataSetChanged")
        fun updateTrips(newTrips: List<Trip>) {
            trips = newTrips
            notifyDataSetChanged()
        }
    }
}
