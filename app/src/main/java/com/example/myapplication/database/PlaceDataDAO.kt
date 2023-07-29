package com.example.myapplication.database

import androidx.room.*
import com.example.myapplication.data.PlaceData

@Dao
interface PlaceDataDAO {

    @Insert
    fun insetLocationData(placeData: PlaceData)

    @Query("SELECT * FROM placedata_table")
     fun getAllLocation(): List<PlaceData>

    @Update
    fun updateLocation(placeData: PlaceData)

    @Delete
    fun deleteLocation(placeData: PlaceData)

    @Query("SELECT * FROM placedata_table ORDER BY ABS(latitude - :latitude) + ABS(longitude - :longitude) ASC")
    abstract fun findByDistance(latitude:Double,longitude:Double): MutableList<PlaceData>

}