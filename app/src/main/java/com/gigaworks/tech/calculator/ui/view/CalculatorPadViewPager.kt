/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Modifications copyright (C) 2021 Archit Raj
 */

package com.gigaworks.tech.calculator.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.gigaworks.tech.calculator.R

class CalculatorPadViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {

    private var onStateChanged: (open: Boolean) -> Unit = {}

    private val mStaticPagerAdapter = object : PagerAdapter() {
        override fun getCount(): Int {
            return childCount
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            return getChildAt(position)
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            removeViewAt(position)
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun getPageWidth(position: Int): Float {
            return if (position == 1) 7.0f / 9.0f else 1.0f
        }
    }

    private val mPageTransformer =
        PageTransformer { view, position ->
            if (position < 0.0f) {
                // Pin the left page to the left side.
                view.translationX = (width) * -position
                view.alpha = (1.0f + position).coerceAtLeast(0.2f)
            } else {
                // Use the default slide transition when moving to the next page.
                view.translationX = 0.0f
                view.alpha = 1.0f
            }
        }

    private val mOnPageChangeListener =
        object : SimpleOnPageChangeListener() {
            private fun recursivelySetEnabled(view: View, enabled: Boolean) {
                if (view is ViewGroup) {
                    for (childIndex in 0 until view.childCount) {
                        recursivelySetEnabled(view.getChildAt(childIndex), enabled)
                    }
                } else {
                    view.isEnabled = enabled
                }
            }

            override fun onPageSelected(position: Int) {
                if (adapter === mStaticPagerAdapter) {
                    for (childIndex in 0 until childCount) {
                        // Only enable subviews of the current page.
                        recursivelySetEnabled(getChildAt(childIndex), childIndex == position)
                    }
                    if (position == 1) {
                        onStateChanged(true)
                    } else if (position == 0) {
                        onStateChanged(false)
                    }

                }
            }
        }

    init {
        adapter = mStaticPagerAdapter
        pageMargin = resources.getDimensionPixelSize(R.dimen.pad_page_margin)
        addOnPageChangeListener(mOnPageChangeListener)
        setPageTransformer(false, mPageTransformer)
    }


    fun addScientificPadStateChangeListener(onStateChanged: (open: Boolean) -> Unit) {
        this.onStateChanged = onStateChanged
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        // Invalidate the adapter's data set since children may have been added during inflation.
        if (adapter === mStaticPagerAdapter) {
            mStaticPagerAdapter.notifyDataSetChanged()
        }
    }

}