package com.acl.agruino_kotlin.models

import android.os.Parcel
import android.os.Parcelable

class Value : Parcelable {
    var key: String?
    var value = 0f
    var measure: String?

    constructor(key: String?, measure: String?) {
        this.key = key
        this.measure = measure
    }

    //parcelable
    constructor(parcel: Parcel) {
        key = parcel.readString()
        value = parcel.readByte().toFloat()
        measure = parcel.readString()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(key)
        dest.writeFloat(value)
        dest.writeString(measure)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Value> {
        override fun createFromParcel(parcel: Parcel): Value {
            return Value(parcel)
        }

        override fun newArray(size: Int): Array<Value?> {
            return arrayOfNulls(size)
        }
    }
}
