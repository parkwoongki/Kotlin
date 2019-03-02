package com.example.tiltsensor

import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.WindowManager

class MainActivity : AppCompatActivity(), SensorEventListener { // 빨간줄 뜨는 건 임플리먼트 빨간전구 해서 Ctrl + a 다 추가

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) { // 센서 정밀도가 변경되면 호출

    }

    override fun onSensorChanged(event: SensorEvent?) { // 센서 값이 변경되면 호출
        // 센서 값이 변경되면 호출되는 곳인데
        // value[0] : x축 값 : 위로 기울이면 -10~0, 아래로 기울이면 0~10,
        // value[1] : y축 값 : 왼쪽으로 기울이면 -10~0, 오른쪽으로 기울이면 0~10,
        // value[2] : z축 값 : 미사용
        event?.let {
            Log.d(
                "MainActivity", "onSensorChanged: x :" +
                        "x:${event.values[0]}, y:${event.values[1]}, z : ${event.values[2]}"
            )
            tiltView.onSensorEvent(event)
        }
    }

    private val sensorManager by lazy {
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    private lateinit var tiltView: TiltView // 늦은 초기화 선언

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        /* 화면 안꺼지게 */
        window.addFlags(WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON)
        /* 화면이 가로 모드로 고정되게 하기 */
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        tiltView = TiltView(this) // 선언 초기화
        //setContentView(R.layout.activity_main) 대신에,
        setContentView(tiltView) // 이걸로 전체 레이아웃 변경!
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(
            this, // 센서 등록
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), // 가속도 센서 지정
            SensorManager.SENSOR_DELAY_NORMAL
        ) // 얼마나 자주 받을지 정하는 것
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this) // 센서 사용 해제
    }
}
