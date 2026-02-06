package com.cerotek.imonitor.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Settings : Screen("settings")
    object InfoAzienda : Screen("info_azienda")
    object Updates : Screen("updates")
    object Smartwatch : Screen("smartwatch")
    object Parametri : Screen("parametri")
    object Storico : Screen("storico")
    object Thresholds : Screen("thresholds")
    object ParameterDetail : Screen("parameter_detail/{parameterType}") {
        fun createRoute(parameterType: String) = "parameter_detail/$parameterType"
    }
}
