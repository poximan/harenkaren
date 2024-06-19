package com.example.demo.model

import java.io.Serializable

data class LatLong(
    var lat: Double = 0.0,
    var lon: Double = 0.0
) : Serializable