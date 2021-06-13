package com.gigaworks.tech.calculator.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
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
import com.gigaworks.tech.calculator.util.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.play.core.review.ReviewManagerFactory
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

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

    override fun onCreate(savedInstanceState: Bundle?) {
        val appPreference = AppPreference(this)
        val accentTheme =
            appPreference.getStringPreference(AppPreference.ACCENT_THEME, AccentTheme.BLUE.name)
        setTheme(getAccentTheme(accentTheme))
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.toolbar)

        setUpView()
        setUpObservables()
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
                it.name.toLowerCase(Locale.ROOT).capitalize(Locale.ROOT)
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
    }

    private fun setUpView() {
        if (viewModel.shouldAskUserRating()) {
            askUserRating()
        }
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
            checkAccentTheme(viewModel.selectedAccentTheme, colorDialog);
            dialog = MaterialAlertDialogBuilder(this)
                .setView(colorDialog.root)
                .setTitle(getString(R.string.select_accent_color))
                .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                    viewModel.setAccentTheme(viewModel.selectedAccentTheme)
                    dialog.dismiss()
                    val intent = arrayOfNulls<Intent>(2)
                    intent[1] = Intent(this, SettingsActivity::class.java)
                    intent[0] = Intent(
                        this,
                        MainActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivities(intent)
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish()
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }.show()
        }

        binding.toolbar.setNavigationOnClickListener { handleBackPress() }
        val appVersion = "${getString(R.string.version)}: ${BuildConfig.VERSION_NAME}"
        binding.aboutSubtitle.text = appVersion
        binding.themeCard.setOnClickListener {
            var selectedThemeChoice = viewModel.selectedTheme.value!!.ordinal
            dialog = MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.choose_theme))
                .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                    viewModel.changeTheme(selectedThemeChoice)
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
            viewModel.setSmartCalculation(!viewModel.getSmartCalculation())
        }
        binding.smartCalculationSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setSmartCalculation(isChecked)
        }
        binding.numberSeparatorCard.setOnClickListener {
            var numberSeparator = viewModel.getNumberSeparator()
            val list =
                NumberSeparator.values()
                    .map { it.name.toLowerCase(Locale.ROOT).capitalize(Locale.ROOT) }
                    .toTypedArray()
            dialog = MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.choose_number_separator))
                .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                    viewModel.changeNumberSeparator(numberSeparator)
                    dialog.dismiss()
                }
                .setSingleChoiceItems(
                    list,
                    viewModel.numberSeparator.value!!.ordinal
                ) { _, which ->
                    numberSeparator = NumberSeparator.values().find { it.ordinal == which }
                        ?: NumberSeparator.INTERNATIONAL
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }.show()
        }
        binding.deleteHistoryCard.setOnClickListener {
            var deleteHistory = viewModel.getAutoDeleteHistory()
            val list =
                HistoryAutoDelete.values().map { it.getString() }.toTypedArray()
            dialog = MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.delete_history))
                .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                    viewModel.setAutoDeleteHistory(deleteHistory)
                    dialog.dismiss()
                }
                .setSingleChoiceItems(
                    list,
                    viewModel.autoDeleteHistory.value!!.ordinal
                ) { _, which ->
                    deleteHistory = HistoryAutoDelete.values().find { it.ordinal == which }
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
            startActivity(Intent.createChooser(intent, getString(R.string.choose)))
        }
        binding.bugCard.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(REPORT_BUG_LINK) })
        }
        binding.rateCard.setOnClickListener {
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
                startActivity(intent)
            }
        }
        binding.rateCard.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(APP_STORE_LINK) })
        }
        binding.followCard.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(FOLLOW_LINK) })
        }
        binding.aboutCard.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
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

    override fun onBackPressed() {
        dialog?.dismiss()
        handleBackPress()
    }

    private fun handleBackPress() {
        finish()
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