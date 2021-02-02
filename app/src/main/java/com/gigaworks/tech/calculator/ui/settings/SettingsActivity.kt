package com.gigaworks.tech.calculator.ui.settings

import android.view.LayoutInflater
import com.gigaworks.tech.calculator.databinding.ActivitySettingsBinding
import com.gigaworks.tech.calculator.ui.base.BaseActivity

class SettingsActivity : BaseActivity<ActivitySettingsBinding>() {
    override fun getViewBinding(inflater: LayoutInflater) =
        ActivitySettingsBinding.inflate(inflater)
}