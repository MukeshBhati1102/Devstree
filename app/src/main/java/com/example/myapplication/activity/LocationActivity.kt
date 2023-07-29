package com.example.myapplication.activity

import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapter.PlacesAutoCompleteAdapter
import com.example.myapplication.data.PlaceData
import com.example.myapplication.databinding.ActivityLocationBinding
import com.example.myapplication.utils.Commons
import com.example.myapplication.utils.visibilityGone
import com.example.myapplication.utils.visible
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import java.lang.String
import kotlin.CharSequence
import kotlin.Int
import kotlin.apply
import kotlin.getValue
import kotlin.lazy
import kotlin.let


class LocationActivity : BaseActivity(), View.OnClickListener, OnMapReadyCallback {
    lateinit var binding: ActivityLocationBinding
    private var mGoogleMap: GoogleMap? = null

    private var selectedPlaceData: PlaceData? = null

    private var isUpdateLocation = false

    private val mAutoCompleteAdapter by lazy {
        PlacesAutoCompleteAdapter(this)
    }

    override fun findContentView(): Int {
        return R.layout.activity_location
    }

    override fun bindViewWithViewBinding(view: View) {
        binding = ActivityLocationBinding.bind(view)
    }

    override fun initData() {
        initialParam()

        Places.initialize(this@LocationActivity, resources.getString(R.string.google_map_key));

        binding.customToolbar.imageLocation.visibilityGone()
        binding.customToolbar.imageBack.visible()
        binding.customToolbar.imageFilter.visible()

        binding.editTextSearchLocation.addTextChangedListener(filterTextWatcher)

        if (isUpdateLocation) {
            binding.containerLocationSaveView.visible()
        }

        setAdapterData()
        setUpMapIfNeeded()
        setClickListener()
    }

    private fun initialParam() {
        intent.extras?.let {
            if (it.containsKey(Commons.isUpdateLocation)) {
                isUpdateLocation = it.getBoolean(Commons.isUpdateLocation)
            }

            if (it.containsKey(Commons.locationData)) {
                selectedPlaceData = it.getSerializable(Commons.locationData) as PlaceData
            }
        }
    }

    private fun setClickListener() {
        binding.customToolbar.imageBack.setOnClickListener(this)
        binding.buttonSave.setOnClickListener(this)
        binding.customToolbar.imageFilter.setOnClickListener(this)
    }

    private val filterTextWatcher: TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
            if (s.toString() != "") {
                mAutoCompleteAdapter.filter.filter(s.toString())
                if (binding.recyclerview.visibility == View.GONE) {
                    binding.recyclerview.visible()
                    binding.textViewResult.visible()
                }
            } else {
                if (binding.recyclerview.visibility == View.VISIBLE) {
                    binding.recyclerview.visibilityGone()
                    binding.textViewResult.visibilityGone()
                }
            }
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    }

    private fun setAdapterData() {
        binding.recyclerview.apply {
            layoutManager = LinearLayoutManager(this@LocationActivity)
            adapter = mAutoCompleteAdapter
        }

        mAutoCompleteAdapter.setClickListener(object : PlacesAutoCompleteAdapter.ClickListener {
            override fun click(place: PlaceData?) {

                if (isUpdateLocation) {
                    val id = selectedPlaceData!!.id
                    selectedPlaceData = place
                    selectedPlaceData!!.id = id
                } else {
                    selectedPlaceData = place
                }

                binding.containerLocationSaveView.visible()
                binding.recyclerview.visibilityGone()
                binding.textViewResult.visibilityGone()
                hideKeyboard()
                setLocationMarker()
            }
        })

        mAutoCompleteAdapter.notifyDataSetChanged()
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.imageBack -> {
                finish()
            }

            R.id.buttonSave -> {

                if (isUpdateLocation) {
                    appDatabase.getPlaceDataDAO().updateLocation(selectedPlaceData!!)
                } else {
                    appDatabase.getPlaceDataDAO().insetLocationData(selectedPlaceData!!)
                }

                binding.containerLocationSaveView.visibilityGone()

                setResult(RESULT_OK)
                Handler(Looper.getMainLooper()).postDelayed({
                    finish()
                }, 100)

            }

            R.id.imageFilter -> {
            }
        }
    }

    private fun setUpMapIfNeeded() {
        val mapFragment = fragmentManager
            .findFragmentById(R.id.map) as MapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setLocationMarker() {
        val latLong = LatLng(selectedPlaceData!!.latitude, selectedPlaceData!!.longitude)
        val markerOptions = MarkerOptions()

        markerOptions.position(latLong)
        markerOptions.title(selectedPlaceData!!.city)
        mGoogleMap!!.clear()

        mGoogleMap!!.setOnMapLoadedCallback(OnMapLoadedCallback { // Animating to the touched position
            mGoogleMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLong))
            mGoogleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLong, 13.3f))
            mGoogleMap!!.addMarker(markerOptions)
        })
    }

    override fun onMapReady(googleMap1: GoogleMap) {
        mGoogleMap = googleMap1

        selectedPlaceData?.let {
            setLocationMarker()
        }
    }
}