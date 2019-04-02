package calculating

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

fun Simple(year:Int,month:Int,day:Int):Double
{
  //  Log.e("ksek",year.toString()+" "+
   //         month.toString()+" "+
    //        day.toString())
    var lp = 2551443;
    var now:Calendar = Calendar.getInstance();now.set(year,month-1,day,20,35,0);
    var new_moon:Calendar = Calendar.getInstance();new_moon.set(1970, 0, 7, 20, 35, 0);
    var phase = ((now.getTimeInMillis() - new_moon.getTimeInMillis())/1000) % lp;
    return Math.floor((phase /(24*3600)).toDouble())// + 1;
}

fun Conway(year:Int, month:Int, day:Int):Double
{
    var r = year % 100;
    var r2:Double=0.0
    r %= 19;
    if (r>9){ r -= 19;}
    r = ((r * 11) % 30) + month + day;
    if (month<3){r += 2;}
    r2= r.toDouble()
    if (year < 2000)r2=r2-4
    else r2=r2-8.3
    //r2 -= (year<2000) ? 4 : 8.3
    r2 = Math.floor(r2+0.5)%30;
    if (r2 < 0.0 ) r2=r2+30
    return r2
    //return (r < 0) ? r+30 : r;
}

fun Trig1(year:Int,month:Int,day:Int):Double {
    var thisJD = julday(year,month,day);
    var degToRad = 3.14159265 / 180;
    //var K0//, T, T2, T3, J0, F0, M0, M1, B1, oldJ;
    var K0 = Math.floor((year-1900)*12.3685);
    var T = (year-1899.5) / 100;
    var T2 = T*T;
    var T3 = T*T*T;
    var J0 = 2415020 + 29*K0;
    var F0 = 0.0001178*T2 - 0.000000155*T3 + (0.75933 + 0.53058868*K0) - (0.000837*T + 0.000335*T2);
    var M0 = 360*(GetFrac(K0*0.08084821133)) + 359.2242 - 0.0000333*T2 - 0.00000347*T3;
    var M1 = 360*(GetFrac(K0*0.07171366128)) + 306.0253 + 0.0107306*T2 + 0.00001236*T3;
    var B1 = 360*(GetFrac(K0*0.08519585128)) + 21.2964 - (0.0016528*T2) - (0.00000239*T3);
    var phase = 0;
    var jday:Double = 0.0
    var oldJ:Double = 0.0
    while (jday < thisJD) {
        var F = F0 + 1.530588*phase;
        var M5 = (M0 + phase*29.10535608)*degToRad;
        var M6 = (M1 + phase*385.81691806)*degToRad;
        var B6 = (B1 + phase*390.67050646)*degToRad;
        F -= 0.4068*Math.sin(M6) + (0.1734 - 0.000393*T)*Math.sin(M5);
        F += 0.0161*Math.sin(2*M6) + 0.0104*Math.sin(2*B6);
        F -= 0.0074*Math.sin(M5 - M6) - 0.0051*Math.sin(M5 + M6);
        F += 0.0021*Math.sin(2*M5) + 0.0010*Math.sin(2*B6-M6);
        F += 0.5 / 1440;
        oldJ=jday;
        jday = J0 + 28*phase + Math.floor(F);
        phase++;
    }
    return (thisJD-oldJ)%30;
}

fun Trig2(year:Int,month:Int,day:Int):Double {
    var n = Math.floor(12.37 * (year -1900 + ((1.0 * month - 0.5)/12.0)));
    var RAD = 3.14159265/180.0;
    var t = n / 1236.85;
    var t2 = t * t;
    var AS = 359.2242 + 29.105356 * n;
    var AM = 306.0253 + 385.816918 * n + 0.010730 * t2;
    var xtra = 0.75933 + 1.53058868 * n + ((1.178e-4) - (1.55e-7) * t) * t2;
    xtra += (0.1734 - 3.93e-4 * t) * Math.sin(RAD * AS) - 0.4068 * Math.sin(RAD * AM);
    var i :Double = 0.0
    if(xtra > 0.0) i=Math.floor(xtra)
    else i=Math.ceil(xtra - 1.0)
    var j1 = julday(year,month,day);
    var jd = (2415020 + 28 * n) + i;
    return (j1-jd + 30)%30;
}

