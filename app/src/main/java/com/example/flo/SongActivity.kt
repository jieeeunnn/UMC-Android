package com.example.flo

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.flo.databinding.ActivitySongBinding
import com.google.gson.Gson

class SongActivity : AppCompatActivity() {

    lateinit var binding: ActivitySongBinding
    lateinit var timer: Timer
    private var mediaPlayer: MediaPlayer ?= null // 액티비티가 소멸될 때 mediaPlayer를 해제 시켜줘야 하기 때문에 nullable로 설정
    private var gson: Gson = Gson()

    val songs = arrayListOf<Song>()
    lateinit var songDB: SongDatabase
    var nowPos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initPlayList()
        initSong()
        initClickListener()

    }

    // 사용자가 포커스를 잃었을 때 음악이 중지
    override fun onPause() {
        super.onPause()
        setPlayerStatus(false)
        songs[nowPos].second = ((binding.songProgressSb.progress * songs[nowPos].playTime) / 100) / 1000 // songActivity에서는 ms로 계산되고 있는데 song에서는 초 단위로 계산되고 있기 때문에 1000으로 나눠줌
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE) // song 데이터를 내부 저장소에 저장하기 위한 sharedPreferences
        val editor = sharedPreferences.edit() // 에디터

        editor.putInt("songId", songs[nowPos].id)

        editor.apply() // apply()를 해주어야 실제로 저장까지 완료됨
    }

    override fun onDestroy() { // 앱이 꺼질 때 자동으로 호출되는 함수인 onDestroy()를 통해 timer.interrupt() 호출
        super.onDestroy()
        timer.interrupt()
        mediaPlayer?.release() // 불필요한 리소스 방지를 위해 mediaPlayer가 갖고 있던 리소스 해제
        mediaPlayer = null // mediaPlayer 해제
    }

    private fun initPlayList() {
        songDB = SongDatabase.getInstance(this)!!
        songs.addAll(songDB.songDao().getSongs())
    }

    private fun initClickListener(){
        binding.songDownIb.setOnClickListener {
            finish()
        }
        binding.songMiniplayerIv.setOnClickListener {
            setPlayerStatus(true)
        }
        binding.songPauseIv.setOnClickListener {
            setPlayerStatus(false)
        }
        binding.songNextIv.setOnClickListener {
            moveSong(+1)
        }
        binding.songPreviousIv.setOnClickListener {
            moveSong(-1)
        }
    }

    private fun initSong() {
        // SharedPreferences에서 song id를 받아와서 song id를 통해서 songs와 비교해서 인덱스 값 구하기
        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId", 0)

        nowPos = getPlayingSongPosition(songId) // 현재 재생되고 있는 노래의 id

        Log.d("now Song Id", songs[nowPos].id.toString())
        startTimer()
        setPlayer(songs[nowPos])
    }

    private fun moveSong(direct: Int) { // 다음 노래를 눌렀을 땐 nowPos+1, 이전 노래를 눌렀을 때나 nowPos-1
        if (nowPos + direct < 0) {
            Toast.makeText(this, "first song", Toast.LENGTH_SHORT).show()
            return
        }
        if (nowPos + direct >= songs.size) {
            Toast.makeText(this, "last song", Toast.LENGTH_SHORT)
            return
        }

        nowPos += direct

        timer.interrupt() // 새로운 노래를 재생해야하기 때문에 timer를 중지시키고
        startTimer() // 새로운 timer 재생

        setPlayer(songs[nowPos])
    }
    private fun getPlayingSongPosition(songId: Int): Int{
        for (i in 0 until songs.size) {
            if (songs[i].id == songId) { // songs의 id와 songId가 같을 때 아이디값인 i 반환
                return i
            }
        }
        return 0
    }

    private fun setPlayer(song: Song) { // 초기화된 song에 대한 데이터를 뷰 렌더링
        binding.songMusicTitleTv.text = song.title
        binding.songSingerNameTv.text = song.singer
        binding.songStartTimeTv.text = String.format("%02d:%02d", song.second / 60, song.second % 60)
        binding.songEndTimeTv.text = String.format("%02d:%02d", song.playTime / 60, song.playTime / 60)
        binding.songAlbumIv.setImageResource(song.coverImg!!)
        binding.songProgressSb.progress = (song.second * 1000 / song.playTime)

        val music = resources.getIdentifier(song.music,"raw", this.packageName) // 재생할 음악 리소스
        mediaPlayer = MediaPlayer.create(this, music) // 재생할 음악 리소스를 MediaPlayer에 전달

        setPlayerStatus(song.isPlaying)
    }
    fun setPlayerStatus(isPlaying: Boolean) { // 버튼에 따라 음악을 재생/정지
        songs[nowPos].isPlaying = isPlaying
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
        timer = Timer(songs[nowPos].playTime, songs[nowPos].isPlaying)
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