package com.chiuxah.wanwandongting.logic.dao

import android.annotation.SuppressLint
import android.content.ContentValues


//object SongListDataBaseManagers {
//    data class ListDB(val listId : Long,val name : String,val id : Int)
//
//    const val createSongList =
//        "create table SongList ("+
//                " id integer primary key autoincrement," +
//                "name text," +
//                "listId integer)"
//    val dbSongList = Database(MyApplication.context,"SongList.db",1, createSongList)
//
//    fun add(listId : Long,name : String) {
//        val dbwritableDatabase =  dbSongList.writableDatabase
//        dbSongList.writableDatabase
//        val values1 = ContentValues().apply {
//            put("listId", listId)
//            put("name",name)
//        }
//        dbwritableDatabase.insert("SongList", null, values1)
//    }
//    //删除项目
//    fun remove(id: Int) {
//        val dbwritableDatabase =  dbSongList.writableDatabase
//        dbwritableDatabase.delete("SongList","id = ?", arrayOf(id.toString()))
//    }
//
//    @SuppressLint("Range")
//    fun queryAll() : MutableList<ListDB> {
//        val l = mutableListOf<ListDB>()
//        val dbwritableDatabase =  dbSongList.writableDatabase
//        val cursor = dbwritableDatabase.query("SongList",null,null,null,null,null,null)
//        if(cursor.moveToFirst()){
//            do{
//                val listId = cursor.getLong(cursor.getColumnIndex("listId"))
//                val name = cursor.getString(cursor.getColumnIndex("name"))
//                val ids = cursor.getInt(cursor.getColumnIndex("id"))
//                l.add(ListDB(listId,name,ids))
//            } while (cursor.moveToNext())
//        }
//        cursor.close()
//        return l
//    }
//}

object SongListDataBaseManager : DataBaseManager("SongList") {
    data class ListDB(val listId : Long,val name : String,val id : Int)

    init {
        // 设置特定的创建表语句
        setCreateQueryTable(
            "create table SongList (" +
                    "id integer primary key autoincrement," +
                    "name text," +
                    "listId integer)"
        )
    }

    fun addItem(listId: Long, name: String) {
        val values = ContentValues().apply {
            put("listId", listId)
            put("name", name)
        }
        super.add(values)
    }
    @SuppressLint("Range")
    fun queryAll() : MutableList<ListDB> {
        val l = mutableListOf<ListDB>()
        val cursor = super.query()
        if (cursor != null) {
            if(cursor.moveToFirst()){
                do{
                    val listId = cursor.getLong(cursor.getColumnIndex("listId"))
                    val name = cursor.getString(cursor.getColumnIndex("name"))
                    val ids = cursor.getInt(cursor.getColumnIndex("id"))
                    l.add(ListDB(listId,name,ids))
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
        return l
    }
}

