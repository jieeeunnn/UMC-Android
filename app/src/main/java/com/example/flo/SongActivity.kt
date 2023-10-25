package com.example.flo

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.databinding.ActivitySongBinding

class SongActivity : AppCompatActivity() {

    lateinit var binding: ActivitySongBinding
    lateinit var song: Song
    lateinit var timer: Timer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initSong()
        setPlayer(song)

        binding.songDownIb.setOnClickListener {
            finish()
        }
        binding.songMiniplayerIv.setOnClickListener {
            setPlayerStatus(true)
        }
        binding.songPauseIv.setOnClickListener {
            setPlayerStatus(false)
        }
    }

    override fun onDestroy() { // 앱이 꺼질 때 자동으로 호출되는 함수인 onDestroy()를 통해 timer.interrupt() 호출
        super.onDestroy()
        timer.interrupt()
    }

    private fun initSong() {
        if (intent.hasExtra("title") && intent.hasExtra("singer")) {
            song = Song(
                intent.getStringExtra("title")!!,
                intent.getStringExtra("title")!!,
                intent.getIntExtra("second", 0),
                intent.getIntExtra("playTime", 0),
                intent.getBooleanExtra("isPlaying", false)
            )
        }
        startTimer()
    }

    private fun setPlayer(song: Song) { // 초기화된 song에 대한 데이터를 뷰 렌더링
        binding.songMusicTitleTv.text = intent.getStringExtra("title")!!
        binding.songSingerNameTv.text = intent.getStringExtra("singer")!!
        binding.songStartTimeTv.text = String.format("%02d:%02d", song.second / 60, song.second % 60)
        binding.songEndTimeTv.text = String.format("%02d:%02d", song.playTime / 60, song.playTime / 60)
        binding.songProgressSb.progress = (song.second * 1000 / song.playTime)

        setPlayerStatus(song.isPlaying)
    }
    fun setPlayerStatus(isPlaying: Boolean) {
        song.isPlaying = isPlaying
        timer.isPlaying = isPlaying

        if (isPlaying) {
            binding.songMiniplayerIv.visibility = View.GONE
            binding.songPauseIv.visibility = View.VISIBLE
        }
        else {
            binding.songMiniplayerIv.visibility = View.VISIBLE
            binding.songPauseIv.visibility = View.GONE
        }
    }

    private fun startTimer() {
        timer = Timer(song.playTime, song.isPlaying)
        timer.start()
    }

    inner class Timer(private val playTime: Int, var isPlaying : Boolean = true) : Thread(){
        private var second : Int = 0
        private var mills : Float = 0f

        override fun run() {
            super.run()

            try {
                while (true) { // 타이머는 계속 진행되어야 하므로 while (true)를 통해 계속 반복
                    if (second >= playTime) { // 노래 시간이 끝나면 반복문 종료
                        break
                    }

                    if (isPlaying) { // 50ms 단위로 진행되는 시간 관리
                        // isPlaying으로 thread를 멈춤
                        sleep(50)
                        mills += 50

                        runOnUiThread { // seekBar의 progress값을 증가시켜야 함. 뷰를 렌더링 해주는 작업이므로 runOnUiThread 함수 사용 (혹은 handler도 사용 가능)
                            binding.songProgressSb.progress = ((mills / playTime) * 100).toInt()
                        }

                        // 진행 시간 타이머
                        if (mills % 1000 == 0f) { // mills가 1000이 되어야 1초가 지나므로 1000이 될 때마다 second에 1씩 더해줌
                            runOnUiThread { // 뷰 렌더링
                                binding.songStartTimeTv.text = String.format("%02d:%02d", second / 60, second % 60)
                            }
                            second ++
                        }

                    }
                }
            } catch (e: InterruptedException) { // 액티비티가 멈췄을 때 오류를 내서 thread 종료 (try-catch문을 이용해서 오류가 나도 앱을 종료시키는 것이 아니라 catch문 내에 있는 코드를 실행함)
                Log.d("Song", "쓰레드가 죽었습니다. ${e.message}")
            }

        }
    }
}