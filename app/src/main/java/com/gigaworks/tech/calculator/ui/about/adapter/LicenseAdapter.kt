package com.gigaworks.tech.calculator.ui.about.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gigaworks.tech.calculator.databinding.LicenseItemBinding
import com.gigaworks.tech.calculator.domain.License

class LicenseAdapter(
    private val licenseList: List<License>,
    private val clickListener: OnLicenseClickListener
) : RecyclerView.Adapter<LicenseAdapter.LicenseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LicenseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LicenseItemBinding.inflate(inflater, parent, false)
        return LicenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LicenseViewHolder, position: Int) {
        holder.bind(licenseList[position])
    }

    override fun getItemCount() = licenseList.size

    interface OnLicenseClickListener {
        fun onLicenseClick(license: License)
    }

    inner class LicenseViewHolder(private val binding: LicenseItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(license: License) {
            with(binding) {
                library.text = license.libraryName
                libraryOwner.text = license.owner
                licenseType.text = license.name
                root.setOnClickListener {
                    clickListener.onLicenseClick(license)
                }
            }
        }

    }

}