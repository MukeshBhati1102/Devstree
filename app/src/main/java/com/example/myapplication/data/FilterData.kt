package com.example.myapplication.data

import com.example.myapplication.utils.EnumID

class FilterData {
    var name: String = ""
    var enumID : EnumID? = null
    var isSelected : Boolean = false

    constructor(name: String, enumID: EnumID?, isSelected : Boolean) {
        this.name = name
        this.enumID = enumID
        this.isSelected = isSelected
    }
}