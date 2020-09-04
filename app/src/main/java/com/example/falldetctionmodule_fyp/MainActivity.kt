package com.example.falldetctionmodule_fyp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.text.DecimalFormat
import kotlin.math.pow
import kotlin.math.sqrt



class MainActivity : AppCompatActivity() , SensorEventListener
{

     lateinit var s: SensorManager;
     lateinit var accelrometer:Sensor

    var x:Double=0.0
    var y:Double=0.0
    var z:Double=0.0

    var counter:Int=0

    var lasttime:Long=0
    var newtime:Long=0
    var diff:Long=0
    var AccRound=0.0
    var personmovement:Boolean=false
    var status:Boolean=false
    var minv:Boolean=false
    var maxv:Boolean=false
    var timerstartornot:Boolean=false
    var timer:CountDownTimer?=null
    lateinit var fallstatus:Button
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
         Log.i("th"," activity id= ${Thread.currentThread().id}")
        s= getSystemService(Context.SENSOR_SERVICE) as SensorManager
        s.registerListener(
            this,
            s.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            300000
        )

    //timer to check if person use phone or not
        timer=  object: CountDownTimer(8000, 500) {
            override fun onTick(millisUntilFinished: Long)
            {
              if(millisUntilFinished<7200) {
                  chexkforpersonvalue()
                  Log.i("clock", "$millisUntilFinished")
              }
            }

            override fun onFinish()
            {
                confirmfall()
                counter=0
                minv=false
                maxv=false
                status=false
                timerstartornot=false

            }
        }


    fallstatus=findViewById(R.id.button)
        fallstatus.setOnClickListener{
            var i=Intent(this,fall_service::class.java)
            startService(i)
         status=false
         minv=false
         maxv=false
         counter=0
            textView3.text="All oky"
            textView4.text="Pre"
            textView2.text="Fall Result:"

        }
        button2.setOnClickListener {

            var int:Intent= Intent(this,jerk_service::class.java)
            startActivity(int)

        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    @SuppressLint("SetTextI18n")
    override fun onSensorChanged(event: SensorEvent?)
    {

        x=event!!.values[0].toDouble()
        y=event!!.values[1].toDouble()
        z=event!!.values[2].toDouble()

        var sqroot= sqrt(x.pow(2) + y.pow(2)+z.pow(2))
        var G=sqroot/SensorManager.GRAVITY_EARTH
          Log.i("sqvalue","sq=$sqroot")
        val precision = DecimalFormat("0.00")
        AccRound = precision.format(sqroot).toDouble()


        textView.setText("x= ${event!!.values[0]}\n\n"+"y= ${event!!.values[1]}\n\n\n"+
                "z= ${event!!.values[2]}"+"\n\n\n acceleration=$sqroot")


        if (sqroot<3 && !minv && !status)
        {
            minv=true
            lasttime=System.currentTimeMillis()
            Log.i("min f","free fall to ground $sqroot" )
            Toast.makeText(this,"free fall to ground $sqroot",Toast.LENGTH_SHORT).show()

            Log.i("min","x=$x\n" +
                    "y=$y\n" +
                    "z=$z \n" +
                    "\n" +
                    "acceleration=$sqroot \n" +
                    "\n")
            textView4.append("--"+AccRound.toString())

        }
        if (minv && !timerstartornot)
        {

            counter++
            textView4.append("--"+AccRound.toString())


            if (sqroot>=120 && !maxv && !status)
            {
                newtime = System.currentTimeMillis()
                diff = newtime - lasttime
                Log.i("min","x=$x\n" +
                        "y=$y\n" +
                        "z=$z \n" +
                        "\n" +
                        "acceleration=$sqroot \n time=$diff" +
                        "\n")

                    if (diff in 100..6000)
                    {
                        maxv = true

                        Toast.makeText(this, "hardfall $sqroot", Toast.LENGTH_SHORT).show()
                        status = true
                        timerstartornot=true
                        timer?.start()

                    }

            }
            if (sqroot>=13 && !maxv && !status)
            {
                newtime = System.currentTimeMillis()
                diff = newtime - lasttime
                Log.i("min","x=$x\n" +
                        "y=$y\n" +
                        "z=$z \n" +
                        "\n" +
                        "acceleration=$sqroot \n time=$diff" +
                        "\n")

                    if (diff in 100..6000)
                    {
                        maxv = true
                        Toast.makeText(this, "softfall$sqroot", Toast.LENGTH_SHORT).show()
                        status = true
                        timerstartornot=true
                        timer?.start()

                    }
            }
        }
        if (counter>=10)
        {
            counter=0
            minv=false
            maxv=false
            Toast.makeText(this,"cancel the fall to groud beacuse not hit groud detect",Toast.LENGTH_LONG).show()

        }
    }


    fun confirmfall()
    {
        if(maxv && minv && status)
        {

            textView3.text="Detected"
            Toast.makeText(this,"fall detected ",Toast.LENGTH_SHORT).show()
            textView2.text="fall result : \n\nx=$x\ny=$y\nz=$z \n\nacceleration= \n\n" +
                    "time difference:$diff milisec"
            minv=false
            maxv=false

        }
    }
    fun chexkforpersonvalue()
    {
        var sqroot= sqrt(x.pow(2) + y.pow(2)+z.pow(2))
        val precision = DecimalFormat("0.00")
        AccRound = precision.format(sqroot).toDouble()
        Log.i("clock","acc=$sqroot")
        if(AccRound in 8.00..11.00)
        {
         personmovement=false

        }
        if( AccRound<8 || AccRound >11)
        {

            timer?.cancel()
            counter=0
            minv=false
            maxv=false
            status=false
            timerstartornot=false
            Log.i("clock","timer cancel")
            Toast.makeText(this@MainActivity,"Person check the phone",Toast.LENGTH_SHORT).show()


        }

    }

}
