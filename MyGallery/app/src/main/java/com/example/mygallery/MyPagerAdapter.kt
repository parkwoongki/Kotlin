package com.example.mygallery

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

// 프래그먼트를 어느 화면에 표시할 것인지 관리하는 객체 (이하 클래스)

class MyPagerAdapter(fm: FragmentManager?) : FragmentStatePagerAdapter(fm) {
    // 뷰페이저가 표시할 프래그먼트 목록
    private val items = arrayListOf<Fragment>()

    // position 위치의 프래그먼트
    override fun getItem(p0: Int): Fragment {
        return items[p0]
    }

    // 아이템 개수
    override fun getCount(): Int {
        return items.size
    }

    // 아이템 갱신
    fun updateFragments(items : List<Fragment>){
        this.items.addAll(items)
    }
}