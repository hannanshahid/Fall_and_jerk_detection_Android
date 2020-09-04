package com.example.falldetctionmodule_fyp

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_jerk.*
import java.lang.Math.abs


class jerk_service : AppCompatActivity() , SensorEventListener {
    lateinit var s: SensorManager;
    lateinit var accelrometer:Sensor
    var x:Double=0.0
    var y:Double=0.0
    var z:Double=0.0
    var mAccel:Float=0F
    var mAccelLast:Float=0F
    var mAccelCurrent:Float=0F


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jerk)
        s= getSystemService(Context.SENSOR_SERVICE) as SensorManager
        s.registerListener(
            this,
            s.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),

            SensorManager.SENSOR_DELAY_NORMAL


        )
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?)
    {





        x=event!!.values[0].toDouble()
        y=event!!.values[1].toDouble()
        z=event!!.values[2].toDouble()

        mAccelLast = mAccelCurrent
        mAccelCurrent = Math.sqrt(x * x + y * y + z * z).toFloat()
        var delta = mAccelCurrent - mAccelLast;
         mAccel = mAccel * 0.9f + delta;
        mAccel= abs(mAccel)
        Log.i("accjerk" ,"acc=$mAccel")
        textView5.setText("a:"+mAccel);

        if (mAccel > 80) {

            Toast.makeText(this,"Sahke detected $mAccel",Toast.LENGTH_LONG).show()
        }
    }
}