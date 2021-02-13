package com.gigaworks.tech.calculator.ui.base

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<B : ViewBinding> : AppCompatActivity() {
    private var _binding: B? = null
    protected val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = getViewBinding(layoutInflater)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            window.statusBarColor = Color.BLACK
        }
        setContentView(binding.root)
    }

    fun setupActionBar(toolbar: Toolbar, title: String = "", onBackIconClick: (View) -> Unit = {}) {
        setSupportActionBar(toolbar)
        toolbar.apply {
            setTitle(title)
            setNavigationOnClickListener(onBackIconClick)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    abstract fun getViewBinding(inflater: LayoutInflater): B

}