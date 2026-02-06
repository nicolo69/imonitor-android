package com.cerotek.imonitor.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cerotek.imonitor.R
import java.text.SimpleDateFormat
import java.util.*

class ParameterDetailFragment : Fragment() {

    private lateinit var parameterType: String
    private lateinit var parameterName: String
    private lateinit var unit: String
    
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            parameterType = it.getString("parameter_type", "")
            parameterName = it.getString("parameter_name", "")
            unit = it.getString("unit", "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_parameter_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupHeader(view)
        setupRecyclerView(view)
        loadData()
    }

    private fun setupHeader(view: View) {
        view.findViewById<TextView>(R.id.tv_parameter_name)?.text = parameterName
        view.findViewById<TextView>(R.id.tv_unit)?.text = unit
        
        view.findViewById<ImageView>(R.id.btn_back)?.setOnClickListener {
            requireActivity().onBackPressed()
        }
        
        view.findViewById<ImageView>(R.id.btn_export)?.setOnClickListener {
            exportToExcel()
        }
        
        view.findViewById<View>(R.id.btn_export_excel)?.setOnClickListener {
            exportToExcel()
        }
    }

    private fun setupRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_history)
        historyAdapter = HistoryAdapter(unit)
        recyclerView?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }
    }

    private fun loadData() {
        // TODO: Carica dati reali dal database
        // Per ora mostra dati di esempio
        val sampleData = generateSampleData()
        
        val currentValueView = view?.findViewById<TextView>(R.id.tv_current_value)
        val currentValue = if (sampleData.isNotEmpty()) sampleData[0].value else "--"
        
        // Animazione del valore attuale
        currentValueView?.apply {
            alpha = 0f
            scaleX = 0.5f
            scaleY = 0.5f
            text = currentValue
            animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(600)
                .setStartDelay(200)
                .start()
        }
        
        view?.findViewById<TextView>(R.id.tv_last_update)?.text = 
            "Ultimo aggiornamento: ${SimpleDateFormat("HH:mm", Locale.ITALIAN).format(Date())}"
        
        // Animazione della lista con delay
        view?.postDelayed({
            historyAdapter.submitList(sampleData)
        }, 300)
        
        // Mostra/nascondi empty state
        view?.findViewById<View>(R.id.empty_state)?.visibility = 
            if (sampleData.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun generateSampleData(): List<MeasurementHistory> {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ITALIAN)
        val timeFormat = SimpleDateFormat("HH:mm", Locale.ITALIAN)
        
        return (0..9).map { i ->
            calendar.add(Calendar.HOUR, -i * 2)
            val value = when (parameterType) {
                "heart_rate" -> (65..85).random().toString()
                "blood_pressure" -> "${(110..130).random()}/${(70..85).random()}"
                "oxygen" -> (95..99).random().toString()
                "temperature" -> String.format("%.1f", 36.0 + Math.random())
                "glucose" -> (80..110).random().toString()
                "saturation" -> (94..99).random().toString()
                else -> "--"
            }
            
            MeasurementHistory(
                date = dateFormat.format(calendar.time),
                time = timeFormat.format(calendar.time),
                value = value
            )
        }
    }

    private fun exportToExcel() {
        Toast.makeText(
            requireContext(),
            "Esportazione Excel in corso...\nFile salvato in Download/$parameterType.xlsx",
            Toast.LENGTH_LONG
        ).show()
        
        // TODO: Implementa export Excel reale
        // Usa Apache POI o libreria simile per creare file Excel
    }
}

data class MeasurementHistory(
    val date: String,
    val time: String,
    val value: String
)
