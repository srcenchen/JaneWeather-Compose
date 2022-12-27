package com.sanenchen.janeweather.utils

import android.content.Context

/**
 * @author sanenchen
 * @since 2022/12/27
 * @description SharedPreferences工具类
 */
class SharedPreferencesUtils {
    /**
     * 保存数据
     */
    fun saveData(context: Context, key: String, value: Any) {
        val sp = context.getSharedPreferences("data", Context.MODE_PRIVATE)
        val editor = sp.edit()
        when (value) {
            is String -> editor.putString(key, value)
            is Int -> editor.putInt(key, value)
            is Boolean -> editor.putBoolean(key, value)
            is Float -> editor.putFloat(key, value)
            is Long -> editor.putLong(key, value)
            else -> editor.putString(key, value.toString())
        }
        editor.apply()
    }

    /**
     * 获取数据
     */
    fun getData(context: Context, key: String, default: Any): Any {
        val sp = context.getSharedPreferences("data", Context.MODE_PRIVATE)
        return when (default) {
            is String -> sp.getString(key, default) ?: ""
            is Int -> sp.getInt(key, default)
            is Boolean -> sp.getBoolean(key, default)
            is Float -> sp.getFloat(key, default)
            is Long -> sp.getLong(key, default)
            else -> sp.getString(key, default.toString()) ?: ""
        }
    }
}