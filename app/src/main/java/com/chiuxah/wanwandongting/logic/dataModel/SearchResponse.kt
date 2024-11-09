package com.chiuxah.wanwandongting.logic.dataModel


data class SearchResponse(val data : SearchData)

data class SearchData(val song : Songs)

data class Songs(val curnum : Int, val curpage : Int, val list : List<SongList>)

data class SongList(val f : String)

data class SongsInfo(val songId : String,
                     val title : String,
                     val singer : String,
                     val albumImgId : String,
                     val album : String,)