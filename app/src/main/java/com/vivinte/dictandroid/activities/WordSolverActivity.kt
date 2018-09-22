package com.vivinte.dictandroid.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.vivinte.dictandroid.R
import com.vivinte.dictandroid.models.DBUtils
import kotlinx.android.synthetic.main.activity_word_solver.*

class WordSolverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_solver)
        DBUtils.createDB(this)
        val s=DBUtils.canBeRealEnglishWord("some")
        lettersEditText.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    doIt()
                }
                return false
            }
        })
        solveButton.setOnClickListener {
            doIt()
        }
    }
    fun doIt(){

        solveButton.isEnabled=false
        resultTextView.text="doing it"
        val text=lettersEditText.editableText.toString()
        val allWords:MutableSet<String> = mutableSetOf()
        val used=Array(text.length){
            false
        }
        fun go(depth:Int,curWords:String){
            allWords.add(curWords)
            if (depth==text.length){
                return
            }
            for (i in 0 until text.length){
                if (!used[i]){
                    used[i]=true
                    go(depth+1,curWords+text[i])
                    used[i]=false
                }
            }
        }
        go(0,"")
        val wordsSorted=allWords.toList().sortedBy { it.length }
        var res=""
        for (x in wordsSorted){
            if (x.length>1 && DBUtils.canBeRealEnglishWord(x)){
                res+="$x,"
            }

        }
        resultTextView.text=res
        solveButton.isEnabled=true
    }
}
