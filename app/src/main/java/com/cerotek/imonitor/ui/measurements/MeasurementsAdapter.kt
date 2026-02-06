package com.cerotek.imonitor.ui.measurements

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cerotek.imonitor.data.local.entity.MeasurementEntity
import com.cerotek.imonitor.databinding.ItemMeasurementBinding
import java.text.SimpleDateFormat
import java.util.*

class MeasurementsAdapter : ListAdapter<MeasurementEntity, MeasurementsAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMeasurementBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
        
        // Animazione di entrata con effetto staggered
        holder.itemView.alpha = 0f
        holder.itemView.translationY = 80f
        holder.itemView.scaleX = 0.9f
        holder.itemView.scaleY = 0.9f
        
        holder.itemView.animate()
            .alpha(1f)
            .translationY(0f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(500)
            .setStartDelay((position * 100).toLong())
            .setInterpolator(DecelerateInterpolator())
            .start()
    }

    class ViewHolder(private val binding: ItemMeasurementBinding) : RecyclerView.ViewHolder(binding.root) {
        
        private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        fun bind(measurement: MeasurementEntity) {
            binding.apply {
                // Formatta data e ora separatamente per un look migliore
                val date = dateFormat.format(measurement.timestamp)
                val time = timeFormat.format(measurement.timestamp)
                tvTimestamp.text = "$date • $time"
                
                measurement.systolic?.let { sys ->
                    measurement.diastolic?.let { dia ->
                        tvBloodPressure.text = "$sys/$dia"
                    }
                }
                
                measurement.heartRate?.let {
                    tvHeartRate.text = it.toString()
                }
                
                measurement.oxygenSaturation?.let {
                    tvOxygen.text = it.toString()
                }
                
                measurement.temperature?.let {
                    tvTemperature.text = String.format("%.1f", it)
                }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<MeasurementEntity>() {
        override fun areItemsTheSame(oldItem: MeasurementEntity, newItem: MeasurementEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MeasurementEntity, newItem: MeasurementEntity): Boolean {
            return oldItem == newItem
        }
    }
}
