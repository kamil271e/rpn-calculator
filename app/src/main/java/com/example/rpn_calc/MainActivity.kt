package com.example.rpn_calc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.sqrt
import kotlin.math.pow

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
                try{if (calcTextView.text[0] == '-') continue}
                catch (e: java.lang.NumberFormatException){}
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
            else if (data.isBlank() && (stack.size > 1) || (stack.size == 1 && view.text.toString() == "SQRT")){
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
        var op = -1
        calcTextView.text = ""
        if (calcText.isEmpty()) return
        try {
            calcText.toDouble()
        }catch (e: NumberFormatException){
            canPushStack = false
        }
        if (canPushStack) { // adding values to stack
            calcText = preprocessValue(calcText)
            stack.add(calcText)
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
            var result : String? = ""
            if (op == 4 &&  stack.last() == "0") {}
            else if (op != 6) result = operation(stack.removeLast(), stack.removeLast(), op)
            else if (stack.last().toFloat() >= 0F) result = operation("", stack.removeLast(), op)
            calcTextView.text = result?.let { preprocessValue(it) }
        }
    }
    fun backSpaceAction(view: View) {
        val len = calcTextView.length()
        if(len > 0) {
            calcTextView.text = calcTextView.text.subSequence(0, len-1)
        }
    }
    private fun operation(b: String, a: String, op: Int) : String? {
        when(op){
            1 -> return (a.toDouble()+b.toDouble()).toString()
            2 -> return (a.toDouble()*b.toDouble()).toString()
            3 -> return (a.toDouble()-b.toDouble()).toString()
            4 -> return (a.toDouble()/b.toDouble()).toString()
            5 -> return ((a.toFloat()).pow(b.toInt())).toString()
            6 -> return sqrt(a.toDouble()).toString()
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
    fun changeSign(view: View) {
        var temp = calcTextView.text.toString()
        temp = when {
            temp.isBlank() -> "-"
            temp=="-" -> ""
            temp[0] == '-' -> temp.subSequence(1,temp.length).toString()
            else -> "-$temp"
        }
        calcTextView.text = temp
    }
    fun gotoMenuActivity(view: View) {
        val myIntent = Intent(this, MenuActivity::class.java)
        val temp = stack.joinToString(" ")
        myIntent.putExtra("stack", temp)
        startActivity(myIntent)
    }
    private fun checkPrecision(str: String) : String{
        if (str.split(".").size < 2) return str
        val decimalPoints = str.split(".")[1]
        val exp: String
        val result: String
        val diff = precision - decimalPoints.length
        when {
            diff > 0 -> result = str
            decimalPoints.split("E").size > 1 -> {
                exp = "E" + decimalPoints.split("E")[1]
                result = str.subSequence(0, str.length -(decimalPoints.length - precision) - exp.length).toString() + exp
            }
            else -> result = str.subSequence(0, str.length -(decimalPoints.length - precision)).toString()
        }
        return result
    }
    private fun checkLength(str: String) : String{
        if (str.length <= 14) return str
        var result = str
        var exp = ""
        if (result.split("E").size > 1){
            result = result.split("E")[0]
            exp = "E" + result.split("E")[1]
        }
        if (result.length > 14 && exp == "")
            result = str.subSequence(0,14).toString()
        else
            result = result.subSequence(0,14-exp.length).toString() + exp

        return result
    }
    private fun redundantZerosSlice(str: String): String {
        var toSlice = 0
        var temp = -1
        if (str.split(".").size < 2) return str
        for (i in (str.length - 1) downTo 0) {
            if (str[i] == '0') toSlice += 1
            else {
                temp = i; break
            }
        }
        if (toSlice > 0 && str[temp] == '.') toSlice+=1
        return str.subSequence(0, str.length - toSlice).toString()
    }
    private fun preprocessValue(str: String): String {
        return checkLength(checkPrecision(redundantZerosSlice(str)))
    }
    private fun readIntents(){
        stack = intent.getSerializableExtra("stack").toString().split(" ").toMutableList()
        if (stack[0] != "null" && stack[0] != "") updateStackView()
        else stack.clear()
        precision = intent.getIntExtra("precision", precision)
    }
}