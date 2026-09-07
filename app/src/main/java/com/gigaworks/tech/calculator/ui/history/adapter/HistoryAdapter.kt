package com.gigaworks.tech.calculator.ui.history.adapter

import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnCreateContextMenuListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gigaworks.tech.calculator.databinding.HistoryAdItemBinding
import com.gigaworks.tech.calculator.databinding.HistoryItemBinding
import com.gigaworks.tech.calculator.domain.HistoryAdapterItem
import com.gigaworks.tech.calculator.util.HISTORY_INLINE_AD_POSITION
import com.gigaworks.tech.calculator.util.visible
import com.google.android.gms.ads.AdView

class HistoryAdapter(
    private val historyList: List<HistoryAdapterItem>,
    private val clickListener: OnHistoryClickListener,
    inlineAdView: AdView? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var inlineAdView: AdView? = inlineAdView

    private val adPosition: Int
        get() = if (inlineAdView == null) NO_AD_POSITION
        else minOf(HISTORY_INLINE_AD_POSITION, historyList.size)

    /**
     * Adds the ad row once the request has filled, so an unfilled 300x250 slot never
     * takes up space in the list.
     */
    fun showInlineAd(adView: AdView) {
        if (inlineAdView != null) {
            return
        }
        inlineAdView = adView
        notifyItemInserted(adPosition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_AD) {
            AdViewHolder(HistoryAdItemBinding.inflate(inflater, parent, false))
        } else {
            HistoryViewHolder(HistoryItemBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AdViewHolder -> inlineAdView?.let { holder.bind(it) }
            is HistoryViewHolder -> holder.bind(historyList[toHistoryIndex(position)])
        }
    }

    override fun getItemCount() = historyList.size + if (adPosition == NO_AD_POSITION) 0 else 1

    override fun getItemViewType(position: Int) =
        if (position == adPosition) VIEW_TYPE_AD else VIEW_TYPE_HISTORY

    fun getHistory(position: Int): HistoryAdapterItem {
        return historyList[toHistoryIndex(position)]
    }

    private fun toHistoryIndex(position: Int): Int {
        return if (adPosition != NO_AD_POSITION && position > adPosition) position - 1 else position
    }

    interface OnHistoryClickListener {
        fun onHistoryClick(history: HistoryAdapterItem)
    }

    class AdViewHolder(private val binding: HistoryAdItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(adView: AdView) {
            // the AdView is owned by the activity and survives adapter rebuilds,
            // so it has to be detached from a recycled holder before being reattached
            (adView.parent as? ViewGroup)?.removeView(adView)
            binding.inlineAdContainer.removeAllViews()
            binding.inlineAdContainer.addView(adView)
        }
    }

    inner class HistoryViewHolder(private val binding: HistoryItemBinding) :
        RecyclerView.ViewHolder(binding.root), OnCreateContextMenuListener {

        fun bind(history: HistoryAdapterItem) {
            with(binding) {
                date.visible(!history.isPrevSame)
                border.visible(!history.isNextSame)
                date.text = history.date
                expression.text = history.expression
                result.text = history.result
                root.setOnClickListener {
                    clickListener.onHistoryClick(history)
                }
                root.setOnCreateContextMenuListener(this@HistoryViewHolder)
            }
        }

        override fun onCreateContextMenu(
            menu: ContextMenu,
            v: View?,
            menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            menu.add(this.bindingAdapterPosition, 102, 0, "Share")
            menu.add(this.bindingAdapterPosition, 101, 1, "Delete")
        }

    }

    companion object {
        private const val NO_AD_POSITION = -1
        private const val VIEW_TYPE_HISTORY = 0
        private const val VIEW_TYPE_AD = 1
    }

}
