package com.gigaworks.tech.calculator.ui.main

import ai.geemee.GeeMee
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.bundleOf
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.gigaworks.tech.calculator.R
import com.gigaworks.tech.calculator.databinding.ActivityMainBinding
import com.gigaworks.tech.calculator.domain.History
import com.gigaworks.tech.calculator.ui.about.AboutActivity
import com.gigaworks.tech.calculator.ui.base.BaseActivity
import com.gigaworks.tech.calculator.ui.history.HistoryActivity
import com.gigaworks.tech.calculator.ui.main.helper.addNumberSeparator
import com.gigaworks.tech.calculator.ui.main.helper.handleClick
import com.gigaworks.tech.calculator.ui.main.helper.handleConstantClick
import com.gigaworks.tech.calculator.ui.main.helper.handleDelete
import com.gigaworks.tech.calculator.ui.main.helper.isNumber
import com.gigaworks.tech.calculator.ui.main.helper.removeNumberSeparator
import com.gigaworks.tech.calculator.ui.main.viewmodel.MainViewModel
import com.gigaworks.tech.calculator.ui.settings.SettingsActivity
import com.gigaworks.tech.calculator.ui.view.CalculatorEditText
import com.gigaworks.tech.calculator.util.ADS_DISABLED
import com.gigaworks.tech.calculator.util.ADS_ENABLED
import com.gigaworks.tech.calculator.util.AccentTheme
import com.gigaworks.tech.calculator.util.AngleType
import com.gigaworks.tech.calculator.util.AppPreference
import com.gigaworks.tech.calculator.util.AppTheme
import com.gigaworks.tech.calculator.util.CHANGE_ANGLE
import com.gigaworks.tech.calculator.util.CLICK_ABOUT
import com.gigaworks.tech.calculator.util.CLICK_CLEAR
import com.gigaworks.tech.calculator.util.CLICK_HISTORY
import com.gigaworks.tech.calculator.util.CLICK_MEMORY
import com.gigaworks.tech.calculator.util.CLICK_PRIVACY_SETTINGS
import com.gigaworks.tech.calculator.util.CLICK_SETTINGS
import com.gigaworks.tech.calculator.util.CLICK_TUTORIAL
import com.gigaworks.tech.calculator.util.EVALUATE
import com.gigaworks.tech.calculator.util.GEEMEE_AD_CLOSE
import com.gigaworks.tech.calculator.util.GEEMEE_AD_OPEN
import com.gigaworks.tech.calculator.util.GEEMEE_ENABLED
import com.gigaworks.tech.calculator.util.GEEMEE_INIT_SUCCESS
import com.gigaworks.tech.calculator.util.GeeMeeCallbackListener
import com.gigaworks.tech.calculator.util.GoogleMobileAdsConsentManager
import com.gigaworks.tech.calculator.util.NumberSeparator
import com.gigaworks.tech.calculator.util.SHARE_EXPRESSION
import com.gigaworks.tech.calculator.util.getAccentTheme
import com.gigaworks.tech.calculator.util.logD
import com.gigaworks.tech.calculator.util.logE
import com.gigaworks.tech.calculator.util.visible
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.get
import com.google.firebase.remoteconfig.remoteConfig
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.sqrt


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val viewModel: MainViewModel by viewModels()
    private var mCurrentAnimator: Animator? = null
    private val isMobileAdsInitializeCalled = AtomicBoolean(false)
    private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager
    private val geemeePlacementId = "13553"
    private val geemeeAppKey = "7OKwz38pamGtKmgUeRoYDqRUVtCYb1WH"

    override fun onCreate(savedInstanceState: Bundle?) {
        val appPreference = AppPreference(this)
        val accentTheme =
            appPreference.getStringPreference(AppPreference.ACCENT_THEME, AccentTheme.BLUE.name)
        setTheme(getAccentTheme(accentTheme))
        super.onCreate(savedInstanceState)
        setupActionBar(binding.toolbar)

        setupView()
        setupObservers()
        setClickListener()
        setAppTheme()

        // Add onBackPressedDispatcher callback
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        // setup remote config
        setupRemoteConfig()

        // setup geemee listener
        setGeeMeeCallbackListener()

        // enable Google ads
        enableAds()

    }

    private fun initializeMobileAdsSdk(adUnitId: String) {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return
        }
        val remoteConfig = Firebase.remoteConfig
        val enableGeeMee = remoteConfig["enable_geemee_ads"].asBoolean()
        if (enableGeeMee) {
            logEvent(GEEMEE_ENABLED)
            GeeMee.initSDK(geemeeAppKey)
            binding.geemeBtn.setImageResource(R.drawable.gift_100x100)
            binding.geemeBtn.visible(true)
            binding.geemeBtn.setOnClickListener {
                if (GeeMee.isInteractiveReady(geemeePlacementId)) {
                    GeeMee.showInterstitial(geemeePlacementId)
                }
            }
        }
        MobileAds.initialize(this) {}
        logD("Consent granted: ${googleMobileAdsConsentManager.canRequestAds}")
        val adRequest = AdRequest.Builder().build()
        val adView = AdView(this)
        adView.setAdSize(AdSize.BANNER)
        adView.adUnitId = adUnitId
        binding.adViewContainer.addView(adView)
        adView.loadAd(adRequest)
        logEvent(ADS_ENABLED)
    }

    private fun setGeeMeeCallbackListener() {
        GeeMee.setCallback(object : GeeMeeCallbackListener {
            override fun onInitSuccess() {
                super.onInitSuccess()
                logEvent(GEEMEE_INIT_SUCCESS)
            }

            override fun onInterstitialOpen(p0: String?) {
                super.onInterstitialOpen(p0)
                logEvent(GEEMEE_AD_OPEN)
            }

            override fun onInterstitialClose(p0: String?) {
                super.onInterstitialClose(p0)
                logEvent(GEEMEE_AD_CLOSE)
            }
        })
    }

    private fun enableAds() {
        googleMobileAdsConsentManager =
            GoogleMobileAdsConsentManager.getInstance(applicationContext)
        val remoteConfig = Firebase.remoteConfig
        val shouldEnableAds = remoteConfig["enable_ads"].asBoolean()
        logD("enable_ads=$shouldEnableAds")
        if (!shouldEnableAds) {
            logD("disabling ads due to remote config")
            logEvent(ADS_DISABLED) {
                param("reason", "ads_disabled")
            }
            return
        }
        //test ad unit id - uncomment below line to enable test ads
        //val adUnitId = "ca-app-pub-3940256099942544/6300978111"
        val adUnitId = remoteConfig["main_ad_id"].asString()
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
//            logEvent(ADS_DISABLED) {
//                param("reason", "user_settings")
//            }
//            return
//        }

        logD("Google Mobile Ads SDK Version: ${MobileAds.getVersion()}")
        googleMobileAdsConsentManager.gatherConsent(this) { error ->
            if (error != null) {
                // Consent not obtained in current session.
                logE("${error.errorCode}: ${error.message}")
            }

            if (googleMobileAdsConsentManager.canRequestAds) {
                initializeMobileAdsSdk(adUnitId)
            }

            if (googleMobileAdsConsentManager.isPrivacyOptionsRequired) {
                // Regenerate the options menu to include a privacy setting.
                invalidateOptionsMenu()
            }
        }

        if (googleMobileAdsConsentManager.canRequestAds) {
            initializeMobileAdsSdk(adUnitId)
        }

    }

    private fun setupRemoteConfig() {
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        remoteConfig.activate()
        remoteConfig.fetchAndActivate().addOnCompleteListener(this) {
            if (it.isSuccessful) {
                logD("Remote config fetch success")
            } else {
                logD("Remote config fetch failed")
            }
        }
    }

    private val buttonClick = View.OnClickListener {
        it.isHapticFeedbackEnabled = true
        it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        val text = (it as Button).text.toString()
        val expression = removeNumberSeparator(getExpression())
        var newExpression = handleClick(expression, text, viewModel.isPrevResult)
        viewModel.isPrevResult = false
        if (viewModel.getNumberSeparator() != NumberSeparator.OFF) {
            newExpression = addNumberSeparator(
                expression = newExpression,
                isIndian = (viewModel.getNumberSeparator() == NumberSeparator.INDIAN)
            )
        }
        setExpression(newExpression)
    }

    private val textSizeChangeListener =
        CalculatorEditText.OnTextSizeChangeListener { textView, oldSize ->
            // Calculate the values needed to perform the scale and translation animations,
            // maintaining the same apparent baseline for the displayed text.
            val textScale = oldSize / textView.textSize
            val translationX = (1.0f - textScale) *
                    (textView.width / 2.0f - textView.paddingEnd)
            val translationY = (1.0f - textScale) *
                    (textView.height / 2.0f - textView.paddingBottom)
            val animatorSet = AnimatorSet()
            animatorSet.playTogether(
                ObjectAnimator.ofFloat(textView, View.SCALE_X, textScale, 1.0f),
                ObjectAnimator.ofFloat(textView, View.SCALE_Y, textScale, 1.0f),
                ObjectAnimator.ofFloat(textView, View.TRANSLATION_X, translationX, 0.0f),
                ObjectAnimator.ofFloat(textView, View.TRANSLATION_Y, translationY, 0.0f)
            )
            animatorSet.duration =
                resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
            animatorSet.interpolator = AccelerateDecelerateInterpolator()
            animatorSet.start()
        }

    private val expressionChangeListener = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            setResult("")
            getResultEditText().setTextColor(getResultTextColor())
            if (!removeNumberSeparator(s.toString()).isNumber()) {
                viewModel.calculateExpression(s.toString())
            }
        }

    }

    private fun setClickListener() {
        //number Pad
        with(binding.numPad) {
            //first row
            percent.setOnClickListener(buttonClick)
            openBracket.setOnClickListener(buttonClick)
            closeBracket.setOnClickListener(buttonClick)
            //second row
            seven.setOnClickListener(buttonClick)
            eight.setOnClickListener(buttonClick)
            nine.setOnClickListener(buttonClick)
            divide.setOnClickListener(buttonClick)
            //third row
            four.setOnClickListener(buttonClick)
            five.setOnClickListener(buttonClick)
            six.setOnClickListener(buttonClick)
            multiply.setOnClickListener(buttonClick)
            //fourth row
            one.setOnClickListener(buttonClick)
            two.setOnClickListener(buttonClick)
            three.setOnClickListener(buttonClick)
            plus.setOnClickListener(buttonClick)
            //fifth row
            decimal.setOnClickListener(buttonClick)
            zero.setOnClickListener(buttonClick)
            minus.setOnClickListener(buttonClick)
        }

        //scientific Pad
        with(binding.scientificPad) {
            //first row
            sin.setOnClickListener(buttonClick)
            cos.setOnClickListener(buttonClick)
            tan.setOnClickListener(buttonClick)
            //second row
            asin.setOnClickListener(buttonClick)
            acos.setOnClickListener(buttonClick)
            atan.setOnClickListener(buttonClick)
            //third row
            exponential.setOnClickListener(buttonClick)
            log.setOnClickListener(buttonClick)
            naturalLog.setOnClickListener(buttonClick)
            power.setOnClickListener(buttonClick)
            //fourth row
            factorial.setOnClickListener(buttonClick)
            squareRoot.setOnClickListener(buttonClick)
            cubeRoot.setOnClickListener(buttonClick)
            pi.setOnClickListener(buttonClick)
        }

        //delete onClick
        binding.numPad.delete.setOnClickListener {
            it.isHapticFeedbackEnabled = true
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            val expression = removeNumberSeparator(getExpression())
            if (expression.isEmpty()) {
                return@setOnClickListener
            }
            val newExpression = if (viewModel.getNumberSeparator() != NumberSeparator.OFF) {
                addNumberSeparator(
                    expression = handleDelete(expression),
                    isIndian = (viewModel.getNumberSeparator() == NumberSeparator.INDIAN)
                )
            } else {
                handleDelete(expression)
            }
            setExpression(newExpression)
        }

        //delete long click
        binding.numPad.delete.setOnLongClickListener {
            if (getExpression().isNotEmpty()) {
                animateClear()
                logEvent(CLICK_CLEAR)
            }
            true
        }

        //equal onClick
        binding.numPad.equal.setOnClickListener {
            it.isHapticFeedbackEnabled = true
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            val expression = removeNumberSeparator(getExpression())
            val result = getResult()
            if (expression.isNotEmpty()) {
                if (result.isEmpty() || !removeNumberSeparator(result).isNumber()) {
                    val shake = AnimationUtils.loadAnimation(this, R.anim.shake)
                    getResultEditText().setTextColor(getResultTextColor(true))
                    val errorStringId = viewModel.error.value ?: R.string.invalid
                    if (errorStringId == -1) {
                        setResult("")
                    } else {
                        setResult(getString(errorStringId))
                        getResultEditText().startAnimation(shake)
                    }
                } else {
                    val balancedExpression = viewModel.getCalculatedExpression()
                    val history = History(
                        expression = balancedExpression,
                        result = result,
                        date = System.currentTimeMillis()
                    )
                    viewModel.insertHistory(history)
                    viewModel.isPrevResult = true
                    setExpressionAfterEqual(result)
                    logEvent(EVALUATE)
                }
            }
        }

        //memory store click
        binding.scientificPad.memoryStore.setOnClickListener {
            it.isHapticFeedbackEnabled = true
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            val result = removeNumberSeparator(getResult())
            if (result.isNumber()) {
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
                viewModel.setMemory(result)
                logEvent(CLICK_MEMORY)
            }
        }

        //memory restore click
        binding.scientificPad.memoryRestore.setOnClickListener {
            it.isHapticFeedbackEnabled = true
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            val memory = viewModel.getMemory()
            val expression = removeNumberSeparator(getExpression())
            var newExpression = handleConstantClick(expression, memory, viewModel.isPrevResult)
            viewModel.isPrevResult = false
            if (viewModel.getNumberSeparator() != NumberSeparator.OFF) {
                newExpression = addNumberSeparator(
                    expression = newExpression,
                    isIndian = (viewModel.getNumberSeparator() == NumberSeparator.INDIAN)
                )
            }
            setExpression(newExpression)
            logEvent(CLICK_MEMORY)
        }

        binding.scientificPad.memoryAdd.setOnClickListener {
            it.isHapticFeedbackEnabled = true
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            val memory = viewModel.getMemory()
            val result = removeNumberSeparator(getResult())
            if (result.isNumber() && memory.isNumber()) {
                val newMemory = memory.toDouble() + result.toDouble()
                viewModel.setMemory(newMemory.toString())
                logEvent(CLICK_MEMORY)
            }
        }

        binding.scientificPad.memorySub.setOnClickListener {
            it.isHapticFeedbackEnabled = true
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            val memory = viewModel.getMemory()
            val result = removeNumberSeparator(getResult())
            if (result.isNumber() && memory.isNumber()) {
                val newMemory = memory.toDouble() - result.toDouble()
                viewModel.setMemory(newMemory.toString())
                logEvent(CLICK_MEMORY)
            }
        }

        binding.calculatorPadViewPager?.addScientificPadStateChangeListener {
            binding.scientificPad.arrow.animate().rotationBy(180F).setDuration(300).start()
        }

    }

    /**
     * Setup viewModel observers to observe the data change
     * */
    private fun setupObservers() {
        viewModel.result.observe(this) {
            setResult(it)
        }
    }

    /**
     * Setup the views with saved or initial values
     * */
    private fun setupView() {
        viewModel.updateLaunchStatistics()
        binding.resultPad.expression.setOnTextSizeChangeListener(textSizeChangeListener)
        binding.resultPad.expression.addTextChangedListener(expressionChangeListener)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val privacySettings = menu.findItem(R.id.privacy_settings)
        privacySettings?.isVisible = googleMobileAdsConsentManager.isPrivacyOptionsRequired
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.let {
            it.findItem(R.id.angleType).title = viewModel.getAngleType()
        }
        return super.onPrepareOptionsMenu(menu)
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        /**
         * On back pressed, close the scientific pad if it is open
         * else close the app.
         */
        override fun handleOnBackPressed() {
            if (binding.calculatorPadViewPager?.currentItem == 1) {
                binding.calculatorPadViewPager?.currentItem = 0
            } else {
                finish()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> {
                logEvent(CLICK_SETTINGS)
                startActivity(Intent(this, SettingsActivity::class.java))
            }

            R.id.history -> {
                logEvent(CLICK_HISTORY)
                startActivity(Intent(this, HistoryActivity::class.java))
            }

            R.id.about -> {
                logEvent(CLICK_ABOUT)
                startActivity(Intent(this, AboutActivity::class.java))
            }

            R.id.share -> {
                val sharedEquation = getShareEquation()
                if (sharedEquation.isNotEmpty()) {
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
                } else {
                    Toast.makeText(this, getString(R.string.share_error), Toast.LENGTH_SHORT).show()
                }
            }

            R.id.tutorial -> {
                logEvent(CLICK_TUTORIAL)
                showTutorial()
            }

            R.id.privacy_settings -> {
                logEvent(CLICK_PRIVACY_SETTINGS)
                googleMobileAdsConsentManager.showPrivacyOptionsForm(this) { formError ->
                    if (formError != null) {
                        logE("${formError.errorCode}: ${formError.message}")
                        Toast.makeText(this, formError.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showTutorial() {
        //close side panel before starting tutorial
        if (binding.calculatorPadViewPager?.currentItem == 1) {
            binding.calculatorPadViewPager?.currentItem = 0
        }
        val tapTargetSequence = TapTargetSequence(this)
        val delete = TapTarget
            .forView(
                binding.numPad.delete,
                getString(R.string.delete_button),
                getString(R.string.delete_button_desc)
            )
            .outerCircleColor(R.color.primary)
            .outerCircleAlpha(1f)
            .targetCircleColor(R.color.white)
            .titleTextSize(28)
            .tintTarget(false)
            .titleTextColor(R.color.white)
            .descriptionTextColor(R.color.white)
            .descriptionTextSize(18)
            .cancelable(true)
        val angle: TapTarget = TapTarget
            .forToolbarMenuItem(
                binding.toolbar,
                R.id.angleType,
                getString(R.string.angle_button),
                getString(R.string.angle_button_desc)
            )
            .outerCircleColor(R.color.primary)
            .outerCircleAlpha(1f)
            .targetCircleColor(R.color.white)
            .titleTextSize(28)
            .tintTarget(true)
            .titleTextColor(R.color.white)
            .descriptionTextColor(R.color.white)
            .descriptionTextSize(18)
            .cancelable(true)
        val options: TapTarget = TapTarget
            .forToolbarOverflow(
                binding.toolbar,
                getString(R.string.options_menu),
                getString(R.string.options_menu_desc)
            )
            .outerCircleColor(R.color.primary)
            .outerCircleAlpha(1f)
            .targetCircleColor(R.color.white)
            .titleTextSize(28)
            .tintTarget(true)
            .titleTextColor(R.color.white)
            .descriptionTextColor(R.color.white)
            .descriptionTextSize(18)
            .cancelable(true)
            .id(56)
        val share: TapTarget = TapTarget
            .forToolbarMenuItem(
                binding.toolbar,
                R.id.share,
                getString(R.string.share_button),
                getString(R.string.share_button_desc)
            )
            .outerCircleColor(R.color.primary)
            .outerCircleAlpha(1f)
            .targetCircleColor(R.color.white)
            .titleTextSize(28)
            .tintTarget(true)
            .titleTextColor(R.color.white)
            .descriptionTextColor(R.color.white)
            .descriptionTextSize(18)
            .cancelable(true)
        val ms = TapTarget.forView(
            binding.scientificPad.memoryStore,
            getString(R.string.memory_store), getString(R.string.memory_store_desc)
        )
            .outerCircleColor(R.color.numPadPrimary)
            .outerCircleAlpha(1f)
            .targetCircleColor(R.color.white)
            .titleTextSize(28)
            .tintTarget(false)
            .titleTextColor(R.color.textPrimary)
            .descriptionTextColor(R.color.textPrimary)
            .descriptionTextSize(18)
            .cancelable(true)
        val mr = TapTarget.forView(
            binding.scientificPad.memoryRestore,
            getString(R.string.memory_restore), getString(R.string.memory_restore_desc)
        )
            .outerCircleColor(R.color.numPadPrimary)
            .outerCircleAlpha(1f)
            .targetCircleColor(R.color.white)
            .titleTextSize(28)
            .tintTarget(false)
            .titleTextColor(R.color.textPrimary)
            .descriptionTextColor(R.color.textPrimary)
            .descriptionTextSize(18)
            .cancelable(true)

        tapTargetSequence.targets(delete, angle, share, options, ms, mr)

        tapTargetSequence.listener(object : TapTargetSequence.Listener {
            override fun onSequenceFinish() {
                binding.calculatorPadViewPager?.currentItem = 0
            }

            override fun onSequenceStep(lastTarget: TapTarget, targetClicked: Boolean) {
                if (lastTarget.id() == 56) {
                    binding.calculatorPadViewPager?.currentItem = 1
                }
            }

            override fun onSequenceCanceled(lastTarget: TapTarget) {}
        })
        tapTargetSequence.continueOnCancel(true).start()
    }

    private fun getShareEquation(): String {
        if (getExpression().isNotEmpty()) {
            val result = getResult()
            return if (result == "" || !removeNumberSeparator(result).isNumber()) {
                ""
            } else {
                val expression = viewModel.getCalculatedExpression()
                "$expression = $result"
            }
        }
        return ""
    }


    fun changeAngleType(menuItem: MenuItem) {
        val text = menuItem.title.toString()
        if (text == AngleType.DEG.name) {
            menuItem.title = AngleType.RAD.name
            viewModel.changeAngleType(AngleType.RAD)
            logEvent(CHANGE_ANGLE, bundleOf("type" to "RAD"))
        } else {
            menuItem.title = AngleType.DEG.name
            viewModel.changeAngleType(AngleType.DEG)
            logEvent(CHANGE_ANGLE, bundleOf("type" to "DEG"))
        }
        val currentExpression = getExpression()
        setExpression(currentExpression)
    }

    private fun getResultTextColor(isError: Boolean = false): Int {
        val typedValue = TypedValue()
        val typedArray = if (isError) {
            obtainStyledAttributes(typedValue.data, intArrayOf(R.attr.colorError))
        } else {
            obtainStyledAttributes(typedValue.data, intArrayOf(R.attr.textDisable))
        }
        val color = typedArray.getColor(0, 0)
        typedArray.recycle()
        return color
    }

    private fun animateClear() {
        with(binding) {
            val cx = clearView.right
            val cy = clearView.bottom
            val l = clearView.height
            val b = clearView.width
            val finalRadius = sqrt((l * l + b * b).toDouble()).toInt()
            val anim = ViewAnimationUtils
                .createCircularReveal(clearView, cx, cy, 0f, finalRadius.toFloat())
            clearView.visibility = View.VISIBLE
            anim.duration = resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
            anim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    setExpression("")
                    setResult("")
                    getResultEditText().setTextColor(getResultTextColor())
                    clearView.visibility = View.INVISIBLE
                    mCurrentAnimator = null
                }
            })
            mCurrentAnimator = anim
            anim.start()
        }
    }

    private fun setExpressionAfterEqual(answer: String) {
        // Calculate the values needed to perform the scale and translation animations,
        // accounting for how the scale will affect the final position of the text.
        val expression = getExpressionEditText()
        val result = getResultEditText()
        val resultScale = expression.getVariableTextSize(answer) / result.textSize
        val resultTranslationX = (1.0f - resultScale) * (result.width / 2.0f - result.paddingEnd)
        val resultTranslationY = (1.0f - resultScale) *
                (result.height / 2.0f - result.paddingBottom) +
                (expression.bottom - result.bottom) +
                (result.paddingBottom - expression.paddingBottom)
        val formulaTranslationY = -expression.bottom.toFloat()

        // Use a value animator to fade to the final text color over the course of the animation.
        val resultTextColor: Int = result.currentTextColor
        val formulaTextColor: Int = expression.currentTextColor
        val textColorAnimator =
            ValueAnimator.ofObject(ArgbEvaluator(), resultTextColor, formulaTextColor)
        textColorAnimator.addUpdateListener { valueAnimator ->
            result.setTextColor(valueAnimator.animatedValue as Int)
        }
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(
            textColorAnimator,
            ObjectAnimator.ofFloat(result, View.SCALE_X, resultScale),
            ObjectAnimator.ofFloat(result, View.SCALE_Y, resultScale),
            ObjectAnimator.ofFloat(result, View.TRANSLATION_X, resultTranslationX),
            ObjectAnimator.ofFloat(result, View.TRANSLATION_Y, resultTranslationY),
            ObjectAnimator.ofFloat(expression, View.TRANSLATION_Y, formulaTranslationY)
        )
        animatorSet.duration =
            resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        animatorSet.addListener(object : AnimatorListenerAdapter() {

            override fun onAnimationEnd(animation: Animator) {
                // Reset all of the values modified during the animation.
                result.setTextColor(resultTextColor)
                result.scaleX = 1.0f
                result.scaleY = 1.0f
                result.translationX = 0.0f
                result.translationY = 0.0f
                expression.translationY = 0.0f

                // Finally update the formula to use the current result.
                expression.setText(answer)
                result.setText("")
                mCurrentAnimator = null
            }
        })
        mCurrentAnimator = animatorSet
        animatorSet.start()
    }

    private fun setAppTheme() {
        val themeMode = when (getSelectedTheme()) {
            AppTheme.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            AppTheme.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(themeMode)
    }

    private fun getSelectedTheme(): AppTheme {
        val themeName = viewModel.getAppTheme()
        return try {
            AppTheme.valueOf(themeName)
        } catch (e: IllegalArgumentException) {
            AppTheme.SYSTEM_DEFAULT
        }
    }

    private fun getExpressionEditText(): CalculatorEditText {
        return binding.resultPad.expression
    }

    private fun getResultEditText(): CalculatorEditText {
        return binding.resultPad.result
    }

    private fun setExpression(expression: String) {
        getExpressionEditText().setText(expression)
    }

    private fun setResult(result: String) {
        getResultEditText().setText(result)
    }

    private fun getExpression(): String {
        return binding.resultPad.expression.text.toString().trim()
    }

    private fun getResult(): String {
        return binding.resultPad.result.text.toString().trim()
    }

    override fun onStart() {
        super.onStart()
        var savedExpression = viewModel.getSavedExpression()
        if (viewModel.getNumberSeparator() != NumberSeparator.OFF) {
            savedExpression = addNumberSeparator(
                expression = savedExpression,
                isIndian = (viewModel.getNumberSeparator() == NumberSeparator.INDIAN)
            )
        }
        setExpression(savedExpression)
    }

    override fun onStop() {
        super.onStop()
        mCurrentAnimator?.end()
        val currentExpression = removeNumberSeparator(getExpression())
        viewModel.saveExpression(currentExpression)
    }

    override fun getViewBinding(inflater: LayoutInflater) = ActivityMainBinding.inflate(inflater)
}