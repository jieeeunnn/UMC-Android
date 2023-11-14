package com.example.flo

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "SongTable", foreignKeys = [
    ForeignKey(
    entity = Album::class,
    parentColumns = ["id"],
    childColumns = ["albumIdx"],
    )]
)
data class Song(
    var title: String = "",
    var singer: String = "",
    var second: Int = 0,// 노래가 얼마만큼 재생되었는지
    var playTime: Int = 0,// 총 재생 시간
    var isPlaying: Boolean = false,// 노래가 재생 중인지
    var music: String = "",// 어떤 음악이 재생 중인지
    var coverImg: Int? = null,
    var isLike: Boolean = false,
    var albumIdx : Int = 0
){
    @PrimaryKey(autoGenerate = true) var id : Int = 0
}