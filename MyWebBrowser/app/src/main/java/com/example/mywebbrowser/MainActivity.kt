package com.example.mywebbrowser

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.browse
import org.jetbrains.anko.email
import org.jetbrains.anko.sendSMS
import org.jetbrains.anko.share

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView.apply {
            settings.javaScriptEnabled = true // 자바스크립트 작동하게 하기
            webViewClient = WebViewClient() // 웹뷰에 표시되게 하는 것. 안하면 자체 웹 브라우져가 실행됨
        }
        webView.loadUrl("http://www.google.com") // loadUrl 메서드를 이용하여 웹뷰에 페이지가 로딩됨

        urlEditText.setOnEditorActionListener { _, actionId, _ ->
            // 에디트 텍스트가 선택되고 글자가 입력될 때 마다 호출
            if (actionId == EditorInfo.IME_ACTION_SEARCH) { // 검색버튼에 해당하는 것만 골라냄
                webView.loadUrl(urlEditText.text.toString())
                true
            } else {
                false
            }
        }

        /* 컨텍스트 메뉴 등록 */
        registerForContextMenu(webView)
    }

    /* 뒤로 가기 */
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean { // 바의 옵션
        menuInflater.inflate(R.menu.main, menu)
        return true // true를 반환하면 액티비티에 메뉴가 있다고 인식한다.
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_google, R.id.action_home -> {
                webView.loadUrl("http://www.google.com")
                return true
            }
            R.id.action_naver, R.id.action_home -> {
                webView.loadUrl("http://www.naver.com")
                return true
            }
            R.id.action_daum, R.id.action_home -> {
                webView.loadUrl("http://www.daum.net")
                return true
            }
            R.id.action_call -> {
                /* 암시적 인텐트 */
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:010-6250-8207") // Uri.parse로 감싼 uriString을 데이터로 설정함
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                } // 전화가 안되는 태블릿 같은거는 안되게하는 것, null 이 반환 되면 수행할 액티비티가 없다는 뜻이다.
                return true
            }
            R.id.action_send_text -> {
                sendSMS("010-6250-8207", webView.url) // Anko를 이용한 암시적 인텐트
                return true
            }
            R.id.action_email -> {
                email("sbe03005@naver.com", "진짜 개발자", webView.url) // 이하 동문
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) { // 문맥 옵션
        super.onCreateContextMenu(menu, v, menuInfo)

        menuInflater.inflate(R.menu.context, menu)
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId){
            R.id.action_share ->{
                share(webView.url)
                //return true
            }
            R.id.action_browser->{
                browse(webView.url)
                //return true
            }
        }

        return super.onContextItemSelected(item)
    }
}
