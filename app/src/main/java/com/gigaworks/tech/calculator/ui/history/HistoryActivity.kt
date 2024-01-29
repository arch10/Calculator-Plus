package com.gigaworks.tech.calculator.ui.history

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
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
import com.gigaworks.tech.calculator.util.GoogleMobileAdsConsentManager
import com.gigaworks.tech.calculator.util.SHARE_EXPRESSION
import com.gigaworks.tech.calculator.util.logD
import com.gigaworks.tech.calculator.util.visible
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.get
import com.google.firebase.remoteconfig.remoteConfig
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryActivity : BaseActivity<ActivityHistoryBinding>() {

    private val viewModel by viewModels<HistoryViewModel>()
    private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.toolbar)

        setupView()
        setupObservables()

        // enable Google ads
        enableAds()
    }

    private fun enableAds() {
        googleMobileAdsConsentManager =
            GoogleMobileAdsConsentManager.getInstance(applicationContext)
        val remoteConfig = Firebase.remoteConfig
        val shouldEnableAds = remoteConfig["enable_ads"].asBoolean()
        if (!shouldEnableAds) {
            logD("disabling ads due to remote config")
            logEvent(ADS_DISABLED)
            return
        }
        //test ad unit id - uncomment below line to enable test ads
        //val adUnitId = "ca-app-pub-3940256099942544/6300978111"
        val adUnitId = remoteConfig["history_ad_id"].asString()
        if (adUnitId.isEmpty()) {
            logD("disabling ads due to empty ad unit id")
            logEvent(ADS_DISABLED)
            return
        }
        if (googleMobileAdsConsentManager.canRequestAds) {
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
            adView.loadAd(adRequest)
            logEvent(ADS_ENABLED)
        }

    }

    private fun setupObservables() {
        viewModel.historyList.observe(this) { historyList ->
            if (historyList != null && historyList.isNotEmpty()) {
                binding.noHistory.visible(false)
                binding.rv.visible(true)
                val list = viewModel.transformHistory(historyList.map { it.toDomain() })
                val adapter = HistoryAdapter(list, object : HistoryAdapter.OnHistoryClickListener {
                    override fun onHistoryClick(history: HistoryAdapterItem) {
                        viewModel.saveExpression(removeNumberSeparator(history.expression))
                        finish()
                    }
                })
                binding.rv.adapter = adapter
            } else {
                binding.rv.visible(false)
                binding.noHistory.visible(true)
            }
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
        binding.toolbar.setNavigationOnClickListener { handleBackPress() }
    }

    override fun onBackPressed() {
        handleBackPress()
    }

    private fun handleBackPress() {
        finish()
    }

    override fun getViewBinding(inflater: LayoutInflater) = ActivityHistoryBinding.inflate(inflater)

}