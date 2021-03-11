package com.acl.agruino_kotlin.fragment

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.acl.agruino_kotlin.MainActivity
import com.acl.agruino_kotlin.R
import com.acl.agruino_kotlin.interfaces.LoginFragInterface
import com.acl.agruino_kotlin.models.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {
    private lateinit var etUser: EditText
    private lateinit var etPass:EditText
    private lateinit var btAccept: Button
    private lateinit var btCancel:Button
    private lateinit var mListener: LoginFragInterface


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is LoginFragInterface) mListener = context;
        else throw RuntimeException("must implement LoginFragInterface")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v:View = inflater!!.inflate(R.layout.fragment_login, container, false)

        //findViewById
        etUser = v.findViewById(R.id.etUserName)
        etPass = v.findViewById(R.id.etPass)
        btAccept = v.findViewById(R.id.btAccept)
        btCancel = v.findViewById(R.id.btCancel)
        btAccept.setOnClickListener(bt_click)
        btCancel.setOnClickListener(bt_click)
        return v;
    }
        //click
        private val bt_click: View.OnClickListener = View.OnClickListener { view ->
            when (view.id) {
                R.id.btAccept-> {
                    var user = User()
                    user.userName = etUser.text.toString()
                    user.pass = etPass.text.toString()
                    if(checkEditText()) mListener.onAccept(user)
                    else Snackbar.make(view,R.string.shortPass,Snackbar.LENGTH_LONG).show()
                }
                R.id.btCancel -> {
                    clearEditText()
                }
            }
        }

    private  fun clearEditText(){
        etUser.text.clear()
        etPass.text.clear()
    }

    private fun checkEditText(): Boolean {
        if (TextUtils.isEmpty(etUser.text) || !TextUtils.isEmpty(etPass.text)){
            if (TextUtils.isEmpty(etUser.text)) etUser.requestFocus()
            else if (TextUtils.isEmpty(etPass.text)) etPass.requestFocus()
            else {
                if (etPass.text.length < 8) {
                    etPass.requestFocus()
                    return false
                }
            }
        }
        return true
    }
    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    //putString(ARG_PARAM1, param1)
                    //putString(ARG_PARAM2, param2)
                }
            }
    }
}