fun GetFrac(fr:Double):Double {	return (fr - Math.floor(fr));}

fun julday(year:Int, month:Int, day:Int):Double {
    //if (year < 0) { year++; }
    var jy = year
    if (jy<0)jy++
    var jm = month +1;
    if (month <= 2) {jy--;	jm += 12;	}
    var jul = Math.floor(365.25 *jy) + Math.floor(30.6001 * jm) + day + 1720995;
    if (day+31*(month+12*year) >= (15+31*(10+12*1582))) {
        var ja = Math.floor(0.01 * jy);
        jul = jul + 2 - ja + Math.floor(0.25 * ja);
    }
    return jul;
}

fun findLastNewMoon(today:Calendar, oblicz:(Int, Int, Int)-> Double):Calendar{

    //decrement in order to find the last new Moon
    var tod:Calendar=today
    var phase:Double =oblicz(tod.get(Calendar.YEAR),
        tod.get(Calendar.MONTH)+1,tod.get(Calendar.DAY_OF_MONTH)) //initial value
    //if today is not the day of new Moon then while loop starts
   // Log.e("dataNew",tod.get(Calendar.YEAR).toString()+" "+tod.get(Calendar.MONTH).toString()+" "+tod.get(Calendar.DAY_OF_MONTH).toString())

    while(phase!=0.0){
        tod.add(Calendar.DAY_OF_MONTH,-1)
        phase=oblicz(tod.get(Calendar.YEAR),
            tod.get(Calendar.MONTH)+1,tod.get(Calendar.DAY_OF_MONTH))
      //  Log.e("dataNew",tod.get(Calendar.YEAR).toString()+" "+tod.get(Calendar.MONTH).toString()+" "+tod.get(Calendar.DAY_OF_MONTH).toString())
    }
    return tod
}

fun findNextFullMoon(today:Calendar,oblicz: (y:Int,m:Int,d:Int)-> Double):Calendar{

    //increment in order to find the next full Moon

    var tod:Calendar=today
    var phase:Double =oblicz(tod.get(Calendar.YEAR),
        tod.get(Calendar.MONTH)+1,tod.get(Calendar.DAY_OF_MONTH)) //initial value
    //if today is not the day of new Moon then while loop starts
   // Log.e("dataFull",tod.get(Calendar.YEAR).toString()+" "+tod.get(Calendar.MONTH).toString()+" "+tod.get(Calendar.DAY_OF_MONTH).toString())
    while(phase!=15.0){
        tod.add(Calendar.DAY_OF_MONTH,1)
        phase=oblicz(tod.get(Calendar.YEAR),
            tod.get(Calendar.MONTH)+1,tod.get(Calendar.DAY_OF_MONTH))
      //  Log.e("dataFull",tod.get(Calendar.YEAR).toString()+" "+tod.get(Calendar.MONTH).toString()+" "+tod.get(Calendar.DAY_OF_MONTH).toString())
    }
    return tod
}

//calculate when a full moon will appear
fun iterateOverYear(year: Int,oblicz: (y:Int,m:Int,d:Int)-> Double):MutableList<String>{
    val sdf = SimpleDateFormat("dd.MM.yyyy")
    var lista: MutableList<String> = ArrayList<String>()
    var phase:Double = 0.0
    var start:Calendar= Calendar.getInstance()
    var end:Calendar= Calendar.getInstance()
    start.set(year,0,1)
    end.set(year+1,0,1)
    //while loop
    while(start.getTimeInMillis()!=end.getTimeInMillis()){
        phase=oblicz(start.get(Calendar.YEAR),
            start.get(Calendar.MONTH)+1,start.get(Calendar.DAY_OF_MONTH))
        if(phase==15.0){
            var str:String=/*sdf.format(start)+".r"*/start.get(Calendar.DAY_OF_MONTH).toString()+"."+(start.get(Calendar.MONTH)+1).toString()+"."+
                    start.get(Calendar.YEAR).toString()+"r."
            lista.add(str)
        }
        start.add(Calendar.DAY_OF_MONTH,1)
    }
    return lista
}
