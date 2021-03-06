package com.example.rpn_calc

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.pow
import kotlin.math.sqrt
import android.util.Log



class MainActivity : AppCompatActivity() {
    private var canPushStack = true
    private lateinit var stack: MutableList<String>
    private var precision: Int = 6
    private var maxWidth: Int = 14
    private var theme = "purple"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        readIntents()
        loadTheme()
    }
    fun numberAction(view: View) {
        if (view is Button && calcTextView.text.length < maxWidth && calcTextView.text.toString() != "0") calcTextView.append(view.text)
    }
    fun operationAction(view: View) {
        val data = calcTextView.text.toString()
        if (view is Button && calcTextView.text.length < maxWidth) {
            if (view.text == "."){
                if (!data.contains(".") && data.isNotEmpty()) calcTextView.append(view.text)
            }
            else if (data.isBlank() && (stack.size >= 2) || (stack.size == 1 && view.text.toString() == "SQRT")){
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
        var calcText = calcTextView.text.toString(); calcTextView.text = ""
        var op = -1
        if (calcText.isEmpty()) return
        try { calcText.toDouble() }
        catch (e: NumberFormatException){ canPushStack = false }
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
            val result = if (op == 4 &&  stack.last() == "0") ""
            else if (op != 6) operation(stack.removeLast(), stack.removeLast(), op)
            else if (stack.last().toFloat() >= 0F) operation("", stack.removeLast(), op)
            else ""
            calcTextView.text = preprocessValue(result.toString()) // TODO refactor
        }
    }
    fun backSpaceAction(view: View) {
        if(calcTextView.text.isNotBlank() && calcTextView.text.toString() != "Infinity" && calcTextView.text.toString() != "NaN") calcTextView.text = calcTextView.text.dropLast(1)
    }
    fun swapAction(view: View) {
        if (stack.size >= 2){
            val temp1 = stack.removeLast(); val temp2 = stack.removeLast()
            stack.add(temp1); stack.add(temp2)
            updateStackView()
        }
    }
    fun dropAction(view: View) {
        if(stack.isNotEmpty()){
            stack.removeLast()
            updateStackView()
        }
    }
    fun signAction(view: View) {
        val temp = calcTextView.text.toString()
        calcTextView.text = when {
            temp.isBlank() -> "-"
            temp=="-" -> ""
            temp[0] == '-' -> temp.subSequence(1,temp.length).toString()
            temp=="0" -> "0"
            else -> "-$temp"
        }
    }
    fun gotoMenuActivity(view: View) {
        val myIntent = Intent(this, MenuActivity::class.java)
        val temp = stack.joinToString(" ")
        myIntent.putExtra("stack", temp)
        myIntent.putExtra("theme", theme)
        startActivity(myIntent)
    }
    private fun operation(b: String, a: String, op: Int) : String? {
        when(op){
            1 -> return (a.toDouble()+b.toDouble()).toString()
            2 -> return (a.toDouble()*b.toDouble()).toString()
            3 -> return (a.toDouble()-b.toDouble()).toString()
            4 -> return (a.toDouble()/b.toDouble()).toString()
            5 -> return ((a.toFloat()).pow(b.toFloat())).toString()
            6 -> return sqrt(a.toDouble()).toString()
        }
        return null
    }
    private fun updateStackView(){
        var temp = ""
        if (stack.size < 4){
            for (i in 1..(4 - stack.size)) temp += "\n"
        }
        val end = if (stack.size > 4) 4
        else stack.size
        for (i in end downTo 1) temp += stack[stack.size-i] + "\n"
        stackTextView.text = temp.subSequence(0, temp.length-1)
    }
    private fun checkPrecision(str: String) : String{
        if (!str.contains(".")) return str
        val decimalPoints = str.split(".")[1]
        val diff = precision - decimalPoints.length
        return when {
            diff > 0 -> str
            decimalPoints.contains("E") ->
                str.subSequence(0, str.length -(decimalPoints.length - precision)).toString() + "E" + decimalPoints.split("E")[1]
            else ->
                str.subSequence(0, str.length -(decimalPoints.length - precision)).toString()
        }
    }
    private fun checkLength(str: String) : String{
        if (str.length <= maxWidth) return str
        var result = str; var exp = ""
        if (result.contains("E")){
            result = result.split("E")[0]
            exp = "E" + result.split("E")[1]
        }
        return if (result.length > maxWidth && exp.isBlank())
            str.subSequence(0,maxWidth).toString()
        else
            result.subSequence(0,maxWidth-exp.length).toString() + exp
    }
    private fun redundantZerosSlice(str: String): String {
        var toSlice = 0 ; var temp = -1
        if (!str.contains(".")) return str
        for (i in (str.length - 1) downTo 0) {
            if (str[i] == '0') toSlice += 1
            else { temp = i; break }
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
        theme = intent.getStringExtra("theme").toString()
        if(theme == "null") theme="purple"
    }
    private fun loadTheme(){
        val stackView = findViewById<TextView>(R.id.stackTextView)
        val btnList = listOf(findViewById(R.id.AC), findViewById(R.id.SWAP), findViewById(R.id.BACK), findViewById<Button>(R.id.COMMA),
            findViewById(R.id.DROP), findViewById(R.id.DIV), findViewById(R.id.ENTER), findViewById(R.id.MENU), findViewById(R.id.MIN),
            findViewById(R.id.MULT), findViewById(R.id.PLUS), findViewById(R.id.SQRT), findViewById(R.id.POW), findViewById(R.id.SIGN))
        Log.i("theme2", theme)
        when (theme) {
            "purple" -> {
                stackView.setBackgroundResource(R.drawable.purple)
                for (btn in btnList){
                    btn.setTextColor(Color.parseColor("#FF6200EE"))
                    supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FF6200EE")))
                }
            }
            "teal" -> {
                stackView.setBackgroundResource(R.drawable.teal)
                for (btn in btnList){
                    btn.setTextColor(Color.parseColor("#FF018786"))
                    supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FF018786")))
                }
            }
            else -> {
                stackView.setBackgroundResource(R.drawable.red)
                for (btn in btnList){
                    btn.setTextColor(Color.RED)
                    supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FF0000")))
                }
            }
        }
    }
}