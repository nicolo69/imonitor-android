package com.cerotek.imonitor.ui.settings

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.cerotek.imonitor.R
import com.cerotek.imonitor.ui.login.LoginActivity
import com.cerotek.imonitor.util.SecurePreferences
import com.cerotek.imonitor.util.SettingsManager
import com.cerotek.imonitor.util.AccessibilityManager

class SettingsFragment : Fragment() {

    private lateinit var settingsManager: SettingsManager
    private lateinit var accessibilityManager: AccessibilityManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsManager = SettingsManager(requireContext())
        accessibilityManager = AccessibilityManager(requireContext())


    }


    fun performLogout() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Vuoi uscire dall'applicazione?")
            .setPositiveButton("Esci") { _, _ ->
                SecurePreferences(requireContext()).clearToken()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }
            .setNegativeButton("Annulla", null)
            .show()
    }
}