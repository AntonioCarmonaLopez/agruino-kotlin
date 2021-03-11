package com.acl.agruino_kotlin.fragment.readValues.water

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import com.acl.agruino_kotlin.R
import com.acl.agruino_kotlin.models.Value
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.getField
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class WaterValuesFragment : Fragment() {

    private var tvTittle: TextView? = null
    private var tvValues: TextView? = null
    private var tvMeasure: TextView? = null
    private var value: Value? = null
    private var db: FirebaseFirestore? = null
    private var settings: FirebaseFirestoreSettings? = null
    private lateinit var readValues: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (savedInstanceState != null) {

            }
        }
    }

    @ExperimentalStdlibApi
    override fun onAttach(context: Context) {
        super.onAttach(context)
        //init
        // instance that provide us a persistence unit
        settings = FirebaseFirestoreSettings.Builder()
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .build()
        db = FirebaseFirestore.getInstance()
        db!!.setFirestoreSettings(settings!!)
        adquireValues()
    }

    @ExperimentalStdlibApi
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_water_values, container, false)
        tvTittle = view.findViewById(R.id.tvTittle)
        tvValues = view.findViewById(R.id.tvValue)
        tvMeasure = view.findViewById(R.id.tvMeasure)
        if (arguments != null) value = arguments?.getParcelable("value")
        if (value?.key.equals("TEMP",ignoreCase = true))  tvTittle?.text = "Temperature"
        else tvTittle?.text = value?.key
        tvMeasure?.text = value?.measure

        return view
    }

    @ExperimentalStdlibApi
    fun adquireValues() {
        CoroutineScope(Dispatchers.IO).launch {

            readValues = launch {
                println("Thread "+Thread.currentThread().name)
                read()
            }
        }
    }

@ExperimentalStdlibApi
fun read() {
    val docRef = db!!.collection("values").document("YC0HJwj1qynXC12LVpAV")
    docRef.addSnapshotListener { document, e ->
        if (e != null) {
            Log.w("k", "Listen failed.", e)
            return@addSnapshotListener
        }

        if (document != null && document.exists()) {
            Log.d("k", "DocumentSnapshot data:" + document.data)
            value?.value = document.getField(value?.key.toString().lowercase())!!
            updateValues()
        } else {
            Log.d("k", "No such document")
        }
    }
}
    @SuppressLint("ResourceAsColor")
    private fun updateValues() {
        when (value?.key) {
            "Turbidity" ->
                if (value?.value!! < 1)
                    tvValues?.setTextColor(
                        AppCompatResources.getColorStateList(this.requireContext(), R.color.lime_green))
                else if (value?.value!! >= 1 && value?.value!! <= 3)
                    tvValues?.setTextColor(
                        AppCompatResources.getColorStateList(this.requireContext(), R.color.orange))
                else
                    tvValues?.setTextColor(
                        AppCompatResources.getColorStateList(this.requireContext(), R.color.red))
            "Conductibity" ->
                if (value?.value!! < 1)
                    tvValues?.setTextColor(
                        AppCompatResources.getColorStateList(this.requireContext(), R.color.orange))
                else if (value?.value!! >= 1 && value?.value!! <= 2)
                    tvValues?.setTextColor(
                        AppCompatResources.getColorStateList(this.requireContext(), R.color.lime_green))
                else
                    tvValues?.setTextColor(
                        AppCompatResources.getColorStateList(this.requireContext(), R.color.red))
            "PH" ->
                if (value?.value!! < 5.5) {
                    tvValues?.setTextColor(
                        AppCompatResources.getColorStateList(this.requireContext(), R.color.red)
                    )
                    value!!.measure = "Basic water"
                    tvMeasure?.text = value?.measure
                } else if (value?.value!! <= 6 && value?.value!! <= 5.5) {
                    tvValues?.setTextColor(
                        AppCompatResources.getColorStateList(this.requireContext(), R.color.orange)
                    )
                    value!!.measure = "Take care about water PH"
                    tvMeasure?.text = value?.measure
                } else if (value?.value!! > 6 && value?.value!! <= 7) {
                    tvValues?.setTextColor(
                        AppCompatResources.getColorStateList(
                            this.requireContext(),
                            R.color.lime_green)
                    )
                    value!!.measure = "Rigth PH"
                    tvMeasure?.text = value?.measure
                } else if (value?.value!! > 7 && value?.value!! <= 7.5) {
                    tvValues?.setTextColor(
                        AppCompatResources.getColorStateList(this.requireContext(), R.color.orange)
                    )
                    value!!.measure = "Take care about water PH"
                    tvMeasure?.text = value?.measure
                }
                else {
                    tvValues?.setTextColor(
                        AppCompatResources.getColorStateList(this.requireContext(), R.color.red)
                    )
                    value!!.measure = "Acid water"
                    tvMeasure?.text = value?.measure
                }
            "TEMP" ->
                if (value?.value!! < 0)
                    tvValues?.setTextColor(
                        AppCompatResources.getColorStateList(this.requireContext(), R.color.red))
                else if (value?.value!! >= 0 && value?.value!! <= 10)
                    tvValues?.setTextColor(
                        AppCompatResources.getColorStateList(this.requireContext(), R.color.orange))
                else if (value?.value!! > 10 && value?.value!! <= 20)
                    tvTittle?.setTextColor(
                        AppCompatResources.getColorStateList(this.requireContext(), R.color.lime_green))
                else if (value?.value!! > 20 && value?.value!! <= 25)
                    tvTittle?.setTextColor(
                        AppCompatResources.getColorStateList(this.requireContext(), R.color.orange))
                else
                    tvTittle?.setTextColor(
                        AppCompatResources.getColorStateList(this.requireContext(), R.color.red))
        }
        tvValues?.text = value?.value.toString()
    }
    override fun onDestroy() {
        super.onDestroy()
        db = null
        readValues.cancel()
    }

    override fun onDetach() {
        super.onDetach()
        db = null
        readValues.cancel()
    }
}