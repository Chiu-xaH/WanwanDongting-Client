package com.cxh.qqmusictp.logic.dataModel


data class SearchResponse(val data : data)

data class data(val song : songs)

data class songs(val curnum : Int, val curpage : Int, val list : List<list>)

data class list(val f : String)
