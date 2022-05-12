package com.example.rpn_calc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.NumberPicker
import android.view.View
import android.widget.Button
import android.graphics.drawable.ColorDrawable
import android.graphics.Color

class MenuActivity : AppCompatActivity() {
    private var stack = ""
    private var precision: Int = 6
    private lateinit var theme : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        readStack()
        loadPrecision()
        loadTheme()
    }
    private fun readStack(){
        stack = intent.getSerializableExtra("stack").toString()
    }
    private fun loadPrecision(){
        val numberPicker: NumberPicker = findViewById(R.id.numberPicker)
        numberPicker.minValue = 1
        numberPicker.maxValue = 6
        numberPicker.wrapSelectorWheel = true
        numberPicker.setOnValueChangedListener {
                _, _, newVal ->
            precision = newVal
        }
    }
    private fun loadTheme(){
        val numberPicker: NumberPicker = findViewById(R.id.numberPicker2)
        numberPicker.minValue = 0
        numberPicker.maxValue = 2
        numberPicker.wrapSelectorWheel = false
        val values = mutableListOf("purple", "teal", "red")
        theme = intent.getStringExtra("theme").toString()
        for (i in 0 until values.size){
            if (values[i] == theme){
                values.removeAt(i)
                values.add(0, theme)
            }
        }
        numberPicker.displayedValues = values.toTypedArray()
        val button = findViewById<Button>(R.id.goBack)
        changeColor(theme, button)
        numberPicker.setOnValueChangedListener {
                _, _, newVal ->
            theme = values[newVal]
            changeColor(theme, button)
        }
    }
    fun gotoMainActivity(view: View) {
        val myIntent = Intent(this, MainActivity::class.java)
        val myBundle = Bundle()
        myBundle.putString("stack", stack)
        myBundle.putInt("precision", precision)
        myBundle.putString("theme", theme)
        myIntent.putExtras(myBundle)
        startActivity(myIntent)
    }
    private fun changeColor(color: String, button: Button){
        when (color) {
            "purple" -> {
                button.setBackgroundResource(R.drawable.purple)
                supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FF6200EE")))
            }
            "teal" -> {
                button.setBackgroundResource(R.drawable.teal)
                supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FF018786")))
            }
            else -> {
                button.setBackgroundResource(R.drawable.red)
                supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FF0000")))
            }
        }
    }
}