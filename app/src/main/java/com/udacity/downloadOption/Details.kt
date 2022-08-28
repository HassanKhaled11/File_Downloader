package com.udacity.downloadOption

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
 data class Details(
   var name: String,
   var status : String
):Parcelable