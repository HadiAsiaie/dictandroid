package com.vivinte.dictandroid

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper
import org.json.JSONObject

object StringUtils{
    val perm_dict:Array<Char> = Array(1.shl(16)){
        it.toChar()
    }
    val inv_dict=Array(1.shl(16)){
        it.toChar()
    }
    init {
        val encoded_chars=" !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}"
        val to_decoded_chars="%PcV\"-x2S\'bkR|p>oNl:[si;yX =rqm4BA`0eWKzgDZ_Y/fM,u8T!*G{v]@#HU(\\nOFjJ<^E?La3w.Q61d&7h5+$9I)}tC"
        for (i in 0 until encoded_chars.length){
            myAssert(perm_dict[encoded_chars[i].toInt()]==encoded_chars[i])
            myAssert(inv_dict[to_decoded_chars[i].toInt()]==to_decoded_chars[i])
            perm_dict[encoded_chars[i].toInt()]=to_decoded_chars[i];
            inv_dict[to_decoded_chars[i].toInt()]=encoded_chars[i];
        }

    }
    fun decrypt(s:String):String{
        val res:Array<Char> = Array(s.length){
            'a'
        }
        for ( i in 0 until s.length) {
            val ch=s[i];
            res[i]=inv_dict[ch.toInt()];
        }
        return String(res.toCharArray())
    }
}
object DBUtils{
    lateinit var db:SQLiteDatabase;
    lateinit var dbManager: DBManager;
    fun createDB(context: Context){
        dbManager= DBManager(context)
        db= dbManager.readableDatabase
    }
    fun getOneTranslation(word:String):String{
        val selection= "word = ?"
        val selectionArgs= arrayOf(word)
        val cursor=DBUtils.db.query("words",null,selection,selectionArgs,null,null,null)
        val itemIds = mutableListOf<Long>()
        var translation:String?=null
        with(cursor) {
            while (moveToNext()) {
                val itemId = getLong(getColumnIndexOrThrow("id"))
                itemIds.add(itemId)
                translation=getString(getColumnIndexOrThrow("translation"))
            }
        }
        translation=StringUtils.decrypt(translation!!)
        val json=JSONObject(translation)
        val map=MyJavaUtils.jsonToMap(json)


        Log.d("DBUtils",itemIds.toString())
        Log.d("DBUtils",translation)
        val res=map["m"] as String
        return res
    }
}