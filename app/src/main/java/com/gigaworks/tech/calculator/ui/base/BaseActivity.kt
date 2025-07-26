package com.gigaworks.tech.calculator.ui.base

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewbinding.ViewBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ParametersBuilder
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.Firebase
import javax.annotation.Nullable

abstract class BaseActivity<B : ViewBinding> : AppCompatActivity() {
    private var _binding: B? = null
    protected lateinit var binding: B
        private set // This is to prevent the user from setting the binding
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewBinding(layoutInflater)
        
        // Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(window, false)

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

    protected fun logEvent(eventName: String, block: ParametersBuilder.() -> Unit) {
        firebaseAnalytics.logEvent(eventName, block)
    }

    /**
     * Sets up edge-to-edge display with proper window insets handling.
     * 
     * @param topInsetsView View to receive top insets (status bar clearance). If null, no top insets applied.
     * @param bottomInsetsView View to receive bottom insets (navigation bar clearance). If null, no bottom insets applied.
     * @param applyToRoot If true, applies both top and bottom insets to the root view. Overrides individual view parameters.
     */
    protected fun setupEdgeToEdge(
        topInsetsView: View? = null,
        bottomInsetsView: View? = null,
        applyToRoot: Boolean = false
    ) {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            
            if (applyToRoot) {
                // Apply both top and bottom insets to root view
                binding.root.setPadding(
                    binding.root.paddingLeft,
                    insets.top,
                    binding.root.paddingRight,
                    insets.bottom
                )
            } else {
                // Apply top insets to specified view
                topInsetsView?.setPadding(
                    topInsetsView.paddingLeft,
                    insets.top,
                    topInsetsView.paddingRight,
                    topInsetsView.paddingBottom
                )
                
                // Apply bottom insets to specified view
                bottomInsetsView?.setPadding(
                    bottomInsetsView.paddingLeft,
                    bottomInsetsView.paddingTop,
                    bottomInsetsView.paddingRight,
                    insets.bottom
                )
            }
            
            WindowInsetsCompat.CONSUMED
        }
    }

    abstract fun getViewBinding(inflater: LayoutInflater): B

}