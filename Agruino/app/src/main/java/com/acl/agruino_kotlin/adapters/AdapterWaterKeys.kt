package com.acl.agruino_kotlin.adapters
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.acl.agruino_kotlin.R
import com.acl.agruino_kotlin.interfaces.ClickListenerWaterKeysRV
import kotlin.collections.ArrayList


public class AdapterWaterKeys(private val mListener:ClickListenerWaterKeysRV,keys: ArrayList<String>?) :
        RecyclerView.Adapter<AdapterWaterKeys.VH>() {
     private var mKeys: ArrayList<String>? = null

    init {
         mKeys = keys
     }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.cv_water_key,
                parent, false)
        return VH(itemView,mListener )
    }
    override fun onBindViewHolder(holder: VH, position: Int) {
        val currentItem = mKeys?.get(position)
        //holder.imValueIcon.setImageResource(currentItem.icon)
        holder.tvTittleValue.text = currentItem
        /*holder.itemView.setOnClickListener(object:View.OnClickListener {
            override fun onClick(v: View?) {
                Log.d("k", mKeys?.get(position)!!)

            }

        })*/
    }


    override fun getItemCount(): Int = mKeys?.size!!

    inner class VH(itemView: View,mListener:ClickListenerWaterKeysRV) : RecyclerView.ViewHolder(itemView) {
        val imValueIcon: ImageView = itemView.findViewById(R.id.imValueIcon)
        val tvTittleValue: TextView = itemView.findViewById(R.id.tvTittleValue)

        init {
            itemView.setOnClickListener {
//                val snack: String = "Item Postion clciked: $adapterPosition"
//                Snackbar.make(itemView, snack, Snackbar.LENGTH_SHORT).show()
                mListener.onClick(adapterPosition)
            }

        }

        }
}