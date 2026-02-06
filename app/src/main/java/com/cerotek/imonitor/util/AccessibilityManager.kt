package com.cerotek.imonitor.util

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class AccessibilityManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "imonitor_accessibility",
        Context.MODE_PRIVATE
    )
    
    companion object {
        private const val KEY_DARK_MODE = "dark_mode_enabled"
        private const val KEY_FONT_SIZE = "font_size"
        
        const val FONT_SIZE_SMALL = 0.85f
        const val FONT_SIZE_NORMAL = 1.0f
        const val FONT_SIZE_LARGE = 1.2f
        const val FONT_SIZE_XLARGE = 1.5f
    }
    
    enum class FontSize(val scale: Float, val displayName: String) {
        SMALL(FONT_SIZE_SMALL, "Piccolo"),
        NORMAL(FONT_SIZE_NORMAL, "Normale"),
        LARGE(FONT_SIZE_LARGE, "Grande"),
        XLARGE(FONT_SIZE_XLARGE, "Molto Grande")
    }
    
    // Dark Mode
    fun setDarkModeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
        applyDarkMode(enabled)
    }
    
    fun isDarkModeEnabled(): Boolean {
        return prefs.getBoolean(KEY_DARK_MODE, false)
    }
    
    fun applyDarkMode(enabled: Boolean) {
        if (enabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
    
    // Font Size
    fun setFontSize(fontSize: FontSize) {
        prefs.edit().putFloat(KEY_FONT_SIZE, fontSize.scale).apply()
    }
    
    fun getFontSize(): FontSize {
        val scale = prefs.getFloat(KEY_FONT_SIZE, FONT_SIZE_NORMAL)
        return when {
            scale <= FONT_SIZE_SMALL -> FontSize.SMALL
            scale <= FONT_SIZE_NORMAL -> FontSize.NORMAL
            scale <= FONT_SIZE_LARGE -> FontSize.LARGE
            else -> FontSize.XLARGE
        }
    }
    
    fun getFontSizeScale(): Float {
        return prefs.getFloat(KEY_FONT_SIZE, FONT_SIZE_NORMAL)
    }
    
    fun applyFontSize(context: Context) {
        val scale = getFontSizeScale()
        val configuration = context.resources.configuration
        configuration.fontScale = scale
        context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
    }
    
    // Inizializzazione
    fun initialize() {
        applyDarkMode(isDarkModeEnabled())
    }
}
