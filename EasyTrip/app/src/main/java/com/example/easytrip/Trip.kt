package com.example.easytrip

data class Trip(
    var id: Int = 0,
    var destination: String,
    var startDate: String,
    var endDate: String,
    var location: String
)
