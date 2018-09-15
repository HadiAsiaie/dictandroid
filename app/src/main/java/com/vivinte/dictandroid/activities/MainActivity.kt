package com.vivinte.dictandroid.activities

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.textservice.*
import android.widget.TextView
import com.vivinte.dictandroid.models.DBUtils
import com.vivinte.dictandroid.fragments.DefinitionFragment
import com.vivinte.dictandroid.R
import com.vivinte.dictandroid.fragments.SearchResultFragment
import com.vivinte.dictandroid.models.SearchItem
import com.vivinte.dictandroid.models.StringUtils
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), DefinitionFragment.OnFragmentInteractionListener, SpellCheckerSession.SpellCheckerSessionListener, SearchResultFragment.OnListFragmentInteractionListener {


    //private var mScs: SpellCheckerSession? = null
    lateinit var searchResultFragment: SearchResultFragment;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DBUtils.createDB(this)
        setContentView(R.layout.activity_main)
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setRawInputType(InputType.TYPE_CLASS_TEXT);



        searchResultFragment = supportFragmentManager.findFragmentById(R.id.search_result_fragment) as SearchResultFragment

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

                /*
                if (s.length>0)
                    mScs!!.getSuggestions( TextInfo(s.toString()), 10);
                    */
                setSearchResultFragment(s)
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        setSearchResultFragment(s=editText.text)

    }

    private fun setSearchResultFragment(s: CharSequence){
        if (s.isBlank()){

            //search_result_fragment.view!!.visibility= View.INVISIBLE;
        }
        else{
            //search_result_fragment.view!!.visibility= View.VISIBLE;
        }
        searchResultFragment.searchResultAdapter.mValues= DBUtils.getSearchResult(s.toString())
        searchResultFragment.searchResultAdapter.notifyDataSetChanged()

    }
    fun setUI(){

    }
    override fun onResume() {
        super.onResume()
        /*
        val tsm = getSystemService(Context.TEXT_SERVICES_MANAGER_SERVICE) as TextServicesManager
        mScs = tsm.newSpellCheckerSession(null, Locale.getDefault(), this, false);
        */
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
        var text=editText.text.toString()
        text=text.trim()
        if (!text.isEmpty()){
            val from=StringUtils.getTextLanguage(text)
            val to=StringUtils.getOtherLanguage(from);
            val f= DefinitionFragment.newInstance(text = text, from = from, to = to)
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container,f)
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit()
        }


    }
    override fun onFragmentInteraction(uri: Uri) {

    }

    override fun onListFragmentInteraction(item: SearchItem?) {
        val f= DefinitionFragment.newInstance(text = "", from = "", to = "")
        f.searchItem=item!!
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container,f)
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit()

    }
}
