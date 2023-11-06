package com.example.flo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.flo.databinding.FragmentHomeBinding
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.scheduleAtFixedRate

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    private val timer = Timer()
    private val handler = Handler(Looper.getMainLooper())
    private var albumDatas = ArrayList<Album>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

//        binding.homeTodayMusicAlbumIv.setOnClickListener {
//            (context as MainActivity).supportFragmentManager.beginTransaction().replace(R.id.main_frm,AlbumFragment()).commitAllowingStateLoss()
//        }

        // 데이터 리스트 생성 더미 데이터
        albumDatas.apply {
            add(Album("Butter", "방탄소년단 (BTS)", R.drawable.img_album_exp))
            add(Album("Lilac", "아이유 (IU)", R.drawable.img_album_exp2))
            add(Album("Next Level", "에스파 (AESPA)", R.drawable.img_album_exp3))
            add(Album("Boy with Luv", "방탄소년단 (BTS)", R.drawable.img_album_exp4))
            add(Album("BBoom BBoom", "모모랜드 (MOMOLAND)", R.drawable.img_album_exp5))
            add(Album("Weekend", "태연 (Tae Yeon)", R.drawable.img_album_exp6))
        }

        val bannerAdapter = BannerVPAdapter(this) // Adapter를 이용해 데이터를 가져옴
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp))
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp2))
        binding.homeBannerVp.adapter = bannerAdapter// viewPager와 Adapter 연결
        binding.homeBannerVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL // ViewPager가 좌우로 스크롤 될 수 있도록 설정

        val panelAdapter = BannerVPAdapter(this)
        panelAdapter.addFragment(HomePanelBannerFragment(R.drawable.img_first_album_default))
        panelAdapter.addFragment(HomePanelBannerFragment(R.drawable.img_first_album_default))
        panelAdapter.addFragment(HomePanelBannerFragment(R.drawable.img_first_album_default))
        panelAdapter.addFragment(HomePanelBannerFragment(R.drawable.img_first_album_default))
        binding.homePanelBackgroundVp.adapter = panelAdapter
        binding.homePanelBackgroundVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        binding.homePanelIndicator.setViewPager(binding.homePanelBackgroundVp)
        startAutoSlide(panelAdapter)

        return binding.root
    }

    private fun startAutoSlide(adpater : BannerVPAdapter) {
        // 일정 간격으로 슬라이드 변경 (3초마다)
        timer.scheduleAtFixedRate(3000, 3000) { // 초기 지연 시간 3초 후에 작업 시작, 그 이후엔 매 3초마다 작업 반복 실행
            handler.post {
                val nextItem = binding.homePanelBackgroundVp.currentItem + 1 // ViewPager의 현재 페이지를 가져와 1을 더함
                if (nextItem < adpater.itemCount) {
                    binding.homePanelBackgroundVp.currentItem = nextItem
                } else {
                    binding.homePanelBackgroundVp.currentItem = 0 // 마지막 페이지에서 첫 페이지로 순환
                }
            }
        }
    }
}