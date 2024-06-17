package com.example.demo.model

import java.io.Serializable

data class LatLong(
    var lat: Double? = null,
    var lon: Double? = null
) : Serializable