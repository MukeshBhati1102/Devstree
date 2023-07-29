package com.example.myapplication.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "placedata_table")
//data class PlaceData(val placeId: String, val city:String, var location :String, val latitudeval :Double, val longitude: Double )

class PlaceData :Serializable {

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @ColumnInfo(name = "place_id")
    var placeId: String = ""

    @ColumnInfo(name = "city")
    var city: String = ""

    @ColumnInfo(name = "location")
    var location: String = ""

    @ColumnInfo(name = "latitude")
    var latitude: Double = 0.0

    @ColumnInfo(name = "longitude")
    var longitude: Double = 0.0
}