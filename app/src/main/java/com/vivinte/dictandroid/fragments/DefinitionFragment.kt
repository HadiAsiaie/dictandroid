package com.vivinte.dictandroid.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.vivinte.dictandroid.R
import com.vivinte.dictandroid.activities.MainActivity
import com.vivinte.dictandroid.models.DBUtils
import com.vivinte.dictandroid.models.SearchItem


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_TEXT = "text"
private const val ARG_FROM = "from"
private const val ARG_TO = "to"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [DefinitionFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [DefinitionFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class DefinitionFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var text: String? = null
    private var from: String? = null
    private var to: String? = null
    private var listener: OnFragmentInteractionListener? = null
    var searchItem:SearchItem?=null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            text = it.getString(ARG_TEXT)
            from = it.getString(ARG_FROM)
            to = it.getString(ARG_TO)
        }


    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val textView=view.findViewById<TextView>(R.id.definitionTextView)


        val translation:String;
        if (searchItem != null){
            translation=DBUtils.getOneTranslation(searchItem!!.id)
        }
        else{
            if (text!=null){
                Log.d("DefinitionFragment","param is: "+text!!)
            }
            else{
                text="null"
                Log.d("DefinitionFragment","param is: null")
            }
            text="${from}_$text"

            translation=DBUtils.getOneTranslation(text!!)
        }
        textView.text=translation

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.const_fragment_layout, container, false)
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onResume() {
        super.onResume()
        val activity=getActivity() as MainActivity
        activity.editTextView.setText(searchItem!!.text,TextView.BufferType.EDITABLE)


    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DefinitionFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(text: String, from: String,to: String) =
                DefinitionFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_TEXT, text)
                        putString(ARG_FROM, from)
                        putString(ARG_TO, to)
                    }
                }
    }
}
