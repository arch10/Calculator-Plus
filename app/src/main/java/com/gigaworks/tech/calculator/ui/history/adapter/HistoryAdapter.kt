package com.gigaworks.tech.calculator.ui.history.adapter

import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnCreateContextMenuListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gigaworks.tech.calculator.databinding.HistoryItemBinding
import com.gigaworks.tech.calculator.domain.HistoryAdapterItem
import com.gigaworks.tech.calculator.util.visible
import java.util.*

class HistoryAdapter(
    private val historyList: List<HistoryAdapterItem>,
    private val clickListener: OnHistoryClickListener
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = HistoryItemBinding.inflate(inflater, parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(historyList[position])
    }

    override fun getItemCount() = historyList.size

    fun getHistory(position: Int): HistoryAdapterItem {
        return historyList[position]
    }

    interface OnHistoryClickListener {
        fun onHistoryClick(history: HistoryAdapterItem)
    }

    inner class HistoryViewHolder(private val binding: HistoryItemBinding) :
        RecyclerView.ViewHolder(binding.root), OnCreateContextMenuListener {

        fun bind(history: HistoryAdapterItem) {
            with(binding) {
                if (history.isPrevSame) {
                    date.visible(false)
                }
                if (history.isNextSame) {
                    border.visible(false)
                }
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
            menu.add(this.adapterPosition, 102, 0, "Share")
            menu.add(this.adapterPosition, 101, 1, "Delete")
        }

    }

}