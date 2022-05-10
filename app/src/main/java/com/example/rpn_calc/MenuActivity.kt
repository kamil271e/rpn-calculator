package com.example.rpn_calc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.NumberPicker
import androidx.core.os.bundleOf

class MenuActivity : AppCompatActivity() {
    private var stack = ""
    private var precision: Int = 6
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        readStack()
        loadPrecision()
    }
    private fun readStack(){
        stack = intent.getSerializableExtra("stack").toString()
        //Log.i("stack", stack as String)
    }
    private fun loadPrecision(){
        val numberPicker: NumberPicker = findViewById(R.id.numberPicker)
        numberPicker.minValue = 1
        numberPicker.maxValue = 6
        val values = arrayOf(1,2,3,4,5,6)
        numberPicker.wrapSelectorWheel = true

        numberPicker.setOnValueChangedListener(){
                _, _, newVal ->
            precision = values[newVal]
        }
    }
    fun gotoMainActivity(view: View) {
        val myIntent = Intent(this, MainActivity::class.java)
        val myBundle = Bundle()
        myBundle.putString("stack", stack)
        myBundle.putInt("precision", precision)
        myIntent.putExtras(myBundle)
        startActivity(myIntent)
    }
}