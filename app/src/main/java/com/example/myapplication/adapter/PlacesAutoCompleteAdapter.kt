package com.example.myapplication.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.adapter.PlacesAutoCompleteAdapter.PredictionHolder
import com.google.android.libraries.places.api.model.AutocompletePrediction
import android.text.style.CharacterStyle
import com.google.android.libraries.places.api.net.PlacesClient
import com.example.myapplication.data.PlaceData
import android.widget.Filter.FilterResults
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.gms.tasks.Tasks
import android.view.ViewGroup
import android.view.LayoutInflater
import com.example.myapplication.R
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.gms.common.api.ApiException
import android.widget.Toast
import android.graphics.Typeface
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.widget.Filter
import android.widget.Filterable
import com.example.myapplication.databinding.RowSearchLocationItemBinding
import com.google.android.libraries.places.api.Places
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class PlacesAutoCompleteAdapter(private val mContext: Context) :
    RecyclerView.Adapter<PredictionHolder>(), Filterable {
    private var mResultList: ArrayList<AutocompletePrediction>? = ArrayList()
    private val STYLE_BOLD: CharacterStyle
    private val STYLE_NORMAL: CharacterStyle
    private val placesClient: PlacesClient
    private var clickListener: ClickListener? = null
    fun setClickListener(clickListener: ClickListener?) {
        this.clickListener = clickListener
    }

    interface ClickListener {
        fun click(place: PlaceData?) //        void click(Place place);
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val results = FilterResults()
                // Skip the autocomplete query if no constraints are given.
                if (constraint != null) {
                    // Query the autocomplete API for the (constraint) search string.
                    mResultList = getPredictions(constraint)
                    if (mResultList != null) {
                        // The API successfully returned results.
                        results.values = mResultList
                        results.count = mResultList!!.size
                    }
                }
                return results
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.
                    notifyDataSetChanged()
                } else {
                    // The API did not return any results, invalidate the data set.
                    //notifyDataSetInvalidated();
                }
            }
        }
    }

    private fun getPredictions(constraint: CharSequence): ArrayList<AutocompletePrediction> {
        val resultList = ArrayList<AutocompletePrediction>()
        val token = AutocompleteSessionToken.newInstance()
        val request = FindAutocompletePredictionsRequest.builder()
            .setSessionToken(token)
            .setQuery(constraint.toString())
            .build()
        val autocompletePredictions = placesClient.findAutocompletePredictions(request)
        try {
            Tasks.await(autocompletePredictions, 60, TimeUnit.SECONDS)
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: TimeoutException) {
            e.printStackTrace()
        }
        return if (autocompletePredictions.isSuccessful) {
            val findAutocompletePredictionsResponse = autocompletePredictions.result
            if (findAutocompletePredictionsResponse != null) for (prediction in findAutocompletePredictionsResponse.autocompletePredictions) {
                Log.e(TAG, " OKH place ID " + prediction.placeId)
                resultList.add(prediction)
            }
            resultList
        } else {
            resultList
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): PredictionHolder {
        val convertView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.row_search_location_item, viewGroup, false)
        return PredictionHolder(RowSearchLocationItemBinding.bind(convertView))
    }

    override fun onBindViewHolder(mPredictionHolder: PredictionHolder, i: Int) {
        mPredictionHolder.bindData()
    }

    override fun getItemCount(): Int {
        return mResultList!!.size
    }

    fun getItem(position: Int): AutocompletePrediction {
        return mResultList!![position]
    }

    inner class PredictionHolder internal constructor(binding: RowSearchLocationItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        var binding: RowSearchLocationItemBinding? = null
        fun bindData() {
            binding!!.textViewCity.text =
                mResultList!![adapterPosition].getPrimaryText(STYLE_NORMAL).toString()
            binding!!.textViewLocation.text =
                mResultList!![adapterPosition].getFullText(STYLE_BOLD).toString()
            binding!!.root.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            val item = mResultList!![adapterPosition]
            val placeFields = Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS
            )
            val request = FetchPlaceRequest.builder(item.placeId, placeFields).build()
            placesClient.fetchPlace(request)
                .addOnSuccessListener { response -> //                    Place place = response.getPlace();
                    val placeData = PlaceData()
                    placeData.placeId = response.place.id
                    placeData.city = response.place.name
                    placeData.location = response.place.address
                    placeData.latitude = response.place.latLng.latitude
                    placeData.longitude = response.place.latLng.longitude
                    clickListener!!.click(placeData)
                }.addOnFailureListener { exception ->
                if (exception is ApiException) {
                    Toast.makeText(mContext, exception.message + "", Toast.LENGTH_SHORT).show()
                }
            }
        }

        init {
            this.binding = binding
        }
    }

    companion object {
        private const val TAG = "PlacesAutoAdapter"
    }

    init {
        STYLE_BOLD = StyleSpan(Typeface.BOLD)
        STYLE_NORMAL = StyleSpan(Typeface.NORMAL)
        placesClient = Places.createClient(mContext)
    }
}