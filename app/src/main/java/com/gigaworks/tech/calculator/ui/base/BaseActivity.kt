package com.gigaworks.tech.calculator.ui.base

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewbinding.ViewBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import javax.annotation.Nullable

abstract class BaseActivity<B : ViewBinding> : AppCompatActivity() {
    private var _binding: B? = null
    protected lateinit var binding: B
        private set // This is to prevent the user from setting the binding
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewBinding(layoutInflater)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            window.statusBarColor = Color.BLACK
        }
        setContentView(binding.root)
        firebaseAnalytics = Firebase.analytics
    }

    fun setupActionBar(toolbar: Toolbar, title: String = "", onBackIconClick: (View) -> Unit = {}) {
        setSupportActionBar(toolbar)
        toolbar.apply {
            setTitle(title)
            setNavigationOnClickListener(onBackIconClick)
        }
    }

    protected fun logEvent(eventName: String, @Nullable bundle: Bundle? = null) {
        firebaseAnalytics.logEvent(eventName, bundle)
    }

    abstract fun getViewBinding(inflater: LayoutInflater): B

}