package com.gigaworks.tech.calculator.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import com.gigaworks.tech.calculator.databinding.ActivityAboutBinding
import com.gigaworks.tech.calculator.ui.base.BaseActivity

class AboutActivity : BaseActivity<ActivityAboutBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getViewBinding(inflater: LayoutInflater) = ActivityAboutBinding.inflate(inflater)
}