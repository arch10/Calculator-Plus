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
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import javax.annotation.Nullable

abstract class BaseFragment<B : ViewBinding> : Fragment() {
    private var _binding: B? = null
    protected val binding get() = _binding!!
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // initialize viewBinding
        _binding = getViewBinding(inflater, container)
        firebaseAnalytics = Firebase.analytics
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

}
