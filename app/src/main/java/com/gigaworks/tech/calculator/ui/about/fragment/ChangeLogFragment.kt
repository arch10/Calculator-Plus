package com.gigaworks.tech.calculator.ui.about.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.gigaworks.tech.calculator.R
import com.gigaworks.tech.calculator.databinding.FragmentChangelogBinding
import com.gigaworks.tech.calculator.ui.base.BaseFragment

class ChangeLogFragment : BaseFragment<FragmentChangelogBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setActionBar(binding.toolbar, getString(R.string.whats_new)) {
            findNavController().navigateUp()
        }

    }


    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentChangelogBinding.inflate(inflater, container, false)

}