package com.example.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.flo.databinding.FragmentHomePannelBannerBinding

class HomePannelBannerFragment(val imgRes : Int): Fragment() {
    lateinit var binding: FragmentHomePannelBannerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomePannelBannerBinding.inflate(inflater, container, false)
        binding.homePannelImageIv.setImageResource(imgRes)

        return binding.root
    }
}