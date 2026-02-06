package com.cerotek.imonitor.ui.detail

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cerotek.imonitor.R

class HistoryAdapter(
    private val unit: String
) : ListAdapter<MeasurementHistory, HistoryAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), unit)
        
        // Animazione di entrata
        holder.itemView.alpha = 0f
        holder.itemView.translationY = 50f
        holder.itemView.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(300)
            .setStartDelay((position * 50).toLong())
            .setInterpolator(DecelerateInterpolator())
            .start()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        private val tvTime: TextView = itemView.findViewById(R.id.tv_time)
        private val tvValue: TextView = itemView.findViewById(R.id.tv_value)
        private val tvUnit: TextView = itemView.findViewById(R.id.tv_unit)

        fun bind(item: MeasurementHistory, unit: String) {
            tvDate.text = item.date
            tvTime.text = item.time
            tvValue.text = item.value
            tvUnit.text = unit
            
            // Animazione del valore
            animateValue()
        }
        
        private fun animateValue() {
            ObjectAnimator.ofFloat(tvValue, "scaleX", 0.8f, 1f).apply {
                duration = 400
                interpolator = DecelerateInterpolator()
                start()
            }
            ObjectAnimator.ofFloat(tvValue, "scaleY", 0.8f, 1f).apply {
                duration = 400
                interpolator = DecelerateInterpolator()
                start()
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<MeasurementHistory>() {
        override fun areItemsTheSame(
            oldItem: MeasurementHistory,
            newItem: MeasurementHistory
        ): Boolean {
            return oldItem.date == newItem.date && oldItem.time == newItem.time
        }

        override fun areContentsTheSame(
            oldItem: MeasurementHistory,
            newItem: MeasurementHistory
        ): Boolean {
            return oldItem == newItem
        }
    }
}
