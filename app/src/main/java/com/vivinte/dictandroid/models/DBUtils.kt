package com.vivinte.dictandroid.models

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import org.json.JSONObject



object DBUtils{
    var numElementKey="num_element_in_db";
    lateinit var db: SQLiteDatabase;
    lateinit var dbManager: DBManager;
    val TAG="DBUtils"
    var totalNumberOfWords=0;
    fun createDB(context: Context){
        dbManager = com.vivinte.dictandroid.models.DBManager(context)
        db = dbManager.readableDatabase
        getTotalWords();
    }
    fun getTotalWords():Int{
        if (totalNumberOfWords==0){

            totalNumberOfWords=UserUtils.getDefaults().getInt(numElementKey,0)
            if (totalNumberOfWords==0){
                val selection= ""
                val selectionArgs:Array<String> = arrayOf()
                val columns:Array<String> = arrayOf("Count(*)")
                val cursor= db.query("words",columns,selection,selectionArgs,null,null,null)
                with(cursor) {
                    while (moveToNext()) {
                        val cnt = getInt(0)
                        totalNumberOfWords=cnt;

                    }
                }
                cursor.close()
            }

            myAssert(totalNumberOfWords!=0)
            val defaults=UserUtils.getDefaults()
            val editor=defaults.edit()
            editor.putInt("numElementKey", totalNumberOfWords)
            editor.commit()
        }
        Log.d(TAG,"total number of words is "+ totalNumberOfWords.toString())
        return totalNumberOfWords;
    }
    fun getTranslationString(word:String):String{
        val selection= "word = ?"
        val selectionArgs= arrayOf(word)
        val cursor= db.query("words",null,selection,selectionArgs,null,null,null)
        var translation:String?=null
        with(cursor) {
            while (moveToNext()) {
                translation=getString(getColumnIndexOrThrow("translation"))
            }
        }
        cursor.close()
        translation= StringUtils.decrypt(translation!!)
        return translation!!
    }
    fun getTranslationString(ind:Int):String{
        val selection= "id = ?"
        val selectionArgs= arrayOf(ind.toString())
        val columns:Array<String> = arrayOf("translation")
        val cursor= db.query("words",columns,selection,selectionArgs,null,null,null)
        var translation:String?=null
        with(cursor) {
            while (moveToNext()) {
                translation=getString(getColumnIndexOrThrow("translation"))
            }
        }
        cursor.close()
        translation= StringUtils.decrypt(translation!!)
        return translation!!

    }
    fun getOneTranslation(ind:Int):String{
        val selection= "id = ?"
        val selectionArgs= arrayOf(ind.toString())
        val cursor= db.query("words",null,selection,selectionArgs,null,null,null)
        var translation:String?=null
        with(cursor) {
            while (moveToNext()) {
                translation=getString(getColumnIndexOrThrow("translation"))
            }
        }
        cursor.close()
        translation= StringUtils.decrypt(translation!!)
        val json= JSONObject(translation)
        val map= MyJavaUtils.jsonToMap(json)
        Log.d("DBUtils",translation)
        val res=map["m"] as String
        return res

    }
    fun getOneTranslation(word:String):String{
        val selection= "word = ?"
        val selectionArgs= arrayOf(word)
        val cursor= db.query("words",null,selection,selectionArgs,null,null,null)
        var translation:String?=null
        with(cursor) {
            while (moveToNext()) {
                translation=getString(getColumnIndexOrThrow("translation"))
            }
        }
        cursor.close()
        translation= StringUtils.decrypt(translation!!)
        val json= JSONObject(translation)
        val map= MyJavaUtils.jsonToMap(json)
        Log.d("DBUtils",translation)
        val res=map["m"] as String
        return res
    }
    fun getIndFromWord(word:String):Int{
        val selection= "word = ?"
        val selectionArgs= arrayOf(word)
        val columns:Array<String> = arrayOf("id")
        val cursor= db.query("words",columns,selection,selectionArgs,null,null,null)
        var res=-1
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow("id"))
                res=id
            }
        }
        cursor.close()
        return res
    }
    fun getWordFromInd(ind:Int):String{
        val selection= "id = ?"
        val selectionArgs= arrayOf(ind.toString())
        val columns:Array<String> = arrayOf("word")
        val cursor= db.query("words",columns,selection,selectionArgs,null,null,null)
        var res=""
        with(cursor) {
            while (moveToNext()) {
                val word = getString(getColumnIndexOrThrow("word"))
                res=word
            }
        }
        cursor.close()
        myAssert(res!="")
        return res
    }
    fun get_exact_index_from_naked_word(_naked_word:String):Int{
        val naked_word=_naked_word.toLowerCase()
        var word="en_$naked_word"
        val ind1= getIndFromWord(word)
        word="${StringUtils.getLanguage()}_$naked_word"
        val ind2=getIndFromWord(word)
        if (ind1==-1) {
            return ind2;
        }
        else if(ind2==-1)
            return ind1;
        else{
            val s1= getTranslationString(ind1)
            val s2= getTranslationString(ind2)
            if (s1.length<s2.length) {
                return ind2;
            }
            else if(s1.length>s2.length)
                return ind1;
            else
                return ind1;
        }
    }
    fun findStartEndIndex(searchedString: String):StarEndPair{
        var low = -1
        var high= totalNumberOfWords;
        while (high-low>1) {
            val mid = (low + high) / 2;
            var word = getWordFromInd(mid)
            word = StringUtils.remove_blah_blah_from_word(word)
            val d=StringUtils.normalized_order(searchedString,word)
            if (d==1){
                low=mid
            }
            else{
                high=mid
            }

        }
        var start=high;
        low=high;
        high= totalNumberOfWords;
        //low is always correct,high is always wrong
        while (high-low>1) {
            val mid = (low + high) / 2;
            var word = getWordFromInd(mid)
            word = StringUtils.remove_blah_blah_from_word(word)
            if (StringUtils.isCool(searchedString,word))
                low=mid;
            else
                high=mid;
        }
        var end=low+1;
        if(end > totalNumberOfWords) {
            start=-1
            end=-1
        }
        else{
            var word = getWordFromInd(low)
            word = StringUtils.remove_blah_blah_from_word(word)
            if (!StringUtils.isCool(searchedString,word)){
                start=-1
                end=-1
            }
        }
        return StarEndPair(start,end)

    }
    fun getSearchItemFromInd(ind: Int):SearchItem{
        val word= getWordFromInd(ind)
        val from=StringUtils.getLanguageFromWord(word)
        val to=StringUtils.getOtherLanguageFromWord(word)
        return SearchItem(ind,word.substring(3),from,to)

    }
    fun getSearchResult(_searchedString:String):MutableList<SearchItem>{
        val max_need = 100
        val searchedString:String=_searchedString.toLowerCase().trim()
        val res:MutableList<SearchItem> = mutableListOf()
        val startEnd= findStartEndIndex(searchedString)
        val test= get_exact_index_from_naked_word(searchedString)
        val seen:MutableSet<Int> = mutableSetOf()
        if (test!=-1){
            res.add(getSearchItemFromInd(test))
            seen.add(test)
        }
        for (i in startEnd.start until startEnd.end){

            if (res.size>=max_need){
                break
            }
            var s= getWordFromInd(i)
            s=StringUtils.remove_blah_blah_from_word(s)
            if (StringUtils.normalized_order(searchedString,s)==0){
                if (!seen.contains(i)){
                    res.add(getSearchItemFromInd(i))
                    seen.add(i)
                }
            }
            else{
                break
            }

        }
        for (i in startEnd.start until startEnd.end){

            if (res.size>=max_need){
                break
            }
            if (!seen.contains(i)){
                res.add(getSearchItemFromInd(i))
                seen.add(i)
            }
        }
        return res


    }
}