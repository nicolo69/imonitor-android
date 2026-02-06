package com.cerotek.imonitor.ui.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cerotek.imonitor.R
import com.cerotek.imonitor.util.NotificationHistoryManager
import com.google.android.material.card.MaterialCardView

class NotificationHistoryAdapter(
    private val onItemClick: (NotificationHistoryManager.NotificationItem) -> Unit,
    private val onDeleteClick: (NotificationHistoryManager.NotificationItem) -> Unit
) : ListAdapter<NotificationHistoryManager.NotificationItem, NotificationHistoryAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
        
        // Animazione di entrata con effetto staggered
        holder.itemView.alpha = 0f
        holder.itemView.translationX = -100f
        holder.itemView.scaleX = 0.9f
        holder.itemView.scaleY = 0.9f
        
        holder.itemView.animate()
            .alpha(1f)
            .translationX(0f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(400)
            .setStartDelay((position * 80).toLong())
            .setInterpolator(DecelerateInterpolator())
            .start()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val card: MaterialCardView = itemView.findViewById(R.id.card_notification)
        private val headerLayout: LinearLayout = itemView.findViewById(R.id.header_layout)
        private val tvMessage: TextView = itemView.findViewById(R.id.tv_message)
        private val tvDateTime: TextView = itemView.findViewById(R.id.tv_datetime)
        private val tvValue: TextView = itemView.findViewById(R.id.tv_value)
        private val iconSeverity: ImageView = itemView.findViewById(R.id.icon_severity)
        private val btnDelete: ImageView = itemView.findViewById(R.id.btn_delete)

        fun bind(item: NotificationHistoryManager.NotificationItem) {
            tvMessage.text = item.message
            tvDateTime.text = item.getFormattedDateTime()
            
            val valueStr = if (item.value % 1 == 0f) 
                item.value.toInt().toString() 
            else 
                String.format("%.1f", item.value)
            tvValue.text = "$valueStr ${item.unit}"
            
            // Colore e icona in base alla severità
            when (item.severity) {
                NotificationHistoryManager.Severity.CRITICAL -> {
                    headerLayout.setBackgroundColor(itemView.context.getColor(R.color.health_danger))
                    iconSeverity.setImageResource(android.R.drawable.ic_dialog_alert)
                    iconSeverity.setColorFilter(itemView.context.getColor(R.color.health_danger))
                    tvMessage.setTextColor(itemView.context.getColor(R.color.white))
                    tvDateTime.setTextColor(itemView.context.getColor(R.color.white))
                    tvValue.setTextColor(itemView.context.getColor(R.color.health_danger))
                    btnDelete.setColorFilter(itemView.context.getColor(R.color.white))
                }
                NotificationHistoryManager.Severity.WARNING -> {
                    headerLayout.setBackgroundColor(itemView.context.getColor(R.color.health_warning))
                    iconSeverity.setImageResource(android.R.drawable.ic_dialog_info)
                    iconSeverity.setColorFilter(itemView.context.getColor(R.color.health_warning))
                    tvMessage.setTextColor(itemView.context.getColor(R.color.white))
                    tvDateTime.setTextColor(itemView.context.getColor(R.color.white))
                    tvValue.setTextColor(itemView.context.getColor(R.color.health_warning))
                    btnDelete.setColorFilter(itemView.context.getColor(R.color.white))
                }
            }
            
            card.setOnClickListener { onItemClick(item) }
            btnDelete.setOnClickListener { onDeleteClick(item) }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<NotificationHistoryManager.NotificationItem>() {
        override fun areItemsTheSame(
            oldItem: NotificationHistoryManager.NotificationItem,
            newItem: NotificationHistoryManager.NotificationItem
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: NotificationHistoryManager.NotificationItem,
            newItem: NotificationHistoryManager.NotificationItem
        ): Boolean = oldItem == newItem
    }
}
