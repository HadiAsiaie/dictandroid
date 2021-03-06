package com.vivinte.dictandroid.fragments

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vivinte.dictandroid.R


import com.vivinte.dictandroid.fragments.SearchResultFragment.OnListFragmentInteractionListener
import com.vivinte.dictandroid.models.DBUtils
import com.vivinte.dictandroid.models.SearchItem
import com.vivinte.dictandroid.models.haveSound
import kotlinx.android.synthetic.main.search_result_item.view.*


/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class MySearchResultRecyclerViewAdapter(
        var mValues: List<SearchItem>,
        private val mListener: OnListFragmentInteractionListener?)
    : RecyclerView.Adapter<MySearchResultRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as SearchItem
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.search_result_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mView.text.text=item.text
        holder.mView.translationTextView.text=DBUtils.getOneTranslation(item.id)
        holder.mView.speakerButton.isEnabled=haveSound(item.from)
        if (DBUtils.isStared(item.text,item.from,item.to)){
            holder.mView.starButton.setBackgroundResource(android.R.drawable.btn_star_big_on)
        }
        else{
            holder.mView.starButton.setBackgroundResource(android.R.drawable.btn_star_big_off)
        }

        //holder.mIdView.text = item.id.toString()
        //holder.mContentView.text = item.text.toString()

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {

        override fun toString(): String {
            return super.toString() + " '" + mView.text.text + "'"
        }
    }
}
