package com.example.rpn_calc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*
import android.util.Log
import kotlin.math.pow
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {
    private var canPushStack = true
    private var stack = ArrayDeque<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }
    fun numberAction(view: View) {
        if (view is Button){
            var flag = false
            for (c in calcTextView.text){
                if (!c.isDigit() && c.toString() != ".") flag = true
            }
            if (!flag) calcTextView.append(view.text)
        }
    }
    fun operationAction(view: View) {
        val data = calcTextView.text.toString()
        if (view is Button) {
            if (view.text == "."){
                val index = -1
                if (data.indexOf(".", index) == -1 && data.isNotEmpty())
                    calcTextView.append(view.text)
            }
            else if (data == "") calcTextView.append(view.text)
        }
    }
    fun clearAction(view: View) {
        calcTextView.text = ""
        stack.clear()
        updateStackView()
    }
    fun enterAction(view: View) {

        val calcText = calcTextView.text.toString()
        var op = -1
        if (calcText.isEmpty()) return

        try {
            calcText.toFloat()
        }catch (e: NumberFormatException){
            canPushStack = false
        }

        if (canPushStack) { // adding values to stack
            stack.add(calcText)
            calcTextView.text = ""
            updateStackView()

        }else{
            when (calcText) {
                "+" -> op = 1
                "*" -> op = 2
                "-" -> op = 3
                "/" -> op = 4
                "pow" -> op = 5
                "sqrt" -> op = 6
            }
            canPushStack = true
            if (op != 6) calcTextView.text = operation(stack.removeLast().toFloat(), stack.removeLast().toFloat(), op).toString()
            else calcTextView.text = operation(1F, stack.removeLast().toFloat(), op).toString()
        }
    }
    fun backSpaceAction(view: View) {
        val len = calcTextView.length()
        if(len > 0) {
            calcTextView.text = calcTextView.text.subSequence(0, len-1)
        }
    }
    private fun operation(b: Float, a: Float, op: Int) : Float? {
        when(op){
            1 -> return a+b
            2 -> return a*b
            3 -> return a-b
            4 -> return a/b // TODO 0 handling
            5 -> return a.pow(b)
            6 -> return sqrt(a)
            }
        return null
    }
    private fun updateStackView(){
        var temp = ""
        if (stack.size < 4){
            for (i in 1..(4 - stack.size)){
                temp += "0\n"
            }
        }
        val end = if (stack.size > 4) 4
        else stack.size
        for (i in end downTo 1){
            temp += stack[stack.size-i] + "\n"
        }
        Log.i("stack_view", temp)
        stackTextView.text = temp.subSequence(0, temp.length-1)
    }
    fun swapAction(view: View) {
        if (stack.size >= 2){
            val temp1 = stack.removeLast()
            val temp2 = stack.removeLast()
            stack.add(temp1)
            stack.add(temp2)
            updateStackView()
        }
    }
    fun dropAction(view: View) {
        if(stack.isNotEmpty()){
            stack.removeLast()
            updateStackView()
        }
    }
}