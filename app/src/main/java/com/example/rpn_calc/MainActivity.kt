package com.example.rpn_calc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*
import android.util.Log

class MainActivity : AppCompatActivity() {
    private var canAddOp = false
    private var canDecimal = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
    fun numAction(view: View) {
        if (view is Button){
            if (view.text == "."){
                if (canDecimal){
                    workTextView.append(view.text)
                }
                canDecimal = false
            }
            else
                workTextView.append(view.text)
            canAddOp = true
        }
    }
    fun opAction(view: View) {
        if (view is Button) {
            workTextView.append(view.text)
            canAddOp = false
            canDecimal = true
        }
    }
    fun clearAction(view: View) {
        workTextView.text = ""
        resTextView.text = ""
    }
    fun equalAction(view: View) {
        val temp = solve().toString()
        clearAction(view)
        workTextView.text = temp
    }

    fun backSpaceAction(view: View) {
        val len = workTextView.length()
        if(len > 0) {
            workTextView.text = workTextView.text.subSequence(0, len-1)
        }
    }

    private fun operation(a: Int, b: Int, op: Int) : Int? {
        when(op){
            1 -> return a+b
            2 -> return a*b
            3 -> return b-a
            4 -> {
                if(a!=0) {
                    Log.i("division", "$a $b") // czyta dzielenie w odwr kolejnosci
                    return b/a
                }
            }
            else -> print("Coś poszło nie tak...")
        }
        return null
    }

    private fun readEquation(): MutableList<String> { // text view represented as a list of num-s and op-s
        /*var currDigit = ""
        for (c in workTextView.text){
            if (c.isDigit()){
                currDigit += c
            }
            else{
                data.add(currDigit)
                currDigit = ""
                data.add(c.toString())
            }
        }
        if (currDigit != "") data.add(currDigit)*/
        /*var currDigit = ""
        for (c in workTextView.text){
            if(c.isDigit() || c == '.'){
                currDigit += c
            }
            else{
                list.add(currDigit.toFloat())
                currDigit = ""
                list.add(c) // adding operator
            }
        }
        if (currDigit != ""){
            list.add(currDigit.toFloat())
        }*/
        return workTextView.text.split(" ") as MutableList<String>
    }

    private fun solve(): Int {
        val stack = ArrayDeque<Int>()
        val equation = readEquation()
        var op = -1
        // Log.i("stack", equation.toString())
        if (equation.isEmpty()) return 0
        for (x in equation){
            var flag = false
            for (c in x){
                if (!c.isDigit()) flag = true
            }
            if (!flag){ // numbers
                stack.add(x.toInt())
            }
            else{ // operator occurred
                when (x) {
                    "+" -> op = 1
                    "*" -> op = 2
                    "-" -> op = 3
                    "/" -> op = 4
                }
                operation(stack.removeLast(), stack.removeLast(), op)?.let { stack.add(it) }
            }
        }
        return stack.last()
    }

    fun spaceAction(view: View) {
        workTextView.append(" ")
    }
}