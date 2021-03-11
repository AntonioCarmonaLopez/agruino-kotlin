package com.acl.agruino_kotlin.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.acl.agruino_kotlin.models.Value
import com.acl.agruino_kotlin.models.ValueLog

class MainViewModel : ViewModel() {

    var date = MutableLiveData<String>()
    var values = MutableLiveData<List<ValueLog>>()
    var value = MutableLiveData<Value>()

    init {
        date.value = ""
    }

    fun updateDate(mDate: String) {
        date.value = mDate
    }
    fun updateValues(values: ArrayList<ValueLog>?) {
        this.values.value = values
    }
    fun updateValue(value: Value?) {
        this.value.value = value
    }
}