package com.cerotek.imonitor.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cerotek.imonitor.R
import com.cerotek.imonitor.util.SettingsManager
import com.cerotek.imonitor.util.AlertManager
import com.cerotek.imonitor.util.SecurePreferences

class DashboardFragment : Fragment() {

    private lateinit var settingsManager: SettingsManager
    private lateinit var alertManager: AlertManager
    private lateinit var securePreferences: SecurePreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        settingsManager = SettingsManager(requireContext())
        alertManager = AlertManager(requireContext())
        securePreferences = SecurePreferences(requireContext())


    }


    fun onNewValueReceived(parameterType: String, parameterName: String, value: Float, unit: String) {

        android.util.Log.d("DashboardFragment", "Ricevuto dato: $parameterName = $value $unit")
    }
}