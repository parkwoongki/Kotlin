package com.example.mygallery

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {

    private val REQUEST_READ_EXTERNAL_STORAGE = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 권한이 부여되었는지 확인
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) { // 인정안될때 진입
            // 권한이 허용되지 않음
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                // 이전에 이미 권한 거부했을 때 나올 설명!
                alert("사전 정보를 얻으려면 외부 저장소 권한이 필수로 필요합니다.", "권한이 필요한 이유") {
                    yesButton {
                        ActivityCompat.requestPermissions(
                            this@MainActivity,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            REQUEST_READ_EXTERNAL_STORAGE
                        )
                        noButton { }
                    }
                }.show()
                // 여기까지 Alert
            } else {
                // 이전에 권한 요청을 거부하지 않았었다면 바로 권한 요청을 실시한다!
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_READ_EXTERNAL_STORAGE
                )
            }
        } else {
            // 권한이 이미 허용되었을 때 바로 보여주면 되겠죠?
            getAllPhoto()
        }
    }

    private fun getAllPhoto() {
        // 모든 사진 정보 가져오기
        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC"
        ) // 찍은 날짜 내림차순

        // 프래그먼트를 아이템으로 하는 ArrayList를 생성, 사진을 Cursor 객체로부터 가져올 때마다 PhotoFrament.newInstance(uri)
        // 로 프래그먼트를 생성하면서 fragments리스트에 추가합니다.
        val fragments = ArrayList<Fragment>()

        // 이동 잘되는 확인하는 로그
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val uri =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)) // 사진 경로가 저장된 데이터베이스의 컬럼명은 DATA상수에 저장되어있음
                // getString 메서드에 그 인덱스를 전달하면 해당하는 데이터를 String 타입으로 반환함
                Log.d("MainActivity", uri)
                fragments.add(PhotoFragment.newInstance(uri)) // 프래그먼트를 생성하면서 fragment리스트에 추가함
            }
            cursor.close() // 제거해야 메모리 누수를 막음
        }

        // 어댑터
        /* MyPagerAdapter를 생성하면서 프래그먼트 매니저를 생성자의 인자로 전달해야 한다.
        * */
        val adapter = MyPagerAdapter(supportFragmentManager)
        adapter.updateFragments(fragments)
        viewPager.adapter = adapter

        // 3초마다 자동 슬라이드
        timer(period = 3000) { // 3초 마다 실행
            runOnUiThread({ // 백그라운드 스레드 움직이게 동작
                if (viewPager.currentItem < adapter.count - 1) { // 현재페이지가 마지막 페이지가 아니라면
                    viewPager.currentItem = viewPager.currentItem + 1 // 다음 페이지로 변경
                } else {
                    viewPager.currentItem = 0 // 마지막 페이지이면 첫 페이지로 이동시킴
                }
            })
        }
    }

    // 권한 요청 응답에 대한 메서드. 권한이 부여되었는지 확인하려면 이 메서드를 오버라이드 해야함. 응답 결과에 따라 사진 또는 거부메시지의 토스트를 띄워준다.
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_READ_EXTERNAL_STORAGE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) { // 하나의 권한만 요청했기 때문에 [0]번 인덱스 값만 확인.
                    // 권한 허용됨
                    getAllPhoto()
                } else {
                    // 권한 거부
                    toast("권한 거부 됨")
                }
                return
            }
        }
    }
}
