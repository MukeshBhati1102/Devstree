package com.example.myapplication.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.PlaceData
import android.view.ViewGroup
import android.view.LayoutInflater
import com.example.myapplication.R
import com.example.myapplication.data.FilterData
import com.example.myapplication.databinding.RowFilterItemBinding
import com.example.myapplication.databinding.RowLocationItemBinding
import com.example.myapplication.utils.visibilityGone
import com.example.myapplication.utils.visible
import java.lang.Exception
import java.util.ArrayList

class FilterAdapter(private val mContext: Context) :
    RecyclerView.Adapter<FilterAdapter.ViewHolder>() {
    private val mResultList = ArrayList<FilterData>()
    private var clickListener: ClickListener? = null

    fun addAll(mResultList: ArrayList<FilterData>?) {
        try {
            this.mResultList.clear()
            this.mResultList.addAll(mResultList!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        notifyDataSetChanged()
    }

    fun changeSelection(position: Int){
        for (i in mResultList.indices){
            mResultList[i].isSelected = position == i
        }

        notifyDataSetChanged()
    }

    fun setClickListener(clickListener: ClickListener?) {
        this.clickListener = clickListener
    }

    interface ClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val convertView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.row_filter_item, viewGroup, false)
        return ViewHolder(RowFilterItemBinding.bind(convertView))
    }

    override fun onBindViewHolder(mPredictionHolder: ViewHolder, i: Int) {
        mPredictionHolder.bindData()
    }

    override fun getItemCount(): Int {
        return mResultList.size
    }

    fun getSeletedItem(): FilterData? {
        for (i in mResultList.indices){
            if (mResultList[i].isSelected){
                return mResultList[i]
            }
        }
        return null
    }

    inner class ViewHolder(val binding: RowFilterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData() {
            binding.imageViewChecked.isSelected = mResultList[adapterPosition].isSelected
            binding.textViewName.text = mResultList[adapterPosition].name

            binding.root.setOnClickListener {
                clickListener?.let {
                    it.onItemClick(adapterPosition)
                }
            }
        }
    }
}