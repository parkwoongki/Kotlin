package com.example.bmicalculator

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {

    private fun saveData(height: Int, weight: Int) {
        val pref = PreferenceManager.getDefaultSharedPreferences(this) // 프리퍼런스 매니저로 프리퍼런스 객체 얻기
        val editor = pref.edit() // 프리퍼런스 객체로 에디터 객체얻기

        // 키 값과 밸류 값을 저장
        editor.putInt("KEY_HEIGHT", height)
            .putInt("KEY_WEIGHT", weight)
            .apply()
    }

    private fun loadData() {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val height = pref.getInt("KEY_HEIGHT", 0)
        val weight = pref.getInt("KEY_WEIGHT", 0)

        // toString()
        // toString은 객체의 이름을 문자열로 리턴해준다.  객체를 생성후 toSting했을때 의미있는 값을 리턴할 수 있다.

        if (height != 0 && weight != 0) {
            heightEditText.setText(height.toString())
            weightEditText.setText(weight.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadData() // 저장했던 것 불러오기

        resultButton.setOnClickListener {
            saveData(
                heightEditText.text.toString().toInt(),
                weightEditText.text.toString().toInt()
            ) // 데이터 저장 로드랑 위치가 다른 이유 : 버튼 눌렀을 때 저장해야되니까

            /*val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra("weight", weightEditText.text.toString())
            intent.putExtra("height", heightEditText.text.toString())
            startActivity(intent)*/
            startActivity<ResultActivity>(
                "weight" to weightEditText.text.toString(),
                "height" to heightEditText.text.toString()
            )
        }
    }
}
