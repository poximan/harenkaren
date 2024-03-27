package com.example.demo.model

import java.io.Serializable

data class LatLong(
    var lat: Double = -42.765525,
    var lon: Double = -65.033470
) : Serializable