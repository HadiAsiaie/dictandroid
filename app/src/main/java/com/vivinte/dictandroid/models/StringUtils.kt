package com.vivinte.dictandroid.models

object StringUtils {
    val perm_dict = Array(1.shl(16)) {
        it.toChar()
    }
    val inv_dict = Array(1.shl(16)) {
        it.toChar()
    }
    val mapper = Array(1.shl(16)) {
        it.toChar()
    }
    val okEnglish = Array(1.shl(16)) {
        false
    }

    init {
        val bads: HashMap<String, String> = hashMapOf("èéêëēėęéẻeèẽẹếểêềễeêềễệ" to "e",
                "ÿỵýỹỷỳyỳ" to "y",
                "ûüùúūứửưừữựựúủuùùũùũụ" to "u",
                "îïíīįìịíĩỉìi" to "i",
                "ôöòóøōõøōºóợớỡởờơơỏốỗộổồôọóõỏỏỏoò" to "o",
                "àáâäãąåāªẵắặâầẩẫấậaàảãáạăằằẳ" to "a",
                "ßśš" to "s",
                "ł" to "l",
                "žźż" to "z",
                "çćč" to "c",
                "ñń" to "n",
                "đ" to "d", "آأإ" to "ا",
                "یئى" to "ي",
                "ک" to "ك",
                "ة" to "ه",
                "ؤ" to "و");


        for (chars in bads) {
            for (i in 0 until chars.key.length) {
                val ch = chars.key[i]
                val ch2 = chars.value[0]
                mapper[ch.toInt()] = ch2;
            }
        }

        val encoded_chars = " !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}"
        val to_decoded_chars = "%PcV\"-x2S\'bkR|p>oNl:[si;yX =rqm4BA`0eWKzgDZ_Y/fM,u8T!*G{v]@#HU(\\nOFjJ<^E?La3w.Q61d&7h5+$9I)}tC"
        for (i in 0 until encoded_chars.length) {
            myAssert(perm_dict[encoded_chars[i].toInt()] == encoded_chars[i])
            myAssert(inv_dict[to_decoded_chars[i].toInt()] == to_decoded_chars[i])
            perm_dict[encoded_chars[i].toInt()] = to_decoded_chars[i];
            inv_dict[to_decoded_chars[i].toInt()] = encoded_chars[i];
        }
        val OKForEnglishCharacters = " !\"#$%&'()*+,-./0123456789:;<=>?@[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\n";
        for (ch in OKForEnglishCharacters) {
            okEnglish[ch.toInt()] = true;
        }


    }


    fun numEnglishLetters(s:String):Int{
        var res=0
        for (ch in s.toLowerCase()){
            if (ch>='a' && ch<='z'){
                res+=1
            }
        }
        return res;
    }

    fun numEnglishCharacter(s:String):Int{
        var res=0
        for (ch in s.toLowerCase()){
            if (okEnglish[ch.toInt()]){
                res+=1
            }
        }
        return res;
    }

    fun getTextLanguage(text:String):String{

        val cntEnglishChars= numEnglishCharacter(text);
        val cntEnglishLetters= numEnglishLetters(text);
        return (if(cntEnglishChars>=text.length/2 && cntEnglishLetters>=1) "en" else getLanguage())
        /*
        if (text.length>=6){

        }
        else{
            if (cntEnglishLetters==0){
                return getLanguage()
            }
            else{

            }
        }
        */

    }
    fun getLanguageFromWord(word: String):String{
        myAssert(has_blah_blah(word))
        return word.substring(0,2)
    }
    fun getOtherLanguageFromWord(word: String):String{
        val from= getLanguageFromWord(word)
        return getOtherLanguage(from)
    }
    fun getLanguage():String{
        return "fa";
    }
    fun getOtherLanguage(lang:String):String{

        if (lang=="en"){
            return getLanguage()
        }
        else{
            myAssert(lang== getLanguage())
            return "en"
        }
    }
    fun decrypt(s: String): String {
        val res: Array<Char> = Array(s.length) {
            'a'
        }
        for (i in 0 until s.length) {
            val ch = s[i];
            res[i] = inv_dict[ch.toInt()];
        }
        return String(res.toCharArray())
    }

    fun has_blah_blah(word: String): Boolean {
        if (word.length < 3)
            return false;
        return true;
    }

    fun remove_blah_blah_from_word(word: String): String {
        myAssert(has_blah_blah(word));
        return word.substring(3)
    }

    fun unicode_order(s1: String, s2: String): Int {
        val minLen = Math.min(s1.length, s2.length)
        for (i in 0 until minLen) {
            if (s1[i] < s2[i]) {
                return -1;
            } else if (s1[i] > s2[i]) {
                return +1;
            }

        }
        if (s1.length < s2.length) {
            return -1;
        } else if (s1.length > s2.length) {
            return +1;
        }
        return 0;
    }

    fun normalize(_s: String): String {
        val s = _s.toLowerCase().trim(' ')
        val goodChars= MutableList(s.length) {
            StringUtils.mapper[s[it].toInt()]
        }
        return String(goodChars.toCharArray())
    }
    fun normalized_order (s1: String, s2: String): Int{
        return unicode_order(normalize(s1),normalize(s2))

    }
    fun isCool(searchedString:String, word:String):Boolean{
        val l1=searchedString.length
        val l2=word.length
        if(l1>l2)
            return false;
        val z= normalized_order(word.substring(0,l1),searchedString);
        return z==0;

    }




}
