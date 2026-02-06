package com.cerotek.imonitor.util

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SecurePreferences(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "imonitor_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveCredentials(username: String, password: String) {
        sharedPreferences.edit().apply {
            putString(KEY_USERNAME, username)
            putString(KEY_PASSWORD, password)
            apply()
        }
    }

    fun getCredentials(): Pair<String, String>? {
        val username = sharedPreferences.getString(KEY_USERNAME, null)
        val password = sharedPreferences.getString(KEY_PASSWORD, null)
        
        return if (username != null && password != null) {
            Pair(username, password)
        } else {
            null
        }
    }

    fun saveDeviceMacAddress(macAddress: String) {
        sharedPreferences.edit().putString(KEY_MAC_ADDRESS, macAddress).apply()
    }

    fun getDeviceMacAddress(): String? {
        return sharedPreferences.getString(KEY_MAC_ADDRESS, null)
    }

    fun saveToken(token: String) {
        sharedPreferences.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString(KEY_TOKEN, null)
    }

    fun clearToken() {
        sharedPreferences.edit().remove(KEY_TOKEN).apply()
    }

    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }
    
    // User Information
    fun saveUserInfo(firstName: String?, lastName: String?, age: Int?, pathology: String?) {
        sharedPreferences.edit().apply {
            putString(KEY_FIRST_NAME, firstName)
            putString(KEY_LAST_NAME, lastName)
            putInt(KEY_AGE, age ?: 0)
            putString(KEY_PATHOLOGY, pathology)
            apply()
        }
    }
    
    fun getUserFirstName(): String? {
        return sharedPreferences.getString(KEY_FIRST_NAME, null)
    }
    
    fun getUserLastName(): String? {
        return sharedPreferences.getString(KEY_LAST_NAME, null)
    }
    
    fun getUserAge(): Int {
        return sharedPreferences.getInt(KEY_AGE, 0)
    }
    
    fun getUserPathology(): String? {
        return sharedPreferences.getString(KEY_PATHOLOGY, null)
    }
    
    // Get all user info as data class
    data class UserInfo(
        val firstName: String,
        val lastName: String,
        val age: Int,
        val pathology: String
    )
    
    fun getUserInfo(): UserInfo? {
        val firstName = getUserFirstName()
        val lastName = getUserLastName()
        val age = getUserAge()
        val pathology = getUserPathology()
        
        return if (firstName != null && lastName != null && age > 0 && pathology != null) {
            UserInfo(firstName, lastName, age, pathology)
        } else {
            null
        }
    }

    companion object {
        private const val KEY_USERNAME = "username"
        private const val KEY_PASSWORD = "password"
        private const val KEY_MAC_ADDRESS = "device_mac_address"
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_FIRST_NAME = "user_first_name"
        private const val KEY_LAST_NAME = "user_last_name"
        private const val KEY_AGE = "user_age"
        private const val KEY_PATHOLOGY = "user_pathology"
    }
}
