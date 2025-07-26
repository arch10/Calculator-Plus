package com.gigaworks.tech.calculator.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.Firebase
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import javax.annotation.Nullable

abstract class BaseFragment<B : ViewBinding> : Fragment() {
    protected lateinit var binding: B
        private set // This is to prevent the user from setting the binding
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // initialize viewBinding
        binding = getViewBinding(inflater, container)
        firebaseAnalytics = Firebase.analytics
        return binding.root
    }

    fun setActionBar(toolbar: Toolbar, title: String = "", onBackIconClick: (View?) -> Unit = {}) {
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.apply {
            setTitle(title)
        }
        toolbar.setNavigationOnClickListener(onBackIconClick)
    }

    protected fun logEvent(eventName: String, @Nullable bundle: Bundle? = null) {
        firebaseAnalytics.logEvent(eventName, bundle)
    }

    abstract fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): B

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
}
