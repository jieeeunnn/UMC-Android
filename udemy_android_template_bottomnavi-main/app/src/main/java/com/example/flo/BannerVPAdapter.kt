package com.example.flo

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class BannerVPAdapter(fragment: Fragment) : FragmentStateAdapter(fragment){

    private val fragmentList : ArrayList<Fragment> = ArrayList() // BannerVPAdapter 안에서만 사용해주기 위해서 private로 설정

    // 이 클래스에서 연결된 viewPager에게 데이터를 전달할 때 데이터를 몇개를 전달할지 알려주는 함수
    // 여기서 전달할 데이터는 fragmentList에 담긴 리스트의 갯수
    override fun getItemCount(): Int {
        return fragmentList.size
    }

    //override fun getItemCount(): Int = fragmentList.size
    // 위 코드처럼도 표현 가능

    // 프래그먼트 리스트 안에 있는 아이템들 (즉, 프래그먼트)을 생성해주는 함수
    override fun createFragment(position: Int): Fragment = fragmentList[position]

    fun addFragment(fragment: Fragment) {
        fragmentList.add(fragment)
        notifyItemInserted(fragmentList.size - 1) // 새로운 프래그먼트가 추가됐을 때 viewPager에게 새로운 프래그먼트가 추가됐음을 알려줌 (새로운 값이 추가되었으니 이것도 추가해서 보여주어야 한다는 뜻)
    }
}