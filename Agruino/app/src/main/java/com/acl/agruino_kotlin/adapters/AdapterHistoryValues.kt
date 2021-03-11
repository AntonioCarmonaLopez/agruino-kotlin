package com.acl.agruino_kotlin.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.acl.agruino_kotlin.R
import com.acl.agruino_kotlin.fragment.HistoryKeysFragment
import com.acl.agruino_kotlin.models.ValueLog


public class AdapterHistoryValues(keys: ArrayList<ValueLog>?) :
    RecyclerView.Adapter<AdapterHistoryValues.VH>() {
    private var mKeys: ArrayList<ValueLog>? = null

    init {
        mKeys = keys
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.cv_historical,
            parent, false)
        return VH(itemView)
    }
    override fun onBindViewHolder(holder: VH, position: Int) {
        val value  = mKeys?.get(position)?.value
        val time  = mKeys?.get(position)?.time
        val date  = mKeys?.get(position)?.date

        holder.tvValueLog.text = value.toString()
        holder.tvTimeLog.text = time.toString()
        holder.tvDateLog.text = date.toString()
    }


    override fun getItemCount(): Int = mKeys?.size!!

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvValueLog: TextView = itemView.findViewById(R.id.tvValueLog)
        val tvTimeLog: TextView = itemView.findViewById(R.id.tvTimeLog)
        val tvDateLog: TextView = itemView.findViewById(R.id.tvDateLog)
    }
}