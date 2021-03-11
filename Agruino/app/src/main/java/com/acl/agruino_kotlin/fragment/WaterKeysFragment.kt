package com.acl.agruino_kotlin.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.acl.agruino_kotlin.R
import com.acl.agruino_kotlin.adapters.AdapterWaterKeys
import com.acl.agruino_kotlin.interfaces.ClickListenerWaterKeysRV
import com.acl.agruino_kotlin.interfaces.WaterKeysInterface
import com.acl.agruino_kotlin.models.Value
import kotlinx.coroutines.Job
import kotlin.collections.ArrayList

class WaterKeysFragment : Fragment(),ClickListenerWaterKeysRV {

    private var rvWaterKeys: RecyclerView? = null
    private var mAdapter: AdapterWaterKeys? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var valuesKeys: ArrayList<Value>? = null
    private var waterKeys: ArrayList<String>? = null
    private lateinit var mListener:  WaterKeysInterface
    private lateinit var readValues: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        waterKeys = ArrayList()
        arguments?.let  { valuesKeys = arguments?.getParcelableArrayList("values") }
        for (value in 1..4) {
            Log.d("k", valuesKeys?.get(value)!!.key.toString())
            waterKeys?.add(valuesKeys?.get(value)!!.key.toString())
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is WaterKeysInterface) mListener = context
        else throw RuntimeException(context.toString()
                    + " must implement WaterKeysInterface")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
       val view = inflater.inflate(R.layout.fragment_water_keys, container, false)
        //findview
        rvWaterKeys = view.findViewById(R.id.rvWaterKeys)
        //init
        mAdapter = AdapterWaterKeys(this,waterKeys)
        layoutManager = LinearLayoutManager(this.context)
        rvWaterKeys?.layoutManager = layoutManager
        rvWaterKeys?.adapter = mAdapter
       return view
    }

    override fun onClick(pos: Int) {
        Log.d("k", "f"+waterKeys!!.get(pos))
        when (waterKeys!!.get(pos)){
            "Turbidity" -> mListener.onTurbidity()
            "Conductibity" -> mListener.onConductivity()
            "TEMP" -> mListener.onTemp()
            "PH" -> mListener.onPH()
        }
    }
}