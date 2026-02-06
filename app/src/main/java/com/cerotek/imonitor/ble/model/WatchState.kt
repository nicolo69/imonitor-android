package com.cerotek.imonitor.ble.model

object WatchState {
    const val WATCH_CONNECTED = 1
    const val WATCH_BATTERY_LOW = 2
    const val WATCH_FOUND = 4
    const val WATCH_MEASURING = 8
    const val WATCH_RECHARGING = 16
    const val WATCH_NOT_WEAR = 32
}
