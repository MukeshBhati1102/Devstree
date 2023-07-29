package com.example.myapplication.activity

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapter.LocationListAdapter
import com.example.myapplication.data.PlaceData
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.dialog.SelectBottomFilterDialog
import com.example.myapplication.utils.Commons
import com.example.myapplication.utils.EnumID
import com.example.myapplication.utils.visibilityGone
import com.example.myapplication.utils.visible
import java.util.ArrayList


class MainActivity : BaseActivity(), View.OnClickListener {
    lateinit var binding: ActivityMainBinding

    var enumID : EnumID = EnumID.Filter_ASCENDING

    private val locationListAdapter by lazy {
        LocationListAdapter(this@MainActivity)
    }

    override fun findContentView(): Int {
        return R.layout.activity_main
    }

    override fun bindViewWithViewBinding(view: View) {
        binding = ActivityMainBinding.bind(view)
    }

    override fun initData() {


        setLocationListData(false)
        setClickListener()
    }

    private fun setLocationListData(
        isFilterValue: Boolean = false,
        enumID: EnumID = EnumID.Filter_ASCENDING
    ) {
        if (appDatabase.getPlaceDataDAO().getAllLocation().isEmpty()) {
            binding.flowEmptyLocationList.visible()
            binding.recyclerView.visibilityGone()
            binding.imageDrawMap.visibilityGone()
            binding.buttonAddLocation.visibilityGone()
        } else {
            if (isFilterValue) {
                val data = appDatabase.getPlaceDataDAO().getAllLocation()[0]

                val locationList = appDatabase.getPlaceDataDAO()
                    .findByDistance(data.latitude, data.longitude) as ArrayList<PlaceData>?

                if (enumID == EnumID.FILTER_DESCENDING) {
                    locationList!!.reverse()
                }

                locationListAdapter.addAll(locationList)
            } else {
                locationListAdapter.addAll(
                    appDatabase.getPlaceDataDAO().getAllLocation() as ArrayList<PlaceData>?
                )
            }

            binding.flowEmptyLocationList.visibilityGone()
            binding.recyclerView.visible()
            binding.imageDrawMap.visible()
            binding.buttonAddLocation.visible()
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = locationListAdapter
        }

        locationListAdapter.setClickListener(object : LocationListAdapter.ClickListener {
            override fun onItemEditClick(place: PlaceData?) {
                val intent = Intent(this@MainActivity, LocationActivity::class.java)
                intent.putExtra(Commons.isUpdateLocation, true)
                intent.putExtra(Commons.locationData, place)
                startForResult.launch(intent)
            }

            override fun onItemDeleteClick(place: PlaceData?) {
                appDatabase.getPlaceDataDAO().deleteLocation(place!!)

                Handler(Looper.getMainLooper()).postDelayed({
                    setLocationListData(false)
                }, 100)

            }

        })
    }

    private fun setClickListener() {
        binding.buttonAddPOI.setOnClickListener(this)
        binding.buttonAddLocation.setOnClickListener(this)
        binding.imageDrawMap.setOnClickListener(this)
        binding.customToolbar.imageFilter.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.imageFilter -> {
//
                val selectBottomFilterDialog = SelectBottomFilterDialog()
                selectBottomFilterDialog.enumID = enumID
                selectBottomFilterDialog.dialogCallBack = object :
                    SelectBottomFilterDialog.DialogCallBack {
                    override fun onClearClick() {
                        setLocationListData(false, enumID)
                        selectBottomFilterDialog.dismiss()
                    }

                    override fun onApplyClick(enumID: EnumID) {
                        this@MainActivity.enumID = enumID
                        setLocationListData(true, enumID)
                        selectBottomFilterDialog.dismiss()

                    }

                }
                selectBottomFilterDialog.show(supportFragmentManager, "")
            }

            R.id.buttonAddLocation,
            R.id.buttonAddPOI -> {
                val intent = Intent(this@MainActivity, LocationActivity::class.java)
                startForResult.launch(intent)
            }

            R.id.imageDrawMap -> {
                if (locationListAdapter.itemCount > 1){
                    val intent = Intent(this@MainActivity, PathLocaitonActivity::class.java)
                    intent.putExtra(Commons.locationData, locationListAdapter.mResultList)
                    startForResult.launch(intent)
                }
            }
        }
    }

    val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                setLocationListData(false)
            }
        }
}