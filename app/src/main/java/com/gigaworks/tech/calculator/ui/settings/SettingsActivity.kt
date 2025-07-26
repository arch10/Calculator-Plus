package com.gigaworks.tech.calculator.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import com.gigaworks.tech.calculator.BuildConfig
import com.gigaworks.tech.calculator.R
import com.gigaworks.tech.calculator.databinding.ActivitySettingsBinding
import com.gigaworks.tech.calculator.databinding.ColorDialogBinding
import com.gigaworks.tech.calculator.databinding.PrecisionDialogBinding
import com.gigaworks.tech.calculator.ui.about.AboutActivity
import com.gigaworks.tech.calculator.ui.base.BaseActivity
import com.gigaworks.tech.calculator.ui.main.MainActivity
import com.gigaworks.tech.calculator.ui.settings.helper.getString
import com.gigaworks.tech.calculator.ui.settings.viewmodel.SettingsViewModel
import com.gigaworks.tech.calculator.util.ADS_DISABLED
import com.gigaworks.tech.calculator.util.ADS_ENABLED
import com.gigaworks.tech.calculator.util.AccentTheme
import com.gigaworks.tech.calculator.util.AppPreference
import com.gigaworks.tech.calculator.util.CHANGE_ACCENT_COLOR
import com.gigaworks.tech.calculator.util.CHANGE_DISABLE_ADS
import com.gigaworks.tech.calculator.util.CHANGE_HISTORY_DELETE
import com.gigaworks.tech.calculator.util.CHANGE_NUMBER_SEPARATOR
import com.gigaworks.tech.calculator.util.CHANGE_PRECISION
import com.gigaworks.tech.calculator.util.CHANGE_SMART_CALCULATION
import com.gigaworks.tech.calculator.util.CHANGE_THEME
import com.gigaworks.tech.calculator.util.CLICK_ABOUT
import com.gigaworks.tech.calculator.util.FOLLOW_ME
import com.gigaworks.tech.calculator.util.GoogleMobileAdsConsentManager
import com.gigaworks.tech.calculator.util.HistoryAutoDelete
import com.gigaworks.tech.calculator.util.NumberSeparator
import com.gigaworks.tech.calculator.util.RATE_APP
import com.gigaworks.tech.calculator.util.REPORT_PROBLEM
import com.gigaworks.tech.calculator.util.SEND_FEEDBACK
import com.gigaworks.tech.calculator.util.SHARE_APP
import com.gigaworks.tech.calculator.util.TRIGGER_STORE_FEEDBACK
import com.gigaworks.tech.calculator.util.capitalize
import com.gigaworks.tech.calculator.util.getAccentTheme
import com.gigaworks.tech.calculator.util.logD
import com.gigaworks.tech.calculator.util.logE
import com.gigaworks.tech.calculator.util.visible
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.get
import com.google.firebase.remoteconfig.remoteConfig
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class SettingsActivity : BaseActivity<ActivitySettingsBinding>() {

    private val themeItems by lazy {
        arrayOf(
            getString(R.string.system_default),
            getString(R.string.light),
            getString(R.string.dark)
        )
    }

    private val viewModel by viewModels<SettingsViewModel>()
    private var dialog: AlertDialog? = null
    private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager

    // Declaring sensorManager
    // and acceleration constants
//    private val sensorManager by lazy { getSystemService(Context.SENSOR_SERVICE) as SensorManager }
//    private var acceleration = 10f
//    private var currentAcceleration = SensorManager.GRAVITY_EARTH
//    private var lastAcceleration = SensorManager.GRAVITY_EARTH

    override fun onCreate(savedInstanceState: Bundle?) {
        val appPreference = AppPreference(this)
        val accentTheme =
            appPreference.getStringPreference(AppPreference.ACCENT_THEME, AccentTheme.BLUE.name)
        setTheme(getAccentTheme(accentTheme))
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.toolbar)

        setUpView()
        setUpObservables()
        setupEdgeToEdge(
            topInsetsView = binding.appBar,
            bottomInsetsView = binding.root
        )

        // enable Google ads
        enableAds()
    }

