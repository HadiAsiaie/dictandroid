package com.vivinte.dictandroid

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.textservice.*
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity(),DefinitionFragment.OnFragmentInteractionListener, SpellCheckerSession.SpellCheckerSessionListener {


    private var mScs: SpellCheckerSession? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DBUtils.createDB(this)
        setContentView(R.layout.activity_main)
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setRawInputType(InputType.TYPE_CLASS_TEXT);


        editText.setOnEditorActionListener(object : TextView.OnEditorActionListener {

            override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    enterPressed()
                    //return true
                }
                return false
            }
        })

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {


            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                if (s.length>0)
                    mScs!!.getSuggestions( TextInfo(s.toString()), 10);
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

    }

    override fun onResume() {
        super.onResume()
        val tsm = getSystemService(Context.TEXT_SERVICES_MANAGER_SERVICE) as TextServicesManager
        mScs = tsm.newSpellCheckerSession(null, Locale.getDefault(), this, false);
    }

    override fun onGetSuggestions(arg0: Array<SuggestionsInfo>) {
        val sb = StringBuilder()

        for (i in arg0.indices) {
            // Returned suggestions are contained in SuggestionsInfo
            val len = arg0[i].suggestionsCount

            sb.append('\n')

            for (j in 0 until len) {
                sb.append("," + arg0[i].getSuggestionAt(j))
            }

            sb.append(" ($len)")
        }
        Log.d(localClassName,sb.toString())

        //runOnUiThread { tv1.append(sb.toString()) }
    }

    override fun onGetSentenceSuggestions(p0: Array<out SentenceSuggestionsInfo>?) {

    }

    fun enterPressed(){
        val text=editText.text.toString()

        val from="en"
        val to="fa";
        val f=DefinitionFragment.newInstance(text = text,from = from,to = to)
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container,f)
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit()

    }
    override fun onFragmentInteraction(uri: Uri) {

    }
}
