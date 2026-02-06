package com.cerotek.imonitor.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cerotek.imonitor.R
import com.cerotek.imonitor.util.NotificationHistoryManager
import com.google.android.material.card.MaterialCardView

class NotificationHistoryFragment : Fragment() {

    private lateinit var historyManager: NotificationHistoryManager
    private lateinit var adapter: NotificationHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notification_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        historyManager = NotificationHistoryManager(requireContext())
        
        setupHeader(view)
        setupRecyclerView(view)
        loadHistory()
    }

    private fun setupHeader(view: View) {
        view.findViewById<View>(R.id.btn_clear_all)?.setOnClickListener {
            showClearConfirmDialog()
        }
        
        updateCounts(view)
    }

    private fun setupRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_notifications)
        adapter = NotificationHistoryAdapter(
            onItemClick = { notification ->
                // Opzionale: mostra dettagli
            },
            onDeleteClick = { notification ->
                historyManager.deleteNotification(notification.id)
                loadHistory()
            }
        )
        
        recyclerView?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@NotificationHistoryFragment.adapter
        }
    }

    private fun loadHistory() {
        val history = historyManager.getHistory()
        adapter.submitList(history)
        
        // Mostra/nascondi empty state
        view?.findViewById<View>(R.id.empty_state)?.visibility = 
            if (history.isEmpty()) View.VISIBLE else View.GONE
        
        view?.let { updateCounts(it) }
    }

    private fun updateCounts(view: View) {
        val criticalCount = historyManager.getCriticalCount()
        val warningCount = historyManager.getWarningCount()
        val totalCount = historyManager.getTotalCount()
        
        view.findViewById<TextView>(R.id.tv_critical_count)?.text = criticalCount.toString()
        view.findViewById<TextView>(R.id.tv_warning_count)?.text = warningCount.toString()
        view.findViewById<TextView>(R.id.tv_total_count)?.text = "$totalCount notifiche"
    }

    private fun showClearConfirmDialog() {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Cancella Storico")
            .setMessage("Vuoi cancellare tutte le notifiche?")
            .setPositiveButton("Cancella") { _, _ ->
                historyManager.clearHistory()
                loadHistory()
                android.widget.Toast.makeText(
                    requireContext(),
                    "Storico cancellato",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("Annulla", null)
            .show()
    }
}
