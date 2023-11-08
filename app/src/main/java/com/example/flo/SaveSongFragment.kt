package com.example.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flo.databinding.FragmentLockerSavedsongBinding

class SaveSongFragment: Fragment(){
    lateinit var binding: FragmentLockerSavedsongBinding
    private var songDatas = ArrayList<SaveSong>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLockerSavedsongBinding.inflate(inflater, container, false)

        songDatas.apply {
            add(SaveSong("Butter", "방탄소년단 (BTS)", R.drawable.img_album_exp))
            add(SaveSong("Lilac", "아이유 (IU)", R.drawable.img_album_exp2))
            add(SaveSong("Next Level", "에스파 (AESPA)", R.drawable.img_album_exp3))
            add(SaveSong("Boy with Luv", "방탄소년단 (BTS)", R.drawable.img_album_exp4))
            add(SaveSong("BBoom BBoom", "모모랜드 (MOMOLAND)", R.drawable.img_album_exp5))
            add(SaveSong("Weekend", "태연 (Tae Yeon)", R.drawable.img_album_exp6))
            add(SaveSong("Butter", "방탄소년단 (BTS)", R.drawable.img_album_exp))
            add(SaveSong("Lilac", "아이유 (IU)", R.drawable.img_album_exp2))
            add(SaveSong("Next Level", "에스파 (AESPA)", R.drawable.img_album_exp3))
            add(SaveSong("Boy with Luv", "방탄소년단 (BTS)", R.drawable.img_album_exp4))
            add(SaveSong("BBoom BBoom", "모모랜드 (MOMOLAND)", R.drawable.img_album_exp5))
            add(SaveSong("Weekend", "태연 (Tae Yeon)", R.drawable.img_album_exp6))
        }

        val saveSongRVAdapter = SongRVAdapter(songDatas)
        binding.lockerSavedSongRecyclerView.adapter = saveSongRVAdapter
        //binding.lockerSavedSongRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        saveSongRVAdapter.setMySongClickListener(object : SongRVAdapter.MySongClickListener {
            override fun onRemoveSong(position: Int) {
                saveSongRVAdapter.removeSong(position)
            }
        })

        return binding.root
    }
}