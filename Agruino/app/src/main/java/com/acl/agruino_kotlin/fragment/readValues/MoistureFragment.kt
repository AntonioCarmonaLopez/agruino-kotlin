package com.acl.agruino_kotlin.fragment.readValues

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.acl.agruino_kotlin.R
import com.acl.agruino_kotlin.models.Value
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.getField
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class MoistureFragment : Fragment() {

    private var tvTittle: TextView? = null
    private var tvValues: TextView? = null
    private var tvMeasure: TextView? = null
    private var value: Value? = null
    private var db: FirebaseFirestore? = null
    private var settings: FirebaseFirestoreSettings? = null
    private lateinit var readValues:Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (savedInstanceState != null) {

            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //init
        // instance that provide us a persistence unit
        settings = FirebaseFirestoreSettings.Builder()
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .build()


        db = FirebaseFirestore.getInstance()
        db!!.firestoreSettings = settings!!

        adquireValues()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_moisture, container, false)
        tvTittle = view.findViewById(R.id.tvTittle)
        tvValues = view.findViewById(R.id.tvValue)
        tvMeasure = view.findViewById(R.id.tvMeasure)
        tvTittle?.setBackgroundColor(
            ContextCompat.getColor(this.requireContext(), R.color.purple)
        )
        if (arguments != null) value = arguments?.getParcelable("moisture")
        tvTittle?.text = value?.key
        tvMeasure?.text = value?.measure

        // Inflate the layout for this fragment
        return view
    }

    fun adquireValues() {
        CoroutineScope(IO).launch {

            readValues = launch {
                println("Thread "+Thread.currentThread().name)
                read()
            }
        }
    }
    fun read() {
        val docRef = db!!.collection("values").document("YC0HJwj1qynXC12LVpAV")
        docRef.addSnapshotListener { document, e ->
            if (e != null) {
                Log.w("k", "Listen failed.", e)
                return@addSnapshotListener
            }
            if (document != null && document.exists()) {
                Log.d("k", "DocumentSnapshot data:" + document.data)
                value?.value = document.getField("moisture")!!
                updateValues()
            } else {
                Log.d("k", "No such document")
            }
        }
    }

    fun updateValues() {
        tvValues?.text = value?.value.toString()
        putColors()
    }

    @SuppressLint("ResourceAsColor")
    fun putColors() {
        if (value?.value!! < 20)
            tvValues?.setTextColor(
                AppCompatResources.getColorStateList(this.requireContext(), R.color.red))
        else if (value?.value!! >= 20 && value?.value!! < 60)
            tvValues?.setTextColor(
                AppCompatResources.getColorStateList(this.requireContext(), R.color.orange))
        else
            tvValues?.setTextColor(
                AppCompatResources.getColorStateList(this.requireContext(), R.color.lime_green))
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

