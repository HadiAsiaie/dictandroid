package com.vivinte.dictandroid.activities


import android.graphics.Color
import android.graphics.Typeface.BOLD
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.text.Layout
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.*
import com.vivinte.dictandroid.R
import kotlinx.android.synthetic.main.activity_test.*
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.vivinte.dictandroid.models.*


class TestActivity : AppCompatActivity() {

    var from="fa"
    var to="en"
    var text="سالم";
    var translation:String?= null;
    var db:AppDatabase?=null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sRoomDatabaseCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

            }
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                Log.d(localClassName,"db is opened");
                addData()

            }

        }
        db = Room.databaseBuilder(applicationContext,
                AppDatabase::class.java, "word_database").build()



        setContentView(R.layout.activity_test)

        val text2="noun\ngo\ndo\nmake\n"
        val spannable = SpannableStringBuilder(text2)
        textView.setMovementMethod(ScrollingMovementMethod())


        //spannable.setSpan(QuoteSpan(Color.RED), 8, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        val al=android.text.style.AlignmentSpan.Standard(Layout.Alignment.ALIGN_RIGHT)
        spannable.setSpan(al, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)


        val curInd=spannable.length
        spannable.append("this is some extra text text\n")
        val nextInd=spannable.length
        val mid=android.text.style.AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER)
        spannable.setSpan(mid, curInd, nextInd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)


        textView.text=getCompleteTranslation(text,from,to)
        speakerButton.isEnabled= haveSound(to)
        speakerButton.setOnClickListener {
            speakerButtonTouched()
        }
        progressBar.visibility=View.INVISIBLE;
    }
    fun speakerButtonTouched(){
        Log.d(localClassName,"speakerButtonTouched")
        progressBar.visibility=View.VISIBLE;
        speakerButton.visibility=View.INVISIBLE
        if (translation!=null){
            playText(this,translation!!,to){
                progressBar.visibility=View.INVISIBLE;
                speakerButton.visibility=View.VISIBLE

            }
        }


    }
    fun addData(){
        val db=this.db!!;
        db.userDao().insert(Word("Hi"))

    }
    fun getCompleteTranslation(text:String,from:String,to:String):SpannableStringBuilder{

        val textWithLang="${from}_$text"
        translation=DBUtils.getOneTranslation(textWithLang)
        val ind=DBUtils.getIndFromWord(textWithLang)
        val meaningArray=DBUtils.getMeaningArray(ind)
        val spannable=SpannableStringBuilder(translation)
        spannable.append("\n")
        var curInd=0
        var nextInd=spannable.length

        spannable.setSpan(android.text.style.AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                curInd, nextInd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val ap=TextAppearanceSpan(this,android.R.style.TextAppearance_Material_Headline)
        //spannable.setSpan(RelativeSizeSpan(1.5f), curInd, nextInd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(ap, curInd, nextInd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(ForegroundColorSpan(ResourcesCompat.getColor(getResources(), R.color.blackTextColor, null)),
                curInd, nextInd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        for (i in 0 until 3){
            for (p in meaningArray){
                curInd=spannable.length
                spannable.append(p[0])
                spannable.append("\n")
                nextInd=spannable.length
                spannable.setSpan(android.text.style.AlignmentSpan.Standard(Layout.Alignment.ALIGN_RIGHT),
                        curInd, nextInd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                spannable.setSpan(ForegroundColorSpan(ResourcesCompat.getColor(getResources(), R.color.mySecondaryTextColor, null)),
                        curInd, nextInd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                curInd=spannable.length
                for (j in 1 until p.size){
                    spannable.append(p[j])
                    spannable.append("\n")
                }
                nextInd=spannable.length
                spannable.setSpan(android.text.style.AlignmentSpan.Standard(Layout.Alignment.ALIGN_LEFT),
                        curInd, nextInd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        return spannable
    }
}
