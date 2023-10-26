package com.example.flo

data class Song(
    val title : String = "",
    val singer : String = "",
    var second : Int = 0, // 노래가 얼마만큼 재생되었는지
    var playTime : Int = 0, // 총 재생 시간
    var isPlaying : Boolean = false, // 노래가 재생 중인지
    var music: String = "" // 어떤 음악이 재생 중인지
)
