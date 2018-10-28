package com.vivinte.dictandroid.activities

import android.app.Activity
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
import com.vivinte.dictandroid.models.myAssert
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.Toast
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.InputMethodManager
import android.widget.EditText


class MainActivity : AppCompatActivity(), DefinitionFragment.OnFragmentInteractionListener, SpellCheckerSession.SpellCheckerSessionListener, SearchResultFragment.OnListFragmentInteractionListener {


    //private var mScs: SpellCheckerSession? = null
    var searchResultFragment: SearchResultFragment?=null;
    lateinit var editTextView: EditText;
    lateinit var textView: TextView;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //DBUtils.createDB(this)
        setContentView(R.layout.activity_main)
        editTextView=editText
        textView=textViewMain
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        addSearchResultFragment()
        //searchResultFragment = supportFragmentManager.findFragmentById(R.id.search_result_fragment) as SearchResultFragment

        editText.setOnClickListener {
            editText.requestFocus()
            setSearchResultFragment()
        }
        editText.onFocusChangeListener = OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                Log.d(localClassName,"has focus")
                Toast.makeText(applicationContext, "Got the focus", Toast.LENGTH_LONG).show()
            } else {
                Log.d(localClassName,"lost focus")
                Toast.makeText(applicationContext, "Lost the focus", Toast.LENGTH_LONG).show()
            }
            setSearchResultFragment()
        }
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
                setSearchResultFragment()
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        //setSearchResultFragment(s=editText.text)

    }

    private fun addSearchResultFragment(){
        myAssert(searchResultFragment==null)
        searchResultFragment= SearchResultFragment.newInstance(1)
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.search_fragment_container,searchResultFragment!!)
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit()

    }
    private fun setSearchResultFragment(){
        Log.d(localClassName,"setSearchResultFragment")
        val s=editText.text.toString()
        if (s.isBlank()){

            //search_result_fragment.view!!.visibility= View.INVISIBLE;
        }
        else{
            //search_result_fragment.view!!.visibility= View.VISIBLE;
        }

        searchResultFragment!!.view!!.visibility=if (editText.hasFocus()) View.VISIBLE else View.INVISIBLE
        searchResultFragment!!.text=s.toString();
        /*
        if (!searchResultFragment!!.isAdded){
            val fragmentManager = supportFragmentManager
            var fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.remove(searchResultFragment)
            fragmentTransaction.commit()
            fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container,searchResultFragment)
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit()
        }

        */
        searchResultFragment!!.updateYourSelf(text = s.toString())
        //searchResultFragment!!.searchResultAdapter.mValues= DBUtils.getSearchResult(s.toString())
        //searchResultFragment!!.searchResultAdapter.notifyDataSetChanged()

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
    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity.currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onListFragmentInteraction(item: SearchItem) {
        editText.setText(item.text, TextView.BufferType.EDITABLE);
        val f= DefinitionFragment.newInstance(text = "", from = "", to = "")
        f.searchItem=item
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container,f)
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit()

        searchResultFragment!!.view!!.visibility=View.INVISIBLE
        searchResultFragment!!.view!!.requestFocus()
        hideKeyboard(this)

    }
    override fun onBackPressed() {
        Log.d(localClassName,"onBackPressed")
        //super.onBackPressed()
        textView.requestFocus()
        val fragmentManager = supportFragmentManager
        if ((searchResultFragment!!.view!!.visibility==View.VISIBLE)){

            Log.d(localClassName,"handling onBackPressed")
            //editText.setText("", TextView.BufferType.EDITABLE);
            searchResultFragment!!.view!!.visibility=View.INVISIBLE


            if (fragmentManager.backStackEntryCount==0){
                //editText.setText("", TextView.BufferType.NORMAL);
            }

            textView.requestFocus()
            Log.d(localClassName,"textView focus: ${textView.hasFocus()}")
            Log.d(localClassName,"textEdit focus: ${editTextView.hasFocus()}")
            //searchResultFragment!!.view!!.requestFocus()
        }
        else{
            super.onBackPressed()
        }


    }
}
