package com.example.myapplication.activity

import android.graphics.Color
import android.util.Log
import android.view.View
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.example.myapplication.R
import com.example.myapplication.api.ServiceCallBack
import com.example.myapplication.data.PlaceData
import com.example.myapplication.databinding.ActivityPathLocationBinding
import com.example.myapplication.utils.Commons
import com.example.myapplication.utils.visibilityGone
import com.example.myapplication.utils.visible
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory


class PathLocaitonActivity : BaseActivity(), View.OnClickListener, OnMapReadyCallback {
    lateinit var binding: ActivityPathLocationBinding
    private var mGoogleMap: GoogleMap? = null

    private var selectedPlaceDataList = ArrayList<PlaceData>()

    override fun findContentView(): Int {
        return R.layout.activity_path_location
    }

    override fun bindViewWithViewBinding(view: View) {
        binding = ActivityPathLocationBinding.bind(view)
    }

    override fun initData() {
        initialParam()

        binding.customToolbar.imageLocation.visibilityGone()
        binding.customToolbar.imageBack.visible()
        binding.customToolbar.imageFilter.visibilityGone()

        setUpMapIfNeeded()
        setClickListener()
    }

    private fun initialParam() {
        intent.extras?.let {

            if (it.containsKey(Commons.locationData)) {
                selectedPlaceDataList =
                    it.getSerializable(Commons.locationData) as ArrayList<PlaceData>
            }
        }
    }

    private fun setClickListener() {
        binding.customToolbar.imageBack.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.imageBack -> {
                finish()
            }

        }
    }

    private fun setUpMapIfNeeded() {
        val mapFragment = fragmentManager
            .findFragmentById(R.id.map) as MapFragment
        mapFragment.getMapAsync(this)
    }

//    private fun getMapsApiDirectionsUrl(): String? {
//
//        var routPathPoint = ""
//
//        for (i in selectedPlaceDataList.indices) {
//            if (i > 0 && i <= selectedPlaceDataList.size - 2) {
//                routPathPoint =
//                    routPathPoint + selectedPlaceDataList[i].latitude + "," + selectedPlaceDataList[i].longitude + "|"
//            }
//        }
//
//        val origin =
//            "origin=" + selectedPlaceDataList[0].latitude + "," + selectedPlaceDataList[0].longitude
//        val waypoints =
//            "waypoints=optimize:true|$routPathPoint"
//        val destination =
//            "destination=" + selectedPlaceDataList[selectedPlaceDataList.size - 1].latitude + "," + selectedPlaceDataList[selectedPlaceDataList.size - 1].longitude
//        val sensor = "sensor=false"
//        val params = "$origin&$waypoints&$destination&$sensor"
//        val output = "json"
////        return (output + "?" + params + "&key=" + getString(R.string.google_map_key))
//        return ("https://maps.googleapis.com/maps/api/directions/"
//                + output + "?" + params + "&key=" + getString(R.string.google_map_key))
//    }

    private fun getMapsApiDirectionsUrlParam(): Map<String, String> {

        val param = HashMap<String, String>()

        var routPathPoint = ""

        for (i in selectedPlaceDataList.indices) {
            if (i > 0 && i <= selectedPlaceDataList.size - 2) {
                routPathPoint =
                    routPathPoint + selectedPlaceDataList[i].latitude + "," + selectedPlaceDataList[i].longitude + "|"
            }
        }

        param["origin"] =
            "" + selectedPlaceDataList[0].latitude + "," + selectedPlaceDataList[0].longitude
        param["waypoints"] = "optimize:true|$routPathPoint"
        param["destination"] =
            ""+selectedPlaceDataList[selectedPlaceDataList.size - 1].latitude + "," + selectedPlaceDataList[selectedPlaceDataList.size - 1].longitude
        param["sensor"] = "false"
        param["key"] = getString(R.string.google_map_key)

        return param
    }

    private fun decodePoly(encoded: String): List<LatLng>? {
        val poly: MutableList<LatLng> = ArrayList()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val p = LatLng(
                lat.toDouble() / 1E5,
                lng.toDouble() / 1E5
            )
            poly.add(p)
        }
        return poly
    }

    fun drawRoute(result: String?) {
        try {
            //Tranform the string into a json object
            val json = JSONObject(result)
            val routeArray = json.getJSONArray("routes")
            val routes = routeArray.getJSONObject(0)
            val overviewPolylines = routes.getJSONObject("overview_polyline")
            val encodedString = overviewPolylines.getString("points")
            val list: List<LatLng> = decodePoly(encodedString)!!
            val line: Polyline = mGoogleMap!!.addPolyline(
                PolylineOptions()
                    .addAll(list)
                    .width(12f)
                    .color(Color.parseColor("#F10606")) //Google maps blue color
                    .geodesic(true)
            )

            setMarker()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun getData() {

        val retrofit = Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .baseUrl("https://maps.googleapis.com/maps/api/")
            .build()

        val serviceCallBack: ServiceCallBack = retrofit.create(ServiceCallBack::class.java)

        val stringCall: Call<String?>? =
            serviceCallBack.getStringResponse(getMapsApiDirectionsUrlParam()!!)
        stringCall!!.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>?, response: Response<String?>) {
                if (response.isSuccessful) {
                    val responseString: String = response.body()!!
                    drawRoute(responseString)
                }
            }

            override fun onFailure(call: Call<String?>?, t: Throwable?) {
                Log.e("Tag", " error >> " + t!!.message)
            }
        })

    }

    private fun setMarker() {
        val latLongList = ArrayList<LatLng>()

        for (i in selectedPlaceDataList.indices) {
            latLongList.add(
                LatLng(
                    selectedPlaceDataList[i].latitude,
                    selectedPlaceDataList[i].longitude
                )
            )

            mGoogleMap!!.addMarker(
                MarkerOptions().position(
                    LatLng(
                        selectedPlaceDataList[i].latitude,
                        selectedPlaceDataList[i].longitude
                    )
                ).title(selectedPlaceDataList[i].city)
            )
            if (i == 0) {

                mGoogleMap!!.moveCamera(
                    CameraUpdateFactory.newLatLng(
                        LatLng(
                            selectedPlaceDataList[i].latitude,
                            selectedPlaceDataList[i].longitude
                        )
                    )
                )

                mGoogleMap!!.animateCamera(CameraUpdateFactory.zoomTo(15f))
            }

        }
    }

    override fun onMapReady(googleMap1: GoogleMap) {
        mGoogleMap = googleMap1

        getData()
    }
}