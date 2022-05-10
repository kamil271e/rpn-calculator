package com.example.rpn_calc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.pow
import kotlin.math.sqrt
import android.util.Log

class MainActivity : AppCompatActivity() {
    private var canPushStack = true
    private lateinit var stack: MutableList<String>
    private var precision: Int = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        readIntents()
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
            else if (data.isBlank() && (stack.size > 1) || (stack.size == 1 && view.text.toString() == "SQRT")) {
                calcTextView.append(view.text)
                enterAction(view)
            }
        }
    }
    fun clearAction(view: View) {
        calcTextView.text = ""
        stack.clear()
        updateStackView()
    }
    fun enterAction(view: View) {
        var calcText = calcTextView.text.toString()
        calcTextView.text = ""
        var op = -1
        if (calcText.isEmpty()) return

        try {
            calcText.toFloat()
        }catch (e: NumberFormatException){
            canPushStack = false
        }

        if (canPushStack) { // adding values to stack
            calcText = checkPrecision(calcText)
            calcText = checkLength(calcText)
            stack.add(calcText)
            calcTextView.text = ""
            updateStackView()
        }else{
            when (calcText) {
                "+" -> op = 1
                "*" -> op = 2
                "-" -> op = 3
                "/" -> op = 4
                "POW" -> op = 5
                "SQRT" -> op = 6
            }
            canPushStack = true
            var result = if (op != 6) operation(stack.removeLast().toFloat(), stack.removeLast().toFloat(), op)
            else operation(1F, stack.removeLast().toFloat(), op)
            result = result?.let { checkPrecision(it) }
            result = result?.let { checkLength(it) }
            calcTextView.text = result?.let { redundantZerosSlice(it) }
        }
    }
    fun backSpaceAction(view: View) {
        val len = calcTextView.length()
        if(len > 0) {
            calcTextView.text = calcTextView.text.subSequence(0, len-1)
        }
    }
    private fun operation(b: Float, a: Float, op: Int) : String? {
        var intFlag = false
        if ((a.toInt()).toString() == redundantZerosSlice(a.toString()) && (b.toInt()).toString() == redundantZerosSlice(b.toString())) {
            intFlag = true
        }
        when(op){
            1 ->
                return if(intFlag) (a.toInt()+b.toInt()).toString()
                else (a+b).toString()
            2 ->
                return if(intFlag) (a.toInt()*b.toInt()).toString()
                else (a*b).toString()
            3 ->
                return if(intFlag) (a.toInt()-b.toInt()).toString()
                else (a-b).toString()
            4 ->
                return if(intFlag) (a.toInt()/b.toInt()).toString()
                else (a/b).toString()
            5 ->
                return if(intFlag) (a.pow(b.toInt())).toString()
                else (a.pow(b)).toString()
            6 -> return sqrt(a).toString()
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
    fun gotoMenuActivity(view: View) {
        val myIntent = Intent(this, MenuActivity::class.java)
        val temp = stack.joinToString(" ")
        myIntent.putExtra("stack", temp)
        startActivity(myIntent)
    }
    private fun checkPrecision(str: String) : String{
        if (str.split(".").size < 2) return str
        val decimalPoints = str.split(".")[1].length
        var text = str
        if(decimalPoints > precision){
            text = String.format("%.${precision}f", str.toFloat())
        }
        return text
    }
    private fun checkLength(str: String) : String{
        var text = str
        if (str.length > 14){
            text = str.subSequence(0,14).toString()
        }
        return text
    }
    private fun redundantZerosSlice(str: String): String {
        var index = -1
        var toSlice = 0
        index = str.indexOf(".", index)
        if (index == -1) return str
        for (i in (str.length - 1) downTo 0) {
            if (str[i] == '0') toSlice += 1
            else break
        }
        return str.subSequence(0, str.length - toSlice-1).toString()
    }
    private fun readIntents(){
        stack = intent.getSerializableExtra("stack").toString().split(" ").toMutableList()
        if (stack[0] != "null" && stack[0] != "") updateStackView()
        else stack.clear()
        precision = intent.getIntExtra("precision", precision)
    }
}