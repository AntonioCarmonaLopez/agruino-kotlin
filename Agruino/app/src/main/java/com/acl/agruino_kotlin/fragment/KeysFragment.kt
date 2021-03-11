package com.acl.agruino_kotlin.fragment

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.acl.agruino_kotlin.R
import com.acl.agruino_kotlin.interfaces.KeysInterface

class KeysFragment : Fragment() {

    private lateinit var mListener: KeysInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is KeysInterface) mListener = context
        else throw RuntimeException(context.toString()
                    + " must implement KeysInterface")
        Toast.makeText(this.context,R.string.login_ok, Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.mnu_moisture -> {
                mListener.onMoisture()
                true
            }
            R.id.mnu_water -> {
                mListener.onWater()
                return true
            }
            R.id.mnu_historical -> {
                mListener.onHistorical()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_keys, container, false)
    }
}