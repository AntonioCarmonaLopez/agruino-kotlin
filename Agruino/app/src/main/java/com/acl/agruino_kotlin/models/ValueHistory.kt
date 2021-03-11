package com.acl.agruino_kotlin.models

import android.os.Parcel
import android.os.Parcelable

class ValueHistory : Parcelable {
    var value = 0f
    var valueHistory = 0f

    constructor()

    //parcelable
    constructor(parcel: Parcel) {
        value = parcel.readByte().toFloat()
        valueHistory = parcel.readByte().toFloat()
    }



    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeFloat(value)
        dest.writeFloat(valueHistory)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ValueHistory> {
        override fun createFromParcel(parcel: Parcel): ValueHistory {
            return ValueHistory(parcel)
        }

        override fun newArray(size: Int): Array<ValueHistory?> {
            return arrayOfNulls(size)
        }
    }
}
