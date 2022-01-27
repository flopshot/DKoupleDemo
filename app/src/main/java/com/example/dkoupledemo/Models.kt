package com.example.dkoupledemo

import android.os.Parcelable

data class Region(
    val id: String,
    val name: String
)

data class Location(
    val id: String,
    val regionId: String,
    val name: String,
)