package com.gigaworks.tech.calculator.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.gigaworks.tech.calculator.BuildConfig
import com.gigaworks.tech.calculator.R
import com.gigaworks.tech.calculator.databinding.ActivitySettingsBinding
import com.gigaworks.tech.calculator.databinding.PrecisionDialogBinding
import com.gigaworks.tech.calculator.ui.about.AboutActivity
import com.gigaworks.tech.calculator.ui.base.BaseActivity
import com.gigaworks.tech.calculator.ui.settings.helper.getString
import com.gigaworks.tech.calculator.ui.settings.viewmodel.SettingsViewModel
import com.gigaworks.tech.calculator.util.HistoryAutoDelete
import com.gigaworks.tech.calculator.util.NumberSeparator
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

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
            binding.numberSeparatorSubtitle.text = it.name.toLowerCase().capitalize()
        }
        viewModel.autoDeleteHistory.observe(this) {
            binding.deleteHistorySubtitle.text = it.getString()
        }
        viewModel.precision.observe(this) {
            val precisionSubtitle = "Precision: $it"
            binding.precisionSubtitle.text = precisionSubtitle
        }
    }

    private fun setUpView() {
        binding.toolbar.setNavigationOnClickListener { handleBackPress() }
        val appVersion = "Version: ${BuildConfig.VERSION_NAME}"
        binding.aboutSubtitle.text = appVersion
        binding.themeCard.setOnClickListener {
            var selectedThemeChoice = viewModel.selectedTheme.value!!.ordinal
            dialog = MaterialAlertDialogBuilder(this)
                .setTitle("Choose theme")
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
                NumberSeparator.values().map { it.name.toLowerCase().capitalize() }.toTypedArray()
            dialog = MaterialAlertDialogBuilder(this)
                .setTitle("Choose number separator")
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
                .setTitle("Auto delete history")
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
                .setTitle("Set answer precision")
                .setMessage("Current precision: $precision")
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