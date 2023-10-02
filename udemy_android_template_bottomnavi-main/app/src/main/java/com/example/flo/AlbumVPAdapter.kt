package com.example.flo

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class AlbumVPAdapter(fragment: Fragment) : FragmentStateAdapter(fragment){
    override fun getItemCount(): Int = 3 // 수록곡, 상세 정보, 영상 부분을 viewPager로 구성할 것이기 떄문에 (= 3개의 프래그먼트) 3으로 설정

    override fun createFragment(position: Int): Fragment {
        return when(position) { // 수록곡, 상세 정보, 영상 탭을 눌렀을 때 position에 따라 다른 프래그먼트를 보여줌
            0 -> SongFragment() // (수록곡 프래그먼트)
            1 -> DetailFragment() // (상세 정보 프래그먼트)
            else -> VideoFragment() // (영상 프래그먼트)
        }

    }

}