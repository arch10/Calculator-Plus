package com.gigaworks.tech.calculator.ui.history

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.viewModels
import com.gigaworks.tech.calculator.R
import com.gigaworks.tech.calculator.cache.model.toDomain
import com.gigaworks.tech.calculator.databinding.ActivityHistoryBinding
import com.gigaworks.tech.calculator.domain.HistoryAdapterItem
import com.gigaworks.tech.calculator.ui.base.BaseActivity
import com.gigaworks.tech.calculator.ui.history.adapter.HistoryAdapter
import com.gigaworks.tech.calculator.ui.history.viewmodel.HistoryViewModel
import com.gigaworks.tech.calculator.ui.main.helper.removeNumberSeparator
import com.gigaworks.tech.calculator.util.ADS_DISABLED
import com.gigaworks.tech.calculator.util.ADS_ENABLED
import com.gigaworks.tech.calculator.util.AdPlacement
import com.gigaworks.tech.calculator.util.GoogleMobileAdsConsentManager
import com.gigaworks.tech.calculator.util.HISTORY_AD_ID
import com.gigaworks.tech.calculator.util.HISTORY_INLINE_AD_ID
import com.gigaworks.tech.calculator.util.INLINE_ADS_ENABLED
import com.gigaworks.tech.calculator.util.SHARE_EXPRESSION
import com.gigaworks.tech.calculator.util.createInlineAdView
import com.gigaworks.tech.calculator.util.getClassName
import com.gigaworks.tech.calculator.util.logAdSource
import com.gigaworks.tech.calculator.util.logD
import com.gigaworks.tech.calculator.util.resolveAdPlacement
import com.gigaworks.tech.calculator.util.visible
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.get
import com.google.firebase.remoteconfig.remoteConfig
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryActivity : BaseActivity<ActivityHistoryBinding>() {

    private val viewModel by viewModels<HistoryViewModel>()
    private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager
    private var inlineAdView: AdView? = null
    private var isInlineAdLoaded = false

    // defaults to false to match the layout's default state (rv visible, noHistory gone)
    // until the first historyList emission says otherwise
    private var isHistoryEmpty = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.toolbar)

        setupView()
        // enable Google ads before the observables so the list adapter can host the inline ad
        enableAds()
        setupObservables()
        setupEdgeToEdge(
            topInsetsView = binding.appBar,
            bottomInsetsView = binding.root
        )
    }

    override fun onDestroy() {
        inlineAdView?.destroy()
        inlineAdView = null
        super.onDestroy()
    }

    private fun enableAds() {
        googleMobileAdsConsentManager =
            GoogleMobileAdsConsentManager.getInstance(applicationContext)

//        val allowDisablingAds = Firebase.remoteConfig["allow_disabling_ads"].asBoolean()
//        val localDisableAds = viewModel.getDisableAds()
//        logD("allowDisablingAds=$allowDisablingAds, localDisableAds=$localDisableAds")
//        if (allowDisablingAds && localDisableAds) {
//            logD("disabling ads due to user setting")
//            logEvent(ADS_DISABLED)
//            return
//        }

        //test ad unit id - pass "ca-app-pub-3940256099942544/6300978111" below to enable test ads
        when (val placement = resolveAdPlacement(HISTORY_INLINE_AD_ID, HISTORY_AD_ID)) {
            is AdPlacement.Inline -> showInlineAd(placement.adUnitId)
            is AdPlacement.BottomBanner -> showBottomBannerAd(placement.adUnitId)
            is AdPlacement.None -> {
                logD("no ad shown: ${placement.reason}")
                logEvent(ADS_DISABLED) {
                    param("reason", placement.reason)
                }
            }
        }
    }

    private fun showBottomBannerAd(adUnitId: String) {
        if (!googleMobileAdsConsentManager.canRequestAds) {
            return
        }
        binding.rv.layoutParams = binding.rv.layoutParams.apply {
            (this as ViewGroup.MarginLayoutParams).bottomMargin =
                resources.getDimensionPixelSize(R.dimen.banner_ad_height)
        }
        binding.adViewContainer.visible(true)
        val adRequest = AdRequest.Builder().build()
        val adView = AdView(this)
        adView.setAdSize(AdSize.BANNER)
        adView.adUnitId = adUnitId
        binding.adViewContainer.addView(adView)
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                adView.responseInfo.logAdSource(getClassName())
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                logD("ad failed to load: ${error.message}")
                error.responseInfo.logAdSource(getClassName())
            }
        }
        adView.loadAd(adRequest)
        logEvent(ADS_ENABLED)
    }

    private fun showInlineAd(adUnitId: String) {
        if (!googleMobileAdsConsentManager.canRequestAds) {
            return
        }
        inlineAdView = createInlineAdView(this, adUnitId, getClassName()) { adView ->
            isInlineAdLoaded = true
            attachInlineAd(adView)
        }
        logEvent(INLINE_ADS_ENABLED)
    }

    /**
     * Routes the loaded inline ad to wherever it currently belongs: a real row in the
     * history list, or the empty-state view when there is no history to scroll through.
     * Called both when the ad finishes loading and whenever the list flips between empty
     * and non-empty (e.g. the user clears history while this screen is open), since either
     * event can happen first.
     */
    private fun attachInlineAd(adView: AdView) {
        if (isHistoryEmpty) {
            (adView.parent as? ViewGroup)?.removeView(adView)
            binding.noHistoryAdContainer.removeAllViews()
            binding.noHistoryAdContainer.addView(adView)
            binding.noHistoryAdContainer.visible(true)
        } else {
            // posted so the row is never inserted while the RecyclerView is laying out
            binding.rv.post {
                (binding.rv.adapter as? HistoryAdapter)?.showInlineAd(adView)
            }
        }
    }

    private fun setupObservables() {
        viewModel.historyList.observe(this) { historyList ->
            isHistoryEmpty = historyList.isNullOrEmpty()
            if (!isHistoryEmpty) {
                binding.noHistory.visible(false)
                binding.rv.visible(true)
                val list = viewModel.transformHistory(historyList!!.map { it.toDomain() })
                val adapter = HistoryAdapter(
                    list,
                    object : HistoryAdapter.OnHistoryClickListener {
                        override fun onHistoryClick(history: HistoryAdapterItem) {
                            viewModel.saveExpression(removeNumberSeparator(history.expression))
                            finish()
                        }
                    },
                    inlineAdView.takeIf { isInlineAdLoaded }
                )
                binding.rv.adapter = adapter
            } else {
                binding.rv.visible(false)
                binding.noHistory.visible(true)
            }
            inlineAdView.takeIf { isInlineAdLoaded }?.let { attachInlineAd(it) }
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            101 -> {
                val position = item.groupId
                val history = (binding.rv.adapter as HistoryAdapter).getHistory(position)
                viewModel.deleteHistory(history.expression)
                true
            }

            102 -> {
                val position = item.groupId
                val history = (binding.rv.adapter as HistoryAdapter).getHistory(position)
                val sharedEquation = "${history.expression} = ${history.result}"
                logEvent(SHARE_EXPRESSION)
                startActivity(
                    Intent.createChooser(
                        Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "Calculator Plus Expression")
                            putExtra(Intent.EXTRA_TEXT, sharedEquation)
                        },
                        getString(R.string.choose)
                    )
                )
                true
            }

            else -> super.onContextItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.history_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.history_trash -> viewModel.clearHistory()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupView() {
        binding.toolbar.setNavigationOnClickListener { finish() }
        // Add back press callback
        onBackPressedDispatcher.addCallback(this) {
            finish()
        }
    }

    override fun getViewBinding(inflater: LayoutInflater) = ActivityHistoryBinding.inflate(inflater)

}