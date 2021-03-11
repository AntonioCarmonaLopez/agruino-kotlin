package com.acl.agruino_kotlin.models

import android.os.Parcel
import android.os.Parcelable

class ValueLog : Parcelable {
    var value = 0f
    var date: String? = null
    var time: String? = null
    var dateUnix: Long = 0

    constructor()

    //parcelable
    constructor(parcel: Parcel) {
        value = parcel.readByte().toFloat()
        date = parcel.readString()
        time = parcel.readString()
        dateUnix = parcel.readLong()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeFloat(value)
        dest.writeString(date)
        dest.writeString(time)
        dest.writeLong(dateUnix)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ValueLog> {
        override fun createFromParcel(parcel: Parcel): ValueLog {
            return ValueLog(parcel)
        }

        override fun newArray(size: Int): Array<ValueLog?> {
            return arrayOfNulls(size)
        }
    }
}
