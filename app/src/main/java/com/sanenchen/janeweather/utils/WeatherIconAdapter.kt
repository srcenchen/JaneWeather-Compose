package com.sanenchen.janeweather.utils

class WeatherIconAdapter() {
    companion object {
        fun getNewIcon(newIcon: String): String {
            val url = when(newIcon.toInt()) {
                150 -> "100n"
                151 -> "101n"
                152 -> "102n"
                153 -> "103n"
                350 -> "300n"
                301 -> "301n"
                456 -> "406n"
                457 -> "407n"
                else -> "${newIcon}d"
            }
            return "https://a.hecdn.net/img/plugin/190516/icon/c/$url.png"

        }
    }
}