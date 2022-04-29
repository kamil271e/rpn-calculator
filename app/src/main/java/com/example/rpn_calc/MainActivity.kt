package com.example.rpn_calc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*
import android.util.Log

class MainActivity : AppCompatActivity() {
    private var canPushStack = true
    private var canUseOperator = false
    private var canPutComma = false
    private var stack = ArrayDeque<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }
    fun numberAction(view: View) {
        if (view is Button){
            calcTextView.append(view.text)
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
            else calcTextView.append(view.text)
        }
    }
    fun clearAction(view: View) {
        calcTextView.text = ""
        stack.clear()
    }
    fun enterAction(view: View) {

        val calcText = calcTextView.text.toString()
        var op = -1
        if (calcText.isEmpty()) return

        try {
            calcText.toInt()
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
            }
            //operation(stack.removeLast().toInt(), stack.removeLast().toInt(), op)?.let { stack.add(it.toString()) }
            val a1 = stack.removeLast().toInt()
            val a2 = stack.removeLast().toInt()
            // Log.i("inside","$calcText $op $a1 $a2")
            val result = operation(a1, a2, op).toString()
            canPushStack = true
            calcTextView.text = result
        }
        //Log.i("stack_values", stack.toString())
    }

    fun backSpaceAction(view: View) {
        val len = calcTextView.length()
        if(len > 0) {
            calcTextView.text = calcTextView.text.subSequence(0, len-1)
        }
    }

    private fun operation(a: Int, b: Int, op: Int) : Int? {
        when(op){
            1 -> return a+b
            2 -> return a*b
            3 -> return b-a
            4 -> {
                if(a!=0) {
                    //Log.i("division", "$a $b") // division in reversed order
                    return b/a
                }
            }
            else -> Log.i("error","Something went wrong...")
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
}