package com.mertkadir.sharelocation.model



import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

annotation class Parcelize

@Parcelize
 data class Post(
    var comment:String,
    var locationName: String,
    var date :Timestamp,
    var latLang: LatLng
    ) : Parcelable



