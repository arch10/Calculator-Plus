package com.gigaworks.tech.calculator.ui.about

import android.view.LayoutInflater
import com.gigaworks.tech.calculator.databinding.ActivityAboutBinding
import com.gigaworks.tech.calculator.ui.base.BaseActivity

class AboutActivity : BaseActivity<ActivityAboutBinding>() {

    override fun getViewBinding(inflater: LayoutInflater) = ActivityAboutBinding.inflate(inflater)
}