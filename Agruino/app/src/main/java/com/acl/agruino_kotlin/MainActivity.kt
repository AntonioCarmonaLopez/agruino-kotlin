package com.acl.agruino_kotlin

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.acl.agruino_kotlin.interfaces.*
import com.acl.agruino_kotlin.models.User
import com.acl.agruino_kotlin.models.Value
import com.acl.agruino_kotlin.models.ValueLog
import com.acl.agruino_kotlin.viewModel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity(), LoginFragInterface,
    KeysInterface,
    WaterKeysInterface,
    HistoricalKeysInterface{

    private lateinit var mNav: NavController

    // we must create a new var initialize to null for store auth instance
    private lateinit var auth: FirebaseAuth

    private var values: ArrayList<Value>? = null
    private lateinit var mainViewModel: MainViewModel

    private enum class Values { Moisture, Turbidity, Conductibity, PH, TEMP };


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //init
        // Initialize Firebase Auth
        auth = Firebase.auth
        //instantiate viewmodel
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        //set keys list
        values = chargeList();
        //find
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        mNav = navHostFragment.navController

        //throw loginFragmen
        mNav.navigate(R.id.action_mainFragment_to_loginFragment)
    }

    private fun chargeList(): ArrayList<Value>? {
        var value: Value?
        if (values == null) {
            values = ArrayList()
            //item 1
            value = Value(Values.Moisture.toString(), "%")
            values?.add(value)
            //item 2
            value = Value(Values.Turbidity.toString(), "Transparency levels")
            values?.add(value)
            //item 3
            value = Value(Values.Conductibity.toString(), "MTU")
            values?.add(value)
            //item 4
            value = Value(Values.PH.toString(), (""))
            values?.add(value)
            //item 5
            value = Value(Values.TEMP.toString(), "Degrees")
            values?.add(value)
        }
        return values;
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        when (id) {
            R.id.mnuExit -> {
                moveTaskToBack(true);
                exitProcess(-1)
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onAccept(user: User) {
        Log.d("kotlin", user.userName)
        signIn(user)
    }

    private fun signIn(user: User) {
        auth.signInWithEmailAndPassword(user.userName, user.pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("kotlin", "signInWithEmail:success")
                    val user = auth.currentUser
                    mNav.navigate(R.id.action_loginFragment_to_keysFragment)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.d("kotlin", "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()

                }

                // ...
            }
    }

    override fun onMoisture() {
        val args = Bundle()
        var moiusture = values?.get(0)
        args.putParcelable("moisture", moiusture)
        mNav.navigate(R.id.action_keysFragment_to_moistureFragment, args)
    }

    override fun onWater() {
        val args = Bundle()
        args.putParcelableArrayList("values", values)
        mNav.navigate(R.id.action_keysFragment_to_waterKeysFragment, args)
    }

    override fun onHistorical() {
        val args = Bundle()
        args.putParcelableArrayList("values", values)
        mNav.navigate(R.id.action_keysFragment_to_historyKeysFragment, args)
    }

    override fun onConductivity() {
        val key = "Conductivity"
        val measure = "MTU"
        var value = setValue(key, measure)
        onWaterFragment(value)
    }

    override fun onTurbidity() {
        val key = "Turbidity"
        val measure = "Degress of transparency"
        var value = setValue(key, measure)
        onWaterFragment(value)
    }

    override fun onPH() {
        val key = "PH"
        val measure = ""
        var value = setValue(key, measure)
        onWaterFragment(value)
    }

    override fun onTemp() {
        val key = "TEMP"
        val measure = "º"
        var value = setValue(key, measure)
        onWaterFragment(value)
    }

    fun setValue(key: String, measure: String): Value {
        return Value(key, measure)
    }

    fun onWaterFragment(value: Value){
        val args = Bundle()
        args.putParcelable("value", value)
        mNav.navigate(R.id.action_waterKeysFragment_to_waterValuesFragment, args)
    }

    fun onHistoricalFragment(value: Value){
        val args = Bundle()
        args.putParcelable("value", value)
        args.putString("date", Tools.today())
        mNav.navigate(R.id.action_historyKeysFragment_to_historyValuesFragment, args)
    }

    override fun onHistoryMoisture() {
        val key = "Moisture"
        val measure = "%"
        var value = setValue(key, measure)
        onHistoricalFragment(value)
    }

    override fun onHystoryConductivity() {
        val key = "Conductivity"
        val measure = "MTU"
        var value = setValue(key, measure)
        onHistoricalFragment(value)
    }

    override fun onHistoryTurbidity() {
        val key = "Turbidity"
        val measure = "Degress of transparency"
        var value = setValue(key, measure)
        onHistoricalFragment(value)
    }

    override fun onHistoryPH() {
        val key = "PH"
        val measure = ""
        var value = setValue(key, measure)
        onHistoricalFragment(value)
    }

    override fun onHistoryTemp() {
        val key = "TEMP"
        val measure = "º"
        var value = setValue(key, measure)
        onHistoricalFragment(value)
    }

}