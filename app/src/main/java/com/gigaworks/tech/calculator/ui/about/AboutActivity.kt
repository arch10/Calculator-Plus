package com.gigaworks.tech.calculator.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gigaworks.tech.calculator.databinding.ActivityAboutBinding
import com.gigaworks.tech.calculator.ui.base.BaseActivity

class AboutActivity : BaseActivity<ActivityAboutBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupEdgeToEdge()
    }

    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            
            // Apply insets to the content
            binding.root.setPadding(
                binding.root.paddingLeft,
                insets.top,
                binding.root.paddingRight,
                insets.bottom
            )
            
            WindowInsetsCompat.CONSUMED
        }
    }

    override fun getViewBinding(inflater: LayoutInflater) = ActivityAboutBinding.inflate(inflater)
}