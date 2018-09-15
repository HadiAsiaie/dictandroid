package com.vivinte.dictandroid.models

fun getSomeItems():MutableList<SearchItem>{
    val res:MutableList<SearchItem> = mutableListOf()
    for (i in 0 until 20){
        val cur=SearchItem(i,"some $i","en","fa")
        res.add(cur)
    }
    return res
}

