package com.mertkadir.sharelocation.model



import com.google.type.LatLng
import java.io.Serializable

data class Post(
    val comment:String, val locationName: String, val selectedLatLng: String
) : Serializable {
}