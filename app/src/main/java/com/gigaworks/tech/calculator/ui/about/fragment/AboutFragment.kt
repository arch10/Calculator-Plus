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
import com.gigaworks.tech.calculator.util.ABOUT_AD_ID
import com.gigaworks.tech.calculator.util.ADS_DISABLED
import com.gigaworks.tech.calculator.util.ADS_ENABLED
import com.gigaworks.tech.calculator.util.GoogleMobileAdsConsentManager
import com.gigaworks.tech.calculator.util.JOIN_BETA
import com.gigaworks.tech.calculator.util.createInlineAdView
import com.gigaworks.tech.calculator.util.getClassName
import com.gigaworks.tech.calculator.util.logD
import com.gigaworks.tech.calculator.util.visible
import com.google.android.gms.ads.AdView
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.get
import com.google.firebase.remoteconfig.remoteConfig

class AboutFragment : BaseFragment<FragmentAboutBinding>() {

    private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager
    private var adView: AdView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setActionBar(binding.toolbar, getString(R.string.title_activity_about)) {
            requireActivity().finish()
        }

        setupEdgeToEdge(
            topInsetsView = binding.appBar,
        )

        setUpView()
        enableAd()
    }

    override fun onDestroyView() {
        adView?.destroy()
        adView = null
        super.onDestroyView()
    }

    /**
     * A standalone 300x250 placement gated only by enable_ads — separate from the
     * enable_inline_ads experiment running on History and Settings.
     */
    private fun enableAd() {
        googleMobileAdsConsentManager =
            GoogleMobileAdsConsentManager.getInstance(requireContext().applicationContext)
        val remoteConfig = Firebase.remoteConfig
        val shouldEnableAds = remoteConfig["enable_ads"].asBoolean()
        if (!shouldEnableAds) {
            logD("disabling ads due to remote config")
            logEvent(ADS_DISABLED) {
                param("reason", "ads_disabled")
            }
            return
        }
        val adUnitId = remoteConfig[ABOUT_AD_ID].asString()
        if (adUnitId.isEmpty()) {
            logD("disabling ads due to empty ad unit id")
            logEvent(ADS_DISABLED) {
                param("reason", "empty_ad_unit")
            }
            return
        }

        if (googleMobileAdsConsentManager.canRequestAds) {
            val newAdView = createInlineAdView(
                requireContext(),
                adUnitId,
                getClassName()
            ) {
                binding.inlineAdViewContainer.visible(true)
            }
            binding.inlineAdViewContainer.addView(newAdView)
            adView = newAdView
            logEvent(ADS_ENABLED)
        }
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