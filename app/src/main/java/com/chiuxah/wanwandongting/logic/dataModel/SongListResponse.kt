package com.chiuxah.wanwandongting.logic.dataModel

data class ListInfoResponse(val result : ListResult)

data class ListResult( val list: List<SongListItem>?,val info : ListInfo)
data class ListInfo(
    val host : HostData,
    val listenNum : Long,
    val pictureUrl : String?,
    val songNum : Long,
    val title : String?)

data class HostData(
    val name : String,
    val pictureUrl: String?
)
data class SongListItem(
    val album : AlbumData,
    val id : Long?,
    val mid : String?,
    val name : String?,
    val remark : String,
    val singer : List<SingerData>?,

    )

data class AlbumData(
    val id : Long?,
    val mid : String?,
    val name: String?
)

data class SingerData(
    val id : Long?,
    val mid : String?,
    val title: String?
)
