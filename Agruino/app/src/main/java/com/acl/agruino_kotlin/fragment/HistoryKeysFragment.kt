package com.acl.agruino_kotlin.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.acl.agruino_kotlin.R
import com.acl.agruino_kotlin.adapters.AdapterHistoryKeys
import com.acl.agruino_kotlin.interfaces.ClickHistoryKeysInterfaceRV
import com.acl.agruino_kotlin.interfaces.HistoricalKeysInterface
import com.acl.agruino_kotlin.models.Value

class HistoryKeysFragment : Fragment(),ClickHistoryKeysInterfaceRV{

    private var rvHistoryKeys: RecyclerView? = null
    private var mAdapter: AdapterHistoryKeys? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var valuesKeys: ArrayList<Value>? = null
    private var historyKeys: ArrayList<String>? = null
    private lateinit var mListener: HistoricalKeysInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        historyKeys = ArrayList()
        arguments?.let  { valuesKeys = arguments?.getParcelableArrayList("values") }
        for (value in 0..4) {
            Log.d("k", valuesKeys?.get(value)!!.key.toString())
           historyKeys?.add(valuesKeys?.get(value)!!.key.toString())
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HistoricalKeysInterface) mListener = context
        else throw RuntimeException(context.toString()
                + " must implement HistoricalKeysInterface")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_history_keys, container, false)
        //findview
        rvHistoryKeys = view.findViewById(R.id.rvHistory)
        //init
        mAdapter = AdapterHistoryKeys(this,historyKeys)
        layoutManager = LinearLayoutManager(this.context)
        rvHistoryKeys?.layoutManager = layoutManager
        rvHistoryKeys?.adapter = mAdapter
        return view
    }

    override fun onClick(pos: Int) {
        Log.d("k",valuesKeys?.get(pos)?.key!!)
            when (valuesKeys?.get(pos)?.key!!){
                "Moisture" -> mListener.onHistoryMoisture()
                "Turbidity" -> mListener.onHistoryTurbidity()
                "Conductibity" -> mListener.onHistoryTurbidity()
                "TEMP" -> mListener.onHistoryTemp()
                "PH" -> mListener.onHistoryPH()
            }
    }
}