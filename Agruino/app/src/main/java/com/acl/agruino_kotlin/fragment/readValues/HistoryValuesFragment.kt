package com.acl.agruino_kotlin.fragment.readValues

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.acl.agruino_kotlin.R
import com.acl.agruino_kotlin.Tools
import com.acl.agruino_kotlin.adapters.AdapterHistoryValues
import com.acl.agruino_kotlin.models.Value
import com.acl.agruino_kotlin.models.ValueHistory
import com.acl.agruino_kotlin.models.ValueLog
import com.acl.agruino_kotlin.viewModel.MainViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ktx.getField
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class HistoryValuesFragment : Fragment() {
    private val PATHVALUES = "YC0HJwj1qynXC12LVpAV"
    private val DOCUMENTVALUES = "values"
    private val PATHHISTORY = "59DXbz5zbanZjBRhkTqv"
    private val DOCUMENTHISTORY = "values_history"
    private val DOCUMENTLOG = "values_log"
    private var mNavC: NavController? = null
    private var tvTittle: TextView? = null
    private var tvCurrentValueStat: TextView? = null
    private var tvHistorical: TextView? = null
    private var rvLog: RecyclerView? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var mAdapter: AdapterHistoryValues? = null
    private var valueHistory: ValueHistory? = null
    private var value: Value? = null
    private lateinit var mDate:String
    private var db: FirebaseFirestore? = null
    private var settings: FirebaseFirestoreSettings? = null
    private var mainViewModel: MainViewModel? = null
    private var values: ArrayList<ValueLog>? = null
    private var valuesFilter: ArrayList<ValueLog>? = null
    private var valueLog: ValueLog? = null
    private lateinit var readValues: Job
    private lateinit var readHistoricalValues: Job
    private lateinit var readLogValues: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    @ExperimentalStdlibApi
    override fun onAttach(context: Context) {
        super.onAttach(context)

        //init
        values = ArrayList()
        valuesFilter = ArrayList()

        // instance for configure chache size of a persistence unit
        // IOS & android devices, the persistence come activated by default
        settings = FirebaseFirestoreSettings.Builder()
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .build()
        db = FirebaseFirestore.getInstance()
        db!!.setFirestoreSettings(settings!!)
        setHasOptionsMenu(true)
        //instantiate viewmodel
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        mNavC = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        //read values in threads
        readValuesThread()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_filter, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        return when (item.itemId) {
            R.id.mnuFind -> {
                var arg = Bundle()
                arg.putParcelable("value", value)
                arg.putString("date", Tools.today())
                arg.putParcelableArrayList("values",values)
                mNavC?.navigate(R.id.action_global_filterFragment, arg)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @ExperimentalStdlibApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_history_values, container, false)
        if (arguments != null) {
            value = arguments?.getParcelable("value")
            values = arguments?.getParcelableArrayList("values")
            mDate = arguments?.getString("date").toString()
            mainViewModel?.updateValue(value)
        }
        //findbiewbyid
        tvTittle = view.findViewById(R.id.tvTittleStat)
        tvCurrentValueStat = view.findViewById(R.id.tvCurrentValueStat)
        tvHistorical = view.findViewById(R.id.tvHistoricalValue)
        rvLog = view.findViewById(R.id.rvLog)
        //init
        tvTittle?.text = value?.key

        return view
    }


    @SuppressLint("DefaultLocale")
    @ExperimentalStdlibApi
    fun adquireValues(document: String, pathDocument: String?) {
        val docRef = db!!.collection(document).document(
            pathDocument!!
        )
        valueHistory = ValueHistory()
        docRef.addSnapshotListener(EventListener { snapshot, e ->
            if (e != null) {
                Log.d("k", "Listen failed.", e)
                return@EventListener
            }
            if (snapshot != null && snapshot.exists()) {
                var key: String? = value?.key
                Log.d("k", "Current data: " + snapshot.data)
                if (document.equals(DOCUMENTVALUES, ignoreCase = true)) {
                    if (key != null)
                        value?.value = snapshot.getField(key.lowercase())!!
                    updateView()
                } else {
                    if (key != null)
                        valueHistory?.valueHistory = snapshot.getField(key.lowercase())!!
                }
            } else {
                Log.d("agruino", "Current data: null")
                valueHistory?.value = 0F
                valueHistory?.valueHistory = 0F
                value?.key?.let { Log.d("k", it) }
            }
            tvHistorical!!.text = java.lang.String.format(
                "%.2f",
                (valueHistory?.valueHistory?.div(365))?.times(Tools.calcDayOfYear())
            ).toString() + " " + value?.measure
        })
    }

    private fun updateView() {
        when (value?.key) {
            "Moisture" ->
                if (value?.value!! < 20)
                    tvCurrentValueStat?.setTextColor(
                        AppCompatResources.getColorStateList(this.requireContext(), R.color.red)
                    )
                else if (value?.value!! >= 20 && value?.value!! < 60)
                    tvCurrentValueStat?.setTextColor(
                        AppCompatResources.getColorStateList(this.requireContext(), R.color.orange)
                    )
                else
                    tvCurrentValueStat?.setTextColor(
                        AppCompatResources.getColorStateList(
                            this.requireContext(),
                            R.color.lime_green
                        )
                    )

            "Turbidity" ->
                if (value?.value!! < 1)
                    tvCurrentValueStat?.setTextColor(
                        AppCompatResources.getColorStateList(
                            this.requireContext(),
                            R.color.lime_green
                        )
                    )
                else if (value?.value!! >= 1 && value?.value!! <= 3)
                    tvCurrentValueStat?.setTextColor(
                        AppCompatResources.getColorStateList(this.requireContext(), R.color.orange)
                    )
                else
                    tvCurrentValueStat?.setTextColor(
                        AppCompatResources.getColorStateList(this.requireContext(), R.color.red)
                    )
            "Conductibity" ->
                if (value?.value!! < 1)
                    tvCurrentValueStat?.setTextColor(
                        AppCompatResources.getColorStateList(this.requireContext(), R.color.orange)
                    )
                else if (value?.value!! >= 1 && value?.value!! <= 2)
                    tvCurrentValueStat?.setTextColor(
                        AppCompatResources.getColorStateList(
                            this.requireContext(),
                            R.color.lime_green
                        )
                    )
                else
                    tvCurrentValueStat?.setTextColor(
                        AppCompatResources.getColorStateList(this.requireContext(), R.color.red)
                    )
            "PH" ->
                if (value?.value!! < 5.5) {
                    tvCurrentValueStat?.setTextColor(
                        AppCompatResources.getColorStateList(this.requireContext(), R.color.red)
                    )
                    value!!.measure = "Basic water"
                } else if (value?.value!! <= 6 && value?.value!! <= 5.5) {
                    tvCurrentValueStat?.setTextColor(
                        AppCompatResources.getColorStateList(this.requireContext(), R.color.orange)
                    )
                    value!!.measure = "Take care about water PH"
                } else if (value?.value!! > 6 && value?.value!! <= 7) {
                    tvCurrentValueStat?.setTextColor(
                        AppCompatResources.getColorStateList(
                            this.requireContext(),
                            R.color.lime_green
                        )
                    )
                    value!!.measure = "Rigth PH"
                } else if (value?.value!! > 7 && value?.value!! <= 7.5) {
                    tvCurrentValueStat?.setTextColor(
                        AppCompatResources.getColorStateList(this.requireContext(), R.color.orange)
                    )
                    value!!.measure = "Take care about water PH"
                } else {
                    tvCurrentValueStat?.setTextColor(
                        AppCompatResources.getColorStateList(this.requireContext(), R.color.red)
                    )
                    value!!.measure = "Acid water"
                }
            "TEMP" ->
                if (value?.value!! < 0)
                    tvCurrentValueStat?.setTextColor(
                        AppCompatResources.getColorStateList(this.requireContext(), R.color.red)
                    )
                else if (value?.value!! >= 0 && value?.value!! <= 10)
                    tvCurrentValueStat?.setTextColor(
                        AppCompatResources.getColorStateList(this.requireContext(), R.color.orange)
                    )
                else if (value?.value!! > 10 && value?.value!! <= 20)
                    tvCurrentValueStat?.setTextColor(
                        AppCompatResources.getColorStateList(
                            this.requireContext(),
                            R.color.lime_green
                        )
                    )
                else if (value?.value!! > 20 && value?.value!! <= 25)
                    tvCurrentValueStat?.setTextColor(
                        AppCompatResources.getColorStateList(this.requireContext(), R.color.orange)
                    )
                else
                    tvCurrentValueStat?.setTextColor(
                        AppCompatResources.getColorStateList(this.requireContext(), R.color.red)
                    )
        }
        tvCurrentValueStat!!.text = java.lang.String.format("%.2f", value?.value)
            .toString() + " " + value?.measure
    }

    @ExperimentalStdlibApi
    fun readValuesThread() {
        CoroutineScope(Dispatchers.IO).launch {

            readValues = launch {
                println("Thread " + Thread.currentThread().name)
                adquireValues(DOCUMENTVALUES, PATHVALUES)
            }
            readHistoricalValues = launch {
                println("Thread " + Thread.currentThread().name)
                adquireValues(DOCUMENTHISTORY, PATHHISTORY)
            }
            readLogValues = launch {
                println("Thread " + Thread.currentThread().name)
                adquireValues(DOCUMENTLOG)
            }
        }
    }

    @ExperimentalStdlibApi
    fun adquireValues(document: String) {

        db!!.collection(document)
            .get()
            .addOnCompleteListener(object : OnCompleteListener<QuerySnapshot> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onComplete(task: Task<QuerySnapshot>) {
                    if (task.isSuccessful) {
                        value = mainViewModel!!.value.value
                        var key = value?.key
                        println("camkey" + key)


                        values = ArrayList()
                        for (document in task.result!!) {
                            valueLog = ValueLog()

                            valueLog?.value = document.getField(key?.lowercase()!!)!!
                            valueLog?.date = document.getField("dateString")
                            valueLog?.time = document.getField("time")
                            valueLog?.dateUnix = document.getField("date")!!
                            values?.add(valueLog!!)
                            updateRV()

                        }
                        mainViewModel?.updateValues(values)
                    } else {
                        Log.d("agruino", "Error getting documents: ", task.exception)
                    }
                }
            })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun string2long(date: String): Long {

        val format = SimpleDateFormat("yyyy-MM-dd")
        try {
            var parsedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            println(date+" : "+parsedDate)
            val unix = parsedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().epochSecond
            println("unix"+unix)
            return unix
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return 0
    }
    
    fun updateRV() {
        // Init RecyclerView
        mAdapter = AdapterHistoryValues(values)
        mAdapter!!.notifyDataSetChanged()
        rvLog!!.setHasFixedSize(true)
        rvLog!!.layoutManager = LinearLayoutManager(requireActivity())
        rvLog!!.addItemDecoration(
            DividerItemDecoration(
                requireActivity(),
                DividerItemDecoration.VERTICAL
            )
        )
        rvLog!!.adapter = mAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        db = null
        readValues.cancel()
        readHistoricalValues.cancel()
        readLogValues.cancel()
    }

    override fun onDetach() {
        super.onDetach()
        db = null
        readValues.cancel()
        readHistoricalValues.cancel()
        readLogValues.cancel()
    }
}


