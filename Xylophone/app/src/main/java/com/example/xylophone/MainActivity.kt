package com.example.xylophone

import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private val soundPool = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        SoundPool.Builder().setMaxStreams(8).build()
    } else {
        SoundPool(8, AudioManager.STREAM_MUSIC, 0)
    } // 21 이상부터 지원됨 빨간전구 클릭 (이 프로젝트는 19임) 롤리팝 이전과 이후로 사용이 가능하게 만듬 Surround 이거
    // 안드로이드 5.0 미만에서의 사용법 private val soundPool = SoundPool(8, AudioManager.STREAM_MUSIC,0) 여기서 세번째 인자는 음질을 의미

    override fun onCreate(savedInstanceState: Bundle?) {
        // 가로 모드 고정
        // 외에도 xml에서 android:screenOrientation = "landscape" 이렇게 해도 똑같은 효과를 가져옴. (<activity>내부에 해야됨!)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // sounds 리스트를 forEach 함수를 사용하여 요소 하나씩 꺼내서 tune 메서드에 전달한다.
        sounds.forEach { tune(it) }
    }

    // listOf 함수를 사용하여 텍스트 뷰의 ID 와 음원 파일의 리소스 ID를 연관 지은 Pair 객체 8개를 리스트 객체 sounds로 만듬
    // Pair 클래스는 두개의 연관된 객체를 저장한다.
    private val sounds = listOf(
        Pair(R.id.do1, R.raw.do1),
        Pair(R.id.re, R.raw.re),
        Pair(R.id.mi, R.raw.mi),
        Pair(R.id.fa, R.raw.fa),
        Pair(R.id.sol, R.raw.sol),
        Pair(R.id.la, R.raw.la),
        Pair(R.id.si, R.raw.si),
        Pair(R.id.do2, R.raw.do2)
    )

    // 텍스트 뷰를 클릭했을 때
    private fun tune(pitch: Pair<Int, Int>) { // tune 메서드는 Pair 객체를 받아서
        val soundId = soundPool.load(this, pitch.second, 1) // load() 메서드로 음원의 ID를 얻고
        findViewById<TextView>(pitch.first).setOnClickListener { // findViewById 메서드로 텍스트뷰의 ID에 해당하는 뷰를 얻고
            soundPool.play(soundId, 1.0f, 1.0f, 0, 0, 1.0f) // 텍스트 뷰 클릭했을 때 음원을 재생한다.
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }
}
