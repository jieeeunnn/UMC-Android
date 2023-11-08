package com.example.flo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flo.databinding.ItemSongBinding

class SongRVAdapter(private val songList: ArrayList<SaveSong>): RecyclerView.Adapter<SongRVAdapter.ViewHolder>() {

    interface MySongClickListener {
        fun onRemoveSong(position: Int)
    }

    private lateinit var mSongClickListener : MySongClickListener

    fun setMySongClickListener(itemClickListener: MySongClickListener) {
        mSongClickListener = itemClickListener
    }

    fun removeSong(position: Int) {
        songList.removeAt(position)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): SongRVAdapter.ViewHolder {
        val binding : ItemSongBinding = ItemSongBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SongRVAdapter.ViewHolder, position: Int) {
        holder.bind(songList[position])
        holder.binding.itemSongMoreIv.setOnClickListener {  // 더보기 버튼 클릭 시 아이템 삭제
            mSongClickListener.onRemoveSong(position)
        }
    }

    override fun getItemCount(): Int = songList.size

    inner class ViewHolder(val binding: ItemSongBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(song: SaveSong) {
            binding.itemSongTitleTv.text = song.title
            binding.itemSongSingerTv.text = song.singer
            binding.itemSongImgIv.setImageResource(song.coverImg!!)
        }
    }
}