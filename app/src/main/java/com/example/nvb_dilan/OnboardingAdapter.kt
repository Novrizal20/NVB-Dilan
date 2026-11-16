package com.example.nvb_dilan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OnboardingAdapter(private val items: List<OnboardingItem>) :
    RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    class OnboardingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgOnboarding: ImageView = view.findViewById(R.id.imgOnboarding)
        val tvTitle: TextView = view.findViewById(R.id.tvOnboardingTitle)
        val tvDesc: TextView = view.findViewById(R.id.tvOnboardingDesc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_onboarding, parent, false)
        return OnboardingViewHolder(view)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        val item = items[position]
        holder.tvTitle.text = item.title
        holder.tvDesc.text = item.description
        holder.imgOnboarding.setImageResource(item.iconRes)
    }

    override fun getItemCount(): Int = items.size
}
