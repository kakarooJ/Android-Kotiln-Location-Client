package com.kakaroo.footprinterclient.Entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FootPrinter(var id: Long, var time: String?, var latitude: Double, var longitude: Double) : Parcelable {
/*    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readDouble()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(time)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FootPrinter> {
        override fun createFromParcel(parcel: Parcel): FootPrinter {
            return FootPrinter(parcel)
        }

        override fun newArray(size: Int): Array<FootPrinter?> {
            return arrayOfNulls(size)
        }
    }
*/
}
