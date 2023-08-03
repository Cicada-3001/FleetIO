package com.example.test.models

import android.os.Parcel
import android.os.Parcelable

data class Route(
    val userId: String?,
    val startPoint: String?,
    val endPoint: String?,
    val startingCoordinate: String?,
    val endingCoordinate: String?,
    val estimateDistance: Double,
    val estimateTime: String?,
    val estimateFareAmt: Double,

): Parcelable {
    val _id: String?= null
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readDouble()
    ) {
    }
    constructor(
        _id:String,
        userId: String,
        startPoint: String,
        endPoint: String,
        startingCoordinate: String?,
        endingCoordinate: String?,
        estimateDistance:  Double,
        estimateTime: String,
        estimateFareAmt: Double
    ) : this(
        userId,
        startPoint,
        endPoint,
        startingCoordinate,
        endingCoordinate,
        estimateDistance,
        estimateTime,
        estimateFareAmt)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeString(startPoint)
        parcel.writeString(endPoint)
        parcel.writeDouble(estimateDistance)
        parcel.writeString(estimateTime)
        parcel.writeDouble(estimateFareAmt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Route> {
        override fun createFromParcel(parcel: Parcel): Route {
            return Route(parcel)
        }

        override fun newArray(size: Int): Array<Route?> {
            return arrayOfNulls(size)
        }
    }
}








