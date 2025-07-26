package com.gigaworks.tech.calculator.ui.about.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.gigaworks.tech.calculator.R
import com.gigaworks.tech.calculator.databinding.FragmentOpenSourceBinding
import com.gigaworks.tech.calculator.domain.License
import com.gigaworks.tech.calculator.ui.about.adapter.LicenseAdapter
import com.gigaworks.tech.calculator.ui.base.BaseFragment

class OpenSourceFragment : BaseFragment<FragmentOpenSourceBinding>() {

    private val licenseData = listOf(
        License(
            "big-math",
            "eobermuhlner",
            "MIT License",
            "https://github.com/eobermuhlner/big-math"
        ),
        License(
            "TapTargetView",
            "KeepSafe",
            "Apache License, Version 2.0",
            "https://github.com/KeepSafe/TapTargetView"
        ),
        License(
            "Firebase",
            "firebase",
            "Apache License, Version 2.0",
            "https://github.com/firebase/firebase-android-sdk"
        ),
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setActionBar(binding.toolbar, getString(R.string.open_source)) {
            findNavController().navigateUp()
        }
        setupEdgeToEdge(
            topInsetsView = binding.appBar,
        )

        setupView()

    }

    private fun setupView() {
        val adapter = LicenseAdapter(licenseData, object : LicenseAdapter.OnLicenseClickListener {
            override fun onLicenseClick(license: License) {
                startActivity(Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(license.url) })
            }
        })
        binding.rv.adapter = adapter
        binding.rv.setHasFixedSize(true)
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentOpenSourceBinding.inflate(inflater, container, false)
}