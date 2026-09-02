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
import com.gigaworks.tech.calculator.util.ADS_DISABLED
import com.gigaworks.tech.calculator.util.ADS_ENABLED
import com.gigaworks.tech.calculator.util.GoogleMobileAdsConsentManager
import com.gigaworks.tech.calculator.util.JOIN_BETA
import com.gigaworks.tech.calculator.util.getClassName
import com.gigaworks.tech.calculator.util.logAdSource
import com.gigaworks.tech.calculator.util.logD
import com.gigaworks.tech.calculator.util.visible
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
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
        enableBannerAd()
    }

    override fun onDestroyView() {
        adView?.destroy()
        adView = null
        super.onDestroyView()
    }

    private fun enableBannerAd() {
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
        //test ad unit id - uncomment below line to enable test ads
        //val adUnitId = "ca-app-pub-3940256099942544/6300978111"
        val adUnitId = remoteConfig["about_ad_id"].asString()
        if (adUnitId.isEmpty()) {
            logD("disabling ads due to empty ad unit id")
            logEvent(ADS_DISABLED) {
                param("reason", "empty_ad_unit")
            }
            return
        }

        if (googleMobileAdsConsentManager.canRequestAds) {
            binding.adViewContainer.visible(true)
            val newAdView = AdView(requireContext())
            newAdView.setAdSize(AdSize.BANNER)
            newAdView.adUnitId = adUnitId
            binding.adViewContainer.addView(newAdView)
            newAdView.adListener = object : AdListener() {
                override fun onAdLoaded() {
                    newAdView.responseInfo.logAdSource(getClassName())
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    logD("ad failed to load: ${error.message}")
                    error.responseInfo.logAdSource(getClassName())
                }
            }
            newAdView.loadAd(AdRequest.Builder().build())
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