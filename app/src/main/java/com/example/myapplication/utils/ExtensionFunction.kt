package com.example.myapplication.utils

import android.view.View

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.visibilityGone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}