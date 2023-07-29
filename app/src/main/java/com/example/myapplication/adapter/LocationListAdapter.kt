package com.example.myapplication.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.PlaceData
import android.view.ViewGroup
import android.view.LayoutInflater
import com.example.myapplication.R
import com.example.myapplication.databinding.RowLocationItemBinding
import com.example.myapplication.utils.visibilityGone
import com.example.myapplication.utils.visible
import java.lang.Exception
import java.util.ArrayList

class LocationListAdapter(private val mContext: Context) :
    RecyclerView.Adapter<LocationListAdapter.ViewHolder>() {
    val mResultList = ArrayList<PlaceData>()
    private var clickListener: ClickListener? = null

    fun addAll(mResultList: ArrayList<PlaceData>?) {
        try {
            this.mResultList.clear()
            this.mResultList.addAll(mResultList!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        notifyDataSetChanged()
    }

    fun setClickListener(clickListener: ClickListener?) {
        this.clickListener = clickListener
    }

    interface ClickListener {
        fun onItemEditClick(place: PlaceData?)
        fun onItemDeleteClick(place: PlaceData?)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val convertView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.row_location_item, viewGroup, false)
        return ViewHolder(RowLocationItemBinding.bind(convertView))
    }

    override fun onBindViewHolder(mPredictionHolder: ViewHolder, i: Int) {
        mPredictionHolder.bindData()
    }

    override fun getItemCount(): Int {
        return mResultList.size
    }

    fun getItem(position: Int): PlaceData {
        return mResultList[position]
    }

    inner class ViewHolder(val binding: RowLocationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData() {
            binding.textViewCity.text = mResultList[adapterPosition].city
            binding.textViewLocation.text = mResultList[adapterPosition].location
            if (mResultList[adapterPosition].id == 1){
                binding.textViewPrimary.visible()
            }else{
                binding.textViewPrimary.visibilityGone()
            }

            binding.imageEditLocation.setOnClickListener {
                clickListener?.let {
                    it.onItemEditClick(mResultList[adapterPosition])
                }
            }

            binding.imageDelete.setOnClickListener {
                clickListener?.let {
                    it.onItemDeleteClick(mResultList[adapterPosition])
                }
            }

        }
    }
}