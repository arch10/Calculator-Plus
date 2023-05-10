package com.gigaworks.tech.calculator.ui.about.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.navigation.fragment.findNavController
import com.gigaworks.tech.calculator.BuildConfig
import com.gigaworks.tech.calculator.R
import com.gigaworks.tech.calculator.databinding.FragmentAboutBinding
import com.gigaworks.tech.calculator.ui.base.BaseFragment
import com.gigaworks.tech.calculator.util.JOIN_BETA

class AboutFragment : BaseFragment<FragmentAboutBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setActionBar(binding.toolbar, getString(R.string.title_activity_about)) {
            requireActivity().finish()
        }

        setUpView()
    }

    private fun setUpView() {
        val appVersion = "${getString(R.string.version)}: ${BuildConfig.VERSION_NAME}"
        binding.version.text = appVersion
        binding.privacyCard.setOnClickListener {
            launchUrl(PRIVACY_URL)
        }
        binding.whatsNewCard.setOnClickListener {
            findNavController().navigate(R.id.action_aboutFragment_to_changeLogFragment)
        }
        binding.betaCard.setOnClickListener {
            logEvent(JOIN_BETA)
            startActivity(Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(BETA_TESTING_LINK) })
        }
        binding.openSourceCard.setOnClickListener {
            findNavController().navigate(R.id.action_aboutFragment_to_openSourceFragment)
        }
        binding.termsCard.setOnClickListener {
            launchUrl(TERMS_OF_USE_URL)
        }
    }

    private fun launchUrl(url: String) {
        val customColorScheme =
            CustomTabColorSchemeParams.Builder().setToolbarColor(getToolbarColor()).build()
        val builder = CustomTabsIntent
            .Builder()
            .setShowTitle(true)
            .setDefaultColorSchemeParams(customColorScheme)
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(requireContext(), Uri.parse(url))
    }


    private fun getToolbarColor(): Int {
        val typedValue = TypedValue()
        val typedArray = requireActivity().obtainStyledAttributes(
            typedValue.data,
            intArrayOf(R.attr.colorSurface)
        )
        val color = typedArray.getColor(0, 0)
        typedArray.recycle()
        return color
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAboutBinding.inflate(inflater, container, false)

    companion object {
        const val PRIVACY_URL =
            "https://raw.githubusercontent.com/arch10/Calculator-Plus/main/docs/en/privacy_policy.md"
        const val TERMS_OF_USE_URL =
            "https://raw.githubusercontent.com/arch10/Calculator-Plus/main/docs/en/terms_and_conditions.md"
        const val BETA_TESTING_LINK =
            "https://play.google.com/apps/testing/com.gigaworks.tech.calculator"
    }
}