package com.example.moonph

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ScrollView
import kotlinx.android.synthetic.main.activity_drugi_ekran.*
import kotlinx.android.synthetic.main.content_main_drugi.*
import android.support.design.widget.Snackbar
import android.widget.TextView
import calculating.*
import java.time.Year


class drugi_ekran : AppCompatActivity() {

    var rok:Int=2019
    var algOpt:Int =4
    var lista: MutableList<String> = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drugi_ekran)

        //overload functions and setlisteners
        //set listener for input of yearText
        yearText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if(s.toString().length>0) rok=s.toString().toInt()
                else rok=0
                if (rok <= 2200 && rok >= 1900) {
                    fillFields(rok)
                }
            }
        })
        //listener for floating buttons
        //adding
        val fab1: View = findViewById(R.id.plusYearButton)
        fab1.setOnClickListener({ view ->
            var pom:Int=0
            if(yearText.text.toString().length>0){
                pom=yearText.text.toString().toInt()+1
            }
            else pom=1
            yearText.setText(pom.toString())

        //    Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
          ///      .setAction("Action", null)
             ///   .show()
        })
        fab1.setOnLongClickListener({v: View? ->  true})

        val fab2: View = findViewById(R.id.minusYearButton)
        fab2.setOnClickListener({ view ->
            var pom:Int=0
            if(yearText.text.toString().length>0){
                pom=yearText.text.toString().toInt()
                if(pom>0)pom--
            }
            else pom=0
            yearText.setText(pom.toString())
            //    Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
            ///      .setAction("Action", null)
            ///   .show()
        })
        fab2.setOnLongClickListener({v: View? ->  true})
        val extras = intent.extras?:return
        algOpt = extras.getInt("algOpt")
        rok = extras.getInt("year")
      //  Log.e("lol rok",rok.toString())
      //  data0.text = algOpt.toString()
        yearText.setText(rok.toString())
    }

    override fun finish(){
        val data = Intent()//this,MainActivity::class.java)
        if(yearText.text.toString().length>0)
            rok = yearText.text.toString().toInt()
        else rok=0
        Log.e("fnish rok",rok.toString())
        data.putExtra("returnString1",rok)
        setResult(Activity.RESULT_OK,data)
        super.finish()
    }

    fun fillFields(year: Int):Boolean{
        //choose an algorithm based on variable "algOpt"
        when (algOpt) {
            1 -> {
                if(year>=1970) {
                    lista = iterateOverYear(year, { i: Int, i1: Int, i2: Int -> Simple(i, i1, i2) })
                    Log.e("alg","Simple")
                }
                else {
                    lista = iterateOverYear(year, { i: Int, i1: Int, i2: Int -> Trig2(i, i1, i2) })
                    Log.e("alg","Simple Trig2")
                }
            }
            2 -> {
                if (year <= 2100) {
                    lista = iterateOverYear(year, { i: Int, i1: Int, i2: Int -> Conway(i, i1, i2) })
                    Log.e("alg","Conway")
                }
                else{
                    lista = iterateOverYear(year, { i: Int, i1: Int, i2: Int -> Trig2(i, i1, i2) })
                    Log.e("alg","Conway Trig2")
                }
            }
            3 -> {
                lista= iterateOverYear(year,{ i: Int, i1: Int, i2: Int -> Trig1(i, i1, i2) })
                Log.e("alg","Conway Trig1")
            }
            4 -> {
                lista= iterateOverYear(year,{ i: Int, i1: Int, i2: Int -> Trig2(i, i1, i2) })
                Log.e("alg","Trig2")
            }
            else -> return false
        }
        //fill textView objects
        for(i in 0 until lista.size){
            var name:String="data"+i.toString()
            var id:Int=resources.getIdentifier(name,"id",packageName)
            if(id!=0){
                var tV:TextView=findViewById(id)
                tV.setText(lista[i])
            }
            else return true
        }
        return true
    }


}
