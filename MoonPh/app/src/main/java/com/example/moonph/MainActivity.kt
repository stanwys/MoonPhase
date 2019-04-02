package com.example.moonph

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import calculating.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*
class MainActivity : AppCompatActivity() {

    var algOption: Int = 0
    var year: Int? = null
    var phase: Double =0.0
    var today: Calendar = Calendar.getInstance()
    var today2: Calendar = Calendar.getInstance()
    val Y=Calendar.getInstance().get(Calendar.YEAR)
    val M=Calendar.getInstance().get(Calendar.MONTH)// as function argument pass M+1
    val D=Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

    val REQUESTCODE = 100
    //variable for storing intent to activate drugi_ekran activity
    var inT: Intent= Intent()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        algOption=4
        year=2019
        inT = Intent(this,drugi_ekran::class.java)
        Log.e("lol","onCreate")
        //WARNING:  Calendar months are from 0 to 11
        //          Function months are from 1 to 12
       // val Y=Calendar.getInstance().get(Calendar.YEAR)
       // val M=Calendar.getInstance().get(Calendar.MONTH)// as function argument pass M+1
       // val D=Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        fillStartFields({ i: Int, i1: Int, i2: Int -> Trig2(i, i1, i2) })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        menu.findItem(R.id.menu_tr2).isChecked=true
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.menu_sim -> {
                if(item.isChecked)item.isChecked=false
                else {
                    item.isChecked=true
                    fillStartFields({ i: Int, i1: Int, i2: Int -> Simple(i, i1, i2) })
                }
                algOption=1
            }
            R.id.menu_con -> {
                if(item.isChecked)item.isChecked=false
                else {
                    item.isChecked=true
                    if(Y<=2100)
                        fillStartFields({ i: Int, i1: Int, i2: Int -> Conway(i, i1, i2) })
                    else fillStartFields({ i: Int, i1: Int, i2: Int -> Trig2(i, i1, i2) })
                }
                algOption=2
            }
            R.id.menu_tr1 -> {
                if(item.isChecked)item.isChecked=false
                else {
                    item.isChecked=true
                    fillStartFields({ i: Int, i1: Int, i2: Int -> Trig1(i, i1, i2) })
                }
                algOption=3
            }
            R.id.menu_tr2 -> {
                if(item.isChecked)item.isChecked=false
                else {
                    item.isChecked=true
                    fillStartFields({ i: Int, i1: Int, i2: Int -> Trig2(i, i1, i2) })
                }
                algOption=4
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    fun pelnieClick(v: View){
        inT.putExtra("algOpt",algOption)
        inT.putExtra("year",year)
        startActivityForResult(inT,REQUESTCODE)
    }

    fun fillStartFields(oblicz:(Int, Int, Int)-> Double){
        today.set(Y,M,D)
        //calculating the last new Moon
        val lastNew = findLastNewMoon(today,{ i: Int, i1: Int, i2: Int -> oblicz(i,i1,i2) })
        //calculating the next full Moon
        today2.set(Y,M,D)
        val nextFull = findNextFullMoon(today2,{ i: Int, i1: Int, i2: Int -> oblicz(i,i1,i2) })
        //calculating moonPhase for today
        //phase= Simple(Y,M+1,D)
        //phase= Conway(Y,M+1,D)
        //phase= Trig1(Y,M+1,D)
        phase= oblicz(Y,M+1,D)

        var ileOdjac:Double = 0.0
        //set the right photo +
        //if phase is between new Moon and full Moon (maybe 1st quarter Moon)
        if(phase < 15){
            ileOdjac=15-phase
            if(phase < 4)imageView.setImageResource(R.drawable.nomoon)
            else if (phase < 12)imageView.setImageResource(R.drawable.quarter1moon)
            else imageView.setImageResource(R.drawable.fullmoon)
        }//if phase is between full Moon and new Moon (maybe 3rd quarter Moon)
        else if (phase >15){
            ileOdjac=phase-15
            if(phase<19)imageView.setImageResource(R.drawable.fullmoon)
            else if (phase<27)imageView.setImageResource(R.drawable.quarter3moon)
            else imageView.setImageResource(R.drawable.nomoon)
        }
        //calculations
        var procent:Double = ((Math.max(15-ileOdjac,0.0))/15) *100


        var str:String = "Dzisiaj: "+String.format("%.2f",procent)+"%"
        //  var str:String = "Dzisiaj: "+phase.toString()
        var str2:String = "Poprzedni nów: "+lastNew.get(Calendar.DAY_OF_MONTH).toString()+"."+
                (lastNew.get(Calendar.MONTH)+1).toString()+"."+lastNew.get(Calendar.YEAR).toString()+"r."
        var str3:String = "Następna pełnia: "+nextFull.get(Calendar.DAY_OF_MONTH).toString()+"."+
                (nextFull.get(Calendar.MONTH)+1).toString()+"."+nextFull.get(Calendar.YEAR).toString()+"r."
        //display today's Moon phase
        todayView.setText(str)
        //display the date of the last new Moon
        lastView.setText(str2)
        //display the date of the next full Moon
        nextView.setText(str3)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //if(data!=null){
       //     Log.e("LOl",data.extras.getString("returnString1")+":hej")}
        if((requestCode==REQUESTCODE) && (
        resultCode== Activity.RESULT_OK)){
            if(data!=null){
                if(data.hasExtra("returnString1")){
                   // Log.e("Lol","Yey!")
                   // todayView.setText( data.extras.getInt("returnString1").toString())
                    year=data.extras.getInt("returnString1")
                    //Log.e("Lol year", year.toString())
                }
            }
        }
    }
}
