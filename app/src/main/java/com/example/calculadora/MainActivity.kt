package com.example.calculadora

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.reflect.Array
import javax.xml.xpath.XPathExpression
import org.json.JSONObject

var historicoString: String = ""
var historicoJSON = JSONObject()
var historicoClass: HistoricoClass = HistoricoClass("","")
var listClass: ArrayList<HistoricoClass> = ArrayList()

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun atualizaHistorico(expression: String, result: String){
        if(historicoString.isEmpty()) historicoString = historicoString + expression + " = " + result
        else historicoString = historicoString + ";" + expression + " = " + result

        listClass.add(HistoricoClass(expression,result))
        historicoClass.expressao+= (expression+";")
        historicoClass.resultado+= (result+";")
        historicoJSON.put(expression,result)


    }

    fun IrparaHistorico(view: View){
        val intent = Intent(this, Historico::class.java)
        intent.putExtra("historicoString", historicoString)
        intent.putExtra("listClass", listClass)
        intent.putExtra("historicoClass", historicoClass)
        intent.putExtra("historicoJSON", historicoJSON.toString())
        startActivity(intent)
    }

    fun operation(view: View){
        val selecionado = view as Button
        var f = ""
        var newExpression = ""
        val currentExpression = txt_expressao.text.toString()
        val selecionadoValue = selecionado.text.toString()
        var newResultado: String = "-1"

        f = caract(selecionadoValue)

        if(f == "number"){
            newExpression = currentExpression+selecionadoValue
            newResultado =  eval(newExpression).toString()
        }else if(f == "operation"){
            if(currentExpression.length != 0) {
                if(caract(currentExpression[currentExpression.length-1].toString()) != "operation"){
                    newExpression = currentExpression+selecionadoValue
                }else newExpression = currentExpression.substring(0,currentExpression.length-1) + selecionadoValue
            }
        }else if(f == "clear"){
            newExpression = ""
            newResultado = ""
        }else if(f == "apaga"){
            if(currentExpression.length > 0){
                newExpression = currentExpression.substring(0,currentExpression.length-1)
                if(newExpression.length>0 && caract(newExpression[newExpression.length-1].toString()) != "operation"){
                    newResultado = eval(newExpression).toString()
                }else if(newExpression.length==0){
                    newResultado = ""
                }
            }
        }else if(f == "equal"){
            atualizaHistorico(currentExpression, txt_resultado.text.toString())
            newExpression = txt_resultado.text.toString()
            newResultado = ""
        }
        txt_expressao.text = newExpression
        if(newResultado!="-1") txt_resultado.text = newResultado
    }
}

fun caract(c: String) = when(c){
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" -> "number"
        "C" -> "clear"
        "/", "*", "-", "+" -> "operation"
        "=" -> "equal"
        "--" -> "apaga"
        else -> "clear"
    }


// --------------------------- FUNÇÃO EVAL -------------------------------------------//
fun eval(str: String): Double {
    return object : Any() {
        var pos = -1
        var ch = 0
        fun nextChar() {
            ch = if (++pos < str.length) str[pos].toInt() else -1
        }

        fun eat(charToEat: Int): Boolean {
            while (ch == ' '.toInt()) nextChar()
            if (ch == charToEat) {
                nextChar()
                return true
            }
            return false
        }

        fun parse(): Double {
            nextChar()
            val x = parseExpression()
            if (pos < str.length) throw RuntimeException("Unexpected: " + ch.toChar())
            return x
        }

        // Grammar:
        // expression = term | expression `+` term | expression `-` term
        // term = factor | term `*` factor | term `/` factor
        // factor = `+` factor | `-` factor | `(` expression `)`
        //        | number | functionName factor | factor `^` factor
        fun parseExpression(): Double {
            var x = parseTerm()
            while (true) {
                if (eat('+'.toInt())) x += parseTerm() // addition
                else if (eat('-'.toInt())) x -= parseTerm() // subtraction
                else return x
            }
        }

        fun parseTerm(): Double {
            var x = parseFactor()
            while (true) {
                if (eat('*'.toInt())) x *= parseFactor() // multiplication
                else if (eat('/'.toInt())) x /= parseFactor() // division
                else return x
            }
        }

        fun parseFactor(): Double {
            if (eat('+'.toInt())) return parseFactor() // unary plus
            if (eat('-'.toInt())) return -parseFactor() // unary minus
            var x: Double
            val startPos = pos
            if (eat('('.toInt())) { // parentheses
                x = parseExpression()
                eat(')'.toInt())
            } else if (ch >= '0'.toInt() && ch <= '9'.toInt() || ch == '.'.toInt()) { // numbers
                while (ch >= '0'.toInt() && ch <= '9'.toInt() || ch == '.'.toInt()) nextChar()
                x = str.substring(startPos, pos).toDouble()
            } else if (ch >= 'a'.toInt() && ch <= 'z'.toInt()) { // functions
                while (ch >= 'a'.toInt() && ch <= 'z'.toInt()) nextChar()
                val func = str.substring(startPos, pos)
                x = parseFactor()
                x =
                    if (func == "sqrt") Math.sqrt(x) else if (func == "sin") Math.sin(
                        Math.toRadians(
                            x
                        )
                    ) else if (func == "cos") Math.cos(Math.toRadians(x)) else if (func == "tan") Math.tan(
                        Math.toRadians(x)
                    ) else throw RuntimeException(
                        "Unknown function: $func"
                    )
            } else {
                throw RuntimeException("Unexpected: " + ch.toChar())
            }
            if (eat('^'.toInt())) x = Math.pow(x, parseFactor()) // exponentiation
            return x
        }
    }.parse()
}