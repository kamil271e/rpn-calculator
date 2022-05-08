package com.example.rpn_calc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View

class MenuActivity : AppCompatActivity() {
    private var stack = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        readStack()
    }
    private fun readStack(){
        stack = intent.getSerializableExtra("stack").toString()
        //Log.i("stack", stack as String)
    }
    fun gotoMainActivity(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("stack", stack)
        startActivity(intent)
    }
}