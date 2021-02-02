package com.gigaworks.tech.calculator.ui.history

import android.view.LayoutInflater
import com.gigaworks.tech.calculator.databinding.ActivityHistoryBinding
import com.gigaworks.tech.calculator.ui.base.BaseActivity

class HistoryActivity : BaseActivity<ActivityHistoryBinding>() {
    override fun getViewBinding(inflater: LayoutInflater) = ActivityHistoryBinding.inflate(inflater)
}