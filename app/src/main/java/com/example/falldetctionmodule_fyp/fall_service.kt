package com.example.falldetctionmodule_fyp

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.text.DecimalFormat
import kotlin.math.log
import kotlin.math.pow
import kotlin.math.sqrt

class fall_service : Service() , SensorEventListener {
    lateinit var s: SensorManager;
    var counter:Int=0
    var x:Double=0.0
    var y:Double=0.0
    var z:Double=0.0


    var lasttime:Long=0
    var newtime:Long=0
    var diff:Long=0
    var AccRound=0.0
    var status:Boolean=false
    var minv:Boolean=false
    var maxv:Boolean=false
    override fun onBind(intent: Intent): IBinder? {
      return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int
    {
        s= getSystemService(Context.SENSOR_SERVICE) as SensorManager
        s.registerListener(
            this,
            s.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),

            SensorManager.SENSOR_DELAY_NORMAL
        )
        Log.i("th","service start ${Thread.currentThread().id}")
        return super.onStartCommand(intent, flags, startId)
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int)
    {

    }

    override fun onDestroy() {
        Log.i("service","close")
        super.onDestroy()
    }
    override fun onSensorChanged(event: SensorEvent?)
    {
        x=event!!.values[0].toDouble()
        y=event!!.values[1].toDouble()
        z=event!!.values[2].toDouble()

        var sqroot= sqrt(x.pow(2) + y.pow(2)+z.pow(2))
        var G=sqroot/SensorManager.GRAVITY_EARTH
        Log.i("gvalue","G=$G")
        val precision = DecimalFormat("0.00")
        AccRound = precision.format(sqroot).toDouble()





        if (sqroot<3 && !minv && !status)
        {
            minv=true
            lasttime=System.currentTimeMillis()
            Log.i("min f","free fall to ground $sqroot" )
            Toast.makeText(this,"free fall to ground $sqroot", Toast.LENGTH_SHORT).show()

            Log.i("min","x=$x\n" +
                    "y=$y\n" +
                    "z=$z \n" +
                    "\n" +
                    "acceleration=$sqroot \n" +
                    "\n")

        }
        if (minv)
        {

            counter++



            if (sqroot>=120 && !maxv && !status)
            {
                Log.i("min","x=$x\n" +
                        "y=$y\n" +
                        "z=$z \n" +
                        "\n" +
                        "acceleration=$sqroot \n" +
                        "\n")

                newtime = System.currentTimeMillis()
                diff = newtime - lasttime
                Toast.makeText(this,"last:$lasttime && new:$newtime \n diff:$diff\n\n $sqroot",
                    Toast.LENGTH_LONG).show()

                if (diff in 150..1500) {
                    maxv = true
                    Log.i("min f", "hiting to ground $sqroot")
                    Toast.makeText(this, "hardfall $sqroot", Toast.LENGTH_SHORT).show()
                    status = true
                }

            }
            if (sqroot>=50 && !maxv && !status)
            {
                Log.i("min","x=$x\n" +
                        "y=$y\n" +
                        "z=$z \n" +
                        "\n" +
                        "acceleration=$sqroot \n" +
                        "\n")

                newtime = System.currentTimeMillis()
                diff = newtime - lasttime
                Toast.makeText(this,"last:$lasttime && new:$newtime \n diff:$diff\n\n $sqroot",
                    Toast.LENGTH_LONG).show()

                if (diff in 150..1500) {
                    maxv = true
                    Log.i("min f", "hiting to ground $sqroot")
                    Toast.makeText(this, "softfall$sqroot", Toast.LENGTH_SHORT).show()
                    status = true
                }
            }




        }
        if(maxv && minv && status)
        {


            Log.i("min","fall detected $sqroot")
            Toast.makeText(this,"fall detected $sqroot", Toast.LENGTH_SHORT).show()

            minv=false
            maxv=false
            stopSelf()
            var i=Intent(this,MainActivity::class.java)
            startActivity(i)
        }
        if (counter>=10)
        {
            counter=0
            minv=false
            maxv=false
            Toast.makeText(this,"cancel the fall to groud beacuse not hit groud detect", Toast.LENGTH_LONG).show()

        }
    }
}
