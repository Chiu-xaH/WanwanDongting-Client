package com.chiuxah.wanwandongting.logic.dataModel


data class SearchResponse(val data : SearchData)

data class SearchData(val song : Songs)

data class Songs(val curnum : Int, val curpage : Int, val list : List<SongList>)

data class SongList(val f : String)

data class SongsInfo(
    var songId : String,
    var title : String,
    var singer : String,
    val albumImgId : String,
    var album : String,)