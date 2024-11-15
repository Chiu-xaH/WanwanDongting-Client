package com.chiuxah.wanwandongting.logic.dataModel


data class SearchResponse(val data : SearchData)

data class SearchData(val song : Songs)

data class Songs(val curnum : Int, val curpage : Int, val list : List<SongList>)

data class SongList(val f : String)

data class SongInfo(
    var songId : String,
    var title : String,
    var singer : String,
    val albumImgId : String,
    var album : String,)



data class SingleSongInfo(
    var title : String,
    var singer : String,
    var albumImgId : String,
    var album : String,
    var url : String? = null,
    val songmid : String? = null
)