package com.chiuxah.wanwandongting.logic.dao

import android.annotation.SuppressLint
import android.content.ContentValues
import com.chiuxah.wanwandongting.MyApplication

const val createSongList =
    "create table SongList ("+
            " id integer primary key autoincrement," +
            "name text," +
            "listId integer)"
val dbSongList = Database(MyApplication.context,"SongList.db",1, createSongList)

object SongListManager {
    fun add(listId : Long,name : String) {
        val dbwritableDatabase =  dbSongList.writableDatabase
        dbSongList.writableDatabase
        val values1 = ContentValues().apply {
            put("listId", listId)
            put("name",name)
        }
        dbwritableDatabase.insert("SongList", null, values1)
    }
    //删除项目
    fun remove(id: Int) {
        val dbwritableDatabase =  dbSongList.writableDatabase
        dbwritableDatabase.delete("SongList","id = ?", arrayOf(id.toString()))
    }

    @SuppressLint("Range")
    fun queryAll() : MutableList<ListDB> {
        val l = mutableListOf<ListDB>()
        val dbwritableDatabase =  dbSongList.writableDatabase
        val cursor = dbwritableDatabase.query("SongList",null,null,null,null,null,null)
        if(cursor.moveToFirst()){
            do{
                val listId = cursor.getLong(cursor.getColumnIndex("listId"))
                val name = cursor.getString(cursor.getColumnIndex("name"))
                val ids = cursor.getInt(cursor.getColumnIndex("id"))
                l.add(ListDB(listId,name,ids))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return l
    }
}
data class ListDB(val listId : Long,val name : String,val id : Int)