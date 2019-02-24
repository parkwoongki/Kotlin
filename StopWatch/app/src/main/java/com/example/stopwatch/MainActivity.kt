package com.example.stopwatch

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {

    private var time = 0
    private var isRunning = false
    private var timerTask: Timer? = null //바로 시작할수는 없으니까 먼저 null을 넣어주고 시작 하면 그때 타이머에 시작시킴
    private var lap = 1

    private fun start() {
        fab.setImageResource(R.drawable.ic_pause_black_24dp)

        timerTask = timer(period = 10) {
            // 시작하는 구간
            time++ // 10주기 마다 즉 0.01초 마다 플러스 1해주는 것 임

            val sec = time / 100 // 1일때는 1/100 이니까 0.01초
            val milli = time % 100 // 1나누기 100은 그 숫자 그대로임 100전까지

            runOnUiThread {
                // ui에 쓸때
                secTextView.text = "$sec"
                milliTextView.text = "$milli"
            }
        }
    }

    private fun pause() {
        fab.setImageResource(R.drawable.ic_play_arrow_black_24dp) // 이미지 다시 바꿔주기
        timerTask?.cancel() // 타이머 킬
    }

    private fun recordLapTime() {
        val lapTime = this.time // 현재시간을 지역변수에 저장
        val textView = TextView(this) // 동적으로 TextView 생성
        textView.text = "$lap LAB : ${lapTime / 100}.${lapTime % 100}" // 문자 넣고,

        // 맨 위에 랩 타임 추가 //
        lapLayout.addView(textView, 0) // 그것을 lapLayout에 추가!
        lap++ // 1Lap 추가
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener {
            isRunning = !isRunning

            if (isRunning) {
                start()
            } else {
                pause()
            }
        }

        lapButton.setOnClickListener {
            recordLapTime()
        }

        resetFab.setOnClickListener {
            reset()
        }
    }

    private fun reset() {
        timerTask?.cancel()

        // 모든 변수 초기화
        time = 0
        isRunning = false
        fab.setImageResource(R.drawable.ic_play_arrow_black_24dp)
        secTextView.text = "0"
        milliTextView.text = "00"

        // 랩 타입 모두 제거
        lapLayout.removeAllViews()
        lap = 1
    }
}
