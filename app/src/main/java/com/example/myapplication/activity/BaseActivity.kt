package com.example.myapplication.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.example.myapplication.R
import com.example.myapplication.database.AppDatabase

abstract class BaseActivity : AppCompatActivity() {

    lateinit var appDatabase: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(findContentView())
        bindViewWithViewBinding((findViewById<ViewGroup>(android.R.id.content)).getChildAt(0))
        super.onCreate(savedInstanceState)
        appDatabase = AppDatabase.getDatabase(this@BaseActivity)!!

        initData()
    }

    fun hideKeyboard() {
        // Check if no view has focus:

        val view = this.currentFocus
        if (view != null) {
            val inputManager =
                this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }

    }

    abstract fun findContentView(): Int

    abstract fun bindViewWithViewBinding(view: View)

    abstract fun initData()

}