//    private val sensorListener: SensorEventListener = object : SensorEventListener {
//        override fun onSensorChanged(event: SensorEvent) {
//
//            // Fetching x,y,z values
//            val x = event.values[0]
//            val y = event.values[1]
//            val z = event.values[2]
//            lastAcceleration = currentAcceleration
//
//            // Getting current accelerations
//            // with the help of fetched x,y,z values
//            currentAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
//            val delta = currentAcceleration - lastAcceleration
//            acceleration = acceleration * 0.9f + delta
//
//            // Display a Toast message if
//            // acceleration value is over 60
//            if (acceleration > 60) {
//                logD("Acceleration: $acceleration")
//                Toast.makeText(applicationContext, "Hidden setting available", Toast.LENGTH_SHORT)
//                    .show()
//                logEvent(HIDDEN_SETTINGS_ENABLED)
//                binding.hiddenSettings.visible(true)
//            }
//        }
//
//        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
//    }
//
//    override fun onResume() {
//        sensorManager.registerListener(
//            sensorListener,
//            sensorManager.getDefaultSensor(
//                Sensor.TYPE_ACCELEROMETER
//            ),
//            SensorManager.SENSOR_DELAY_NORMAL
//        )
//        super.onResume()
//    }
//
//    override fun onPause() {
//        sensorManager.unregisterListener(sensorListener)
//        super.onPause()
//    }

    private fun enableAds() {
        googleMobileAdsConsentManager =
            GoogleMobileAdsConsentManager.getInstance(applicationContext)
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
        val adUnitId = remoteConfig["settings_ad_id"].asString()
        if (adUnitId.isEmpty()) {
            logD("disabling ads due to empty ad unit id")
            logEvent(ADS_DISABLED) {
                param("reason", "empty_ad_unit")
            }
            return
        }

//        val allowDisablingAds = remoteConfig["allow_disabling_ads"].asBoolean()
//        val localDisableAds = viewModel.getDisableAds()
//        logD("allowDisablingAds=$allowDisablingAds, localDisableAds=$localDisableAds")
//        if (allowDisablingAds && localDisableAds) {
//            logD("disabling ads due to user setting")
//            logEvent(ADS_DISABLED)
//            return
//        }

        if (googleMobileAdsConsentManager.canRequestAds) {
            binding.profileView.layoutParams = binding.profileView.layoutParams.apply {
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

    private fun setUpObservables() {
        viewModel.selectedTheme.observe(this) {
            binding.themeSubtitle.text = themeItems[it.ordinal]
        }
        viewModel.smartCalculation.observe(this) {
            binding.smartCalculationSwitch.isChecked = it
        }
        viewModel.numberSeparator.observe(this) {
            binding.numberSeparatorSubtitle.text =
                it.name.lowercase(Locale.ROOT).capitalize()
        }
        viewModel.autoDeleteHistory.observe(this) {
            binding.deleteHistorySubtitle.text = it.getString()
        }
        viewModel.precision.observe(this) {
            val precisionSubtitle = "${getString(R.string.precision)}: $it"
            binding.precisionSubtitle.text = precisionSubtitle
        }
        viewModel.accentTheme.observe(this) {
            val accentTheme = it.name.lowercase().capitalize()
            binding.colorSubtitle.text = accentTheme
        }
        viewModel.disableAds.observe(this) {
            binding.disableAdsSwitch.isChecked = it
        }
    }

    private fun setUpView() {
        if (viewModel.shouldAskUserRating()) {
            logEvent(TRIGGER_STORE_FEEDBACK)
            askUserRating()
        }

        binding.toolbar.setNavigationOnClickListener { finish() }

        val colorDialog = ColorDialogBinding.inflate(layoutInflater, null, false)
        colorDialog.colorDefault.setOnClickListener {
            checkAccentTheme(AccentTheme.BLUE, colorDialog)
        }
        colorDialog.colorGreen.setOnClickListener {
            checkAccentTheme(AccentTheme.GREEN, colorDialog)
        }
        colorDialog.colorPurple.setOnClickListener {
            checkAccentTheme(AccentTheme.PURPLE, colorDialog)
        }
        colorDialog.colorPink.setOnClickListener {
            checkAccentTheme(AccentTheme.PINK, colorDialog)
        }
        colorDialog.colorRed.setOnClickListener {
            checkAccentTheme(AccentTheme.RED, colorDialog)
        }
        colorDialog.colorGrey.setOnClickListener {
            checkAccentTheme(AccentTheme.GREY, colorDialog)
        }
        binding.colorCard.setOnClickListener {
            if (colorDialog.root.parent != null) {
                ((colorDialog.root.parent) as ViewGroup).removeView(colorDialog.root)
            }
            checkAccentTheme(viewModel.selectedAccentTheme, colorDialog)
            dialog = MaterialAlertDialogBuilder(this)
                .setView(colorDialog.root)
                .setTitle(getString(R.string.select_accent_color))
                .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                    viewModel.setAccentTheme(viewModel.selectedAccentTheme)
                    logEvent(
                        CHANGE_ACCENT_COLOR,
                        bundleOf("value" to viewModel.selectedAccentTheme.name)
                    )
                    dialog.dismiss()
                    val intent = arrayOfNulls<Intent>(2)
                    intent[1] = Intent(this, SettingsActivity::class.java)
                    intent[0] = Intent(
                        this,
                        MainActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivities(intent)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        overrideActivityTransition(
                            OVERRIDE_TRANSITION_OPEN,
                            android.R.anim.fade_in,
                            android.R.anim.fade_out
                        )
                    } else {
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    }
                    finish()
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }.show()
        }

        val appVersion = "${getString(R.string.version)}: ${BuildConfig.VERSION_NAME}"
        binding.aboutSubtitle.text = appVersion

        binding.themeCard.setOnClickListener {
            var selectedThemeChoice = viewModel.selectedTheme.value!!.ordinal
            dialog = MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.choose_theme))
                .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                    viewModel.changeTheme(selectedThemeChoice)
                    val themeName = viewModel.getAppThemeByOrdinal(selectedThemeChoice)
                    logEvent(CHANGE_THEME, bundleOf("value" to themeName))
                    dialog.dismiss()
                }
                .setSingleChoiceItems(
                    themeItems,
                    viewModel.selectedTheme.value!!.ordinal
                ) { _, which ->
                    selectedThemeChoice = which
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }.show()
        }

        binding.smartCalculationCard.setOnClickListener {
            val currentValue = viewModel.getSmartCalculation()
            logEvent(CHANGE_SMART_CALCULATION, bundleOf("value" to !currentValue))
            viewModel.setSmartCalculation(!currentValue)
        }
        binding.smartCalculationSwitch.setOnCheckedChangeListener { _, isChecked ->
            logEvent(CHANGE_SMART_CALCULATION, bundleOf("value" to isChecked))
            viewModel.setSmartCalculation(isChecked)
        }

        binding.numberSeparatorCard.setOnClickListener {
            var numberSeparator = viewModel.getNumberSeparator()
            val list =
                NumberSeparator.entries
                    .map { it.name.lowercase(Locale.ROOT).capitalize() }
                    .toTypedArray()
            dialog = MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.choose_number_separator))
                .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                    viewModel.changeNumberSeparator(numberSeparator)
                    logEvent(CHANGE_NUMBER_SEPARATOR, bundleOf("value" to numberSeparator.name))
                    dialog.dismiss()
                }
                .setSingleChoiceItems(
                    list,
                    viewModel.numberSeparator.value!!.ordinal
                ) { _, which ->
                    numberSeparator = NumberSeparator.entries.find { it.ordinal == which }
                        ?: NumberSeparator.INTERNATIONAL
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }.show()
        }

        binding.deleteHistoryCard.setOnClickListener {
            var deleteHistory = viewModel.getAutoDeleteHistory()
            val list =
                HistoryAutoDelete.entries.map { it.getString() }.toTypedArray()
            dialog = MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.delete_history))
                .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                    viewModel.setAutoDeleteHistory(deleteHistory)
                    logEvent(CHANGE_HISTORY_DELETE, bundleOf("value" to deleteHistory.days))
                    dialog.dismiss()
                }
                .setSingleChoiceItems(
                    list,
                    viewModel.autoDeleteHistory.value!!.ordinal
                ) { _, which ->
                    deleteHistory = HistoryAutoDelete.entries.find { it.ordinal == which }
                        ?: HistoryAutoDelete.NEVER
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }.show()
        }

        binding.precisionCard.setOnClickListener {
            val precision = viewModel.getAnswerPrecision()
            val precisionDialogLayout = PrecisionDialogBinding.inflate(layoutInflater, null, false)
            precisionDialogLayout.precisionSlider.value = precision.toFloat()
            dialog = MaterialAlertDialogBuilder(this)
                .setView(precisionDialogLayout.root)
                .setTitle(getString(R.string.set_answer_precision))
                .setMessage("${getString(R.string.current_precision)}: $precision")
                .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                    val newPrecision = precisionDialogLayout.precisionSlider.value.toInt()
                    viewModel.setAnswerPrecision(newPrecision)
                    logEvent(CHANGE_PRECISION, bundleOf("value" to newPrecision))
                    dialog.dismiss()
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }.show()
        }

        binding.shareCard.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                val msg = StringBuilder()
                msg.append("Hey,\n\n")
                msg.append("Calculator Plus is a Smart, Reliable and Light weight app that I often use.")
                msg.append(" It is free and has no Ads whatsoever.\n\n")
                msg.append("Download it from $DOWNLOAD_LINK")
                putExtra(Intent.EXTRA_TEXT, msg.toString())
            }
            logEvent(SHARE_APP)
            startActivity(Intent.createChooser(intent, getString(R.string.choose)))
        }

        binding.bugCard.setOnClickListener {
            logEvent(REPORT_PROBLEM)
            startActivity(Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(REPORT_BUG_LINK) })
        }

        binding.rateCard.setOnClickListener {
            logEvent(RATE_APP)
            startActivity(Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(APP_STORE_LINK) })
        }

        binding.contactCard.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                val body = StringBuilder()
                body.append("\n")
                body.append("-----------------------------------------\n")
                body.append("App version: ${BuildConfig.VERSION_NAME}\n")
                body.append("Device: ${Build.MANUFACTURER} - ${Build.MODEL} (${Build.DEVICE})\n")
                body.append("Screen density: ${resources.displayMetrics.densityDpi}\n")
                body.append("-----------------------------------------\n\n")
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(MAILING_ADDRESS))
                putExtra(
                    Intent.EXTRA_SUBJECT, "Calculator Plus Feedback"
                )
                putExtra(Intent.EXTRA_TEXT, body.toString())
            }
            if (intent.resolveActivity(packageManager) != null) {
                logEvent(SEND_FEEDBACK)
                startActivity(intent)
            }
        }

        binding.followCard.setOnClickListener {
            logEvent(FOLLOW_ME)
            startActivity(Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(FOLLOW_LINK) })
        }

        binding.aboutCard.setOnClickListener {
            logEvent(CLICK_ABOUT)
            startActivity(Intent(this, AboutActivity::class.java))
        }

        binding.disableAdsCard.setOnClickListener {
            val currentValue = viewModel.disableAds.value!!
            logEvent(CHANGE_DISABLE_ADS, bundleOf("value" to currentValue))
            viewModel.setDisableAds(!currentValue)
        }

        binding.disableAdsSwitch.setOnCheckedChangeListener { _, isChecked ->
            logEvent(CHANGE_DISABLE_ADS, bundleOf("value" to isChecked))
            viewModel.setDisableAds(isChecked)
        }
    }

    private fun checkAccentTheme(accentTheme: AccentTheme, colorDialog: ColorDialogBinding) {
        colorDialog.defaultCheck.visible(false)
        colorDialog.greenCheck.visible(false)
        colorDialog.purpleCheck.visible(false)
        colorDialog.pinkCheck.visible(false)
        colorDialog.redCheck.visible(false)
        colorDialog.greyCheck.visible(false)
        when (accentTheme) {
            AccentTheme.BLUE -> {
                colorDialog.defaultCheck.visible(true)
            }

            AccentTheme.GREEN -> {
                colorDialog.greenCheck.visible(true)
            }

            AccentTheme.PURPLE -> {
                colorDialog.purpleCheck.visible(true)
            }

            AccentTheme.PINK -> {
                colorDialog.pinkCheck.visible(true)
            }

            AccentTheme.RED -> {
                colorDialog.redCheck.visible(true)
            }

            AccentTheme.GREY -> {
                colorDialog.greyCheck.visible(true)
            }
        }
        viewModel.selectedAccentTheme = accentTheme
    }

    private fun askUserRating() {
        val manager = ReviewManagerFactory.create(this)
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                val flow = manager.launchReviewFlow(this, reviewInfo)
                flow.addOnCompleteListener { flowTask ->
                    if (flowTask.isComplete) {
                        logD("Review process complete")
                    }
                }
            } else {
                logE("Failed to initiate user review. ${task.exception?.message}")
            }
        }
    }


    override fun getViewBinding(inflater: LayoutInflater) =
        ActivitySettingsBinding.inflate(inflater)

    companion object {
        const val DOWNLOAD_LINK =
            "https://play.google.com/store/apps/details?id=com.gigaworks.tech.calculator"
        const val REPORT_BUG_LINK =
            "https://github.com/arch10/Calculator-Plus/issues/new?assignees=&labels=&template=bug_report.md"
        const val APP_STORE_LINK =
            "https://play.google.com/store/apps/details?id=com.gigaworks.tech.calculator"
        const val MAILING_ADDRESS = "arch1824@gmail.com"
        const val FOLLOW_LINK = "https://twitter.com/arch1006"
    }
}