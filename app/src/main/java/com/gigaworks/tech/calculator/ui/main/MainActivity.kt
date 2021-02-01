package com.gigaworks.tech.calculator.ui.main

import android.os.Bundle
import android.view.*
import android.widget.Toast
import com.gigaworks.tech.calculator.R
import com.gigaworks.tech.calculator.databinding.ActivityMainBinding
import com.gigaworks.tech.calculator.ui.base.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActionBar(binding.toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu);
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return super.onOptionsItemSelected(item)
    }

    fun changeAngleType(menuItem: MenuItem) {
        Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show()
    }

    override fun getViewBinding(inflater: LayoutInflater) = ActivityMainBinding.inflate(inflater)
}