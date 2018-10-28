package com.vivinte.dictandroid.activities

import android.graphics.Color
import android.graphics.Typeface.BOLD
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.BulletSpan
import android.text.style.ForegroundColorSpan
import android.text.style.QuoteSpan
import android.text.style.StyleSpan
import com.vivinte.dictandroid.R
import com.vivinte.dictandroid.models.DBUtils
import kotlinx.android.synthetic.main.activity_test.*

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        val x=DBUtils.getOneTranslation("en_some")
        val text2="noun\ngo\ndo\nmake\n"
        val text="Text is \n spantastic!"
        val spannable = SpannableStringBuilder(text2)


        //spannable.setSpan(QuoteSpan(Color.RED), 8, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        val al=android.text.style.AlignmentSpan.Standard(Layout.Alignment.ALIGN_RIGHT)
        spannable.setSpan(al, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)


        val curInd=spannable.length
        spannable.append("this is some extra text text\n")
        val nextInd=spannable.length
        val mid=android.text.style.AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER)
        spannable.setSpan(mid, curInd, nextInd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        textView.text=getCompleteTranslation("some","en","fa")
    }
    fun getCompleteTranslation(text:String,from:String,to:String):SpannableStringBuilder{

        val textWithLang="${from}_$text"
        val translation=DBUtils.getOneTranslation(textWithLang)
        val ind=DBUtils.getIndFromWord(textWithLang)
        val meaningArray=DBUtils.getMeaningArray(ind)
        val spannable=SpannableStringBuilder(translation)
        spannable.append("\n")
        var curInd=0
        var nextInd=spannable.length

        spannable.setSpan(android.text.style.AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                curInd, nextInd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        for (p in meaningArray){
            curInd=spannable.length
            spannable.append(p[0])
            spannable.append("\n")
            nextInd=spannable.length
            spannable.setSpan(android.text.style.AlignmentSpan.Standard(Layout.Alignment.ALIGN_RIGHT),
                    curInd, nextInd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            curInd=spannable.length
            for (i in 1 until p.size){
                spannable.append(p[i])
                spannable.append("\n")
            }
            nextInd=spannable.length
            spannable.setSpan(android.text.style.AlignmentSpan.Standard(Layout.Alignment.ALIGN_LEFT),
                    curInd, nextInd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        return spannable
    }
}
