package com.gigaworks.tech.calculator.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import com.gigaworks.tech.calculator.R
import com.gigaworks.tech.calculator.databinding.ActivityMainBinding
import com.gigaworks.tech.calculator.ui.about.AboutActivity
import com.gigaworks.tech.calculator.ui.base.BaseActivity
import com.gigaworks.tech.calculator.ui.history.HistoryActivity
import com.gigaworks.tech.calculator.ui.settings.SettingsActivity

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActionBar(binding.toolbar)

        binding.calculatorPadViewPager.addScientificPadStateChangeListener {
            binding.scientificPad.arrow.animate().rotationBy(180F).setDuration(300).start()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * On back pressed, close the scientific pad if it is open
     * else close the app.
     * */
    override fun onBackPressed() {
        if (binding.calculatorPadViewPager.currentItem == 0) {
            super.onBackPressed()
        } else {
            binding.calculatorPadViewPager.currentItem = 0
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.history -> startActivity(Intent(this, HistoryActivity::class.java))
            R.id.about -> startActivity(Intent(this, AboutActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    fun changeAngleType(menuItem: MenuItem) {
        val text = menuItem.title.toString()
        if (text == "DEG") {
            menuItem.title = "RAD"
        } else {
            menuItem.title = "DEG"
        }
    }

    override fun getViewBinding(inflater: LayoutInflater) = ActivityMainBinding.inflate(inflater)
}