package com.example.calculadora

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_historico.*

class Historico : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historico)
        getHistorico1()
    }

    fun getHistorico1(){
        val historicoString = intent.getStringExtra("historicoString").toString().replace(";","\n")
        val listClass = intent.getParcelableArrayListExtra<HistoricoClass>("listClass")
        val historicoClass = intent.getParcelableExtra<HistoricoClass>("historicoClass")
        var historicoJSON = intent.getStringExtra("historicoJSON")


        txt_historicoClass.text = ""
        if(historicoClass!=null){
            val expressoes = historicoClass.expressao.split(";")
            val resultados = historicoClass.resultado.split(";")
            for(i in 0..expressoes.size-2){
                txt_historicoClass.text = txt_historicoClass.text.toString() + expressoes[i] + " = " + resultados[i] + "\n"
            }
            //txt_historico2.text = historicoClass?.expressao
        }
        txt_historicoString.text = historicoString?: ""
        txt_historicoListClass.text = ""
        if (listClass != null) {
            for (i in listClass){
                txt_historicoListClass.text = txt_historicoListClass.text.toString() + "\n" + i.expressao + " = " + i.resultado
            }
        }
        txt_historicoJSON.text = ""
        if (historicoJSON != null) {
            var historico = historicoJSON.substring(1,historicoJSON.length-1).replace("\"","").replace(":", " = ").split(",")
            for(i in 0..historico.size-1){
                txt_historicoJSON.text = txt_historicoJSON.text.toString() + historico[i] + "\n"
            }
        }


    }
}