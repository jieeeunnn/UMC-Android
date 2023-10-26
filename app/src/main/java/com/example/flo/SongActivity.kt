package com.example.flo

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.databinding.ActivitySongBinding
import com.google.gson.Gson

class SongActivity : AppCompatActivity() {

    lateinit var binding: ActivitySongBinding
    lateinit var song: Song
    lateinit var timer: Timer
    private var mediaPlayer: MediaPlayer ?= null // 액티비티가 소멸될 때 mediaPlayer를 해제 시켜줘야 하기 때문에 nullable로 설정
    private var gson: Gson = Gson()

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

    // 사용자가 포커스를 잃었을 때 음악이 중지
    override fun onPause() {
        super.onPause()
        setPlayerStatus(false)
        song.second = ((binding.songProgressSb.progress * song.playTime) / 100) / 1000 // songActivity에서는 ms로 계산되고 있는데 song에서는 초 단위로 계산되고 있기 때문에 1000으로 나눠줌
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE) // song 데이터를 내부 저장소에 저장하기 위한 sharedPreferences
        val editor = sharedPreferences.edit() // 에디터
        val songJson = gson.toJson(song) // song 객체를 Json으로 변환
        editor.putString("song", songJson)

        editor.apply() // apply()를 해주어야 실제로 저장까지 완료됨
    }

    override fun onDestroy() { // 앱이 꺼질 때 자동으로 호출되는 함수인 onDestroy()를 통해 timer.interrupt() 호출
        super.onDestroy()
        timer.interrupt()
        mediaPlayer?.release() // 불필요한 리소스 방지를 위해 mediaPlayer가 갖고 있던 리소스 해제
        mediaPlayer = null // mediaPlayer 해제
    }

    private fun initSong() {
        if (intent.hasExtra("title") && intent.hasExtra("singer")) {
            song = Song(
                intent.getStringExtra("title")!!,
                intent.getStringExtra("title")!!,
                intent.getIntExtra("second", 0),
                intent.getIntExtra("playTime", 0),
                intent.getBooleanExtra("isPlaying", false),
                intent.getStringExtra("music")!!
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

        val music = resources.getIdentifier(song.music,"raw", this.packageName) // 재생할 음악 리소스
        mediaPlayer = MediaPlayer.create(this, music) // 재생할 음악 리소스를 MediaPlayer에 전달

        setPlayerStatus(song.isPlaying)
    }
    fun setPlayerStatus(isPlaying: Boolean) { // 버튼에 따라 음악을 재생/정지
        song.isPlaying = isPlaying
        timer.isPlaying = isPlaying

        if (isPlaying) {
            binding.songMiniplayerIv.visibility = View.GONE
            binding.songPauseIv.visibility = View.VISIBLE
            mediaPlayer?.start()
        }
        else {
            binding.songMiniplayerIv.visibility = View.VISIBLE
            binding.songPauseIv.visibility = View.GONE
            if (mediaPlayer?.isPlaying == true) { // mediaPlayer가 재생중이 아닐 때 pause를 하면 오류가 생길 수 있으므로 if문 추가
                mediaPlayer?.pause()
            }
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