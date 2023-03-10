package com.sanenchen.janeweather.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.qweather.sdk.view.HeConfig
import com.sanenchen.janeweather.components.CurrentWeather
import com.sanenchen.janeweather.components.ForecastWeather
import com.sanenchen.janeweather.components.HourlyWeather
import com.sanenchen.janeweather.components.PrecipitationPreview
import com.sanenchen.janeweather.ui.theme.JaneWeatherTheme
import com.sanenchen.janeweather.utils.APIKeys

/**
 * @author sanenchen
 * @since 2022/12/27
 * @description MainActivity
 */

class MainActivity : ComponentActivity() {
    companion object {
        val refresh = mutableStateOf(false) // 全局刷新Key
    }
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        // 和风天气初始化
        HeConfig.init(APIKeys.weatherPublicKey, APIKeys.weatherPrivateKey)
        HeConfig.switchToDevService()
        setContent {
            JaneWeatherTheme {
                val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
                Scaffold(
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                    topBar = { TopAppBar(title = { Text("简·天气") }, scrollBehavior = scrollBehavior) },
                ) {
                    Box(
                        modifier = Modifier
                            .padding(it)
                            .verticalScroll(rememberScrollState())
                            .fillMaxWidth()
                    ) {
                        Components()
                    }
                }
            }
        }
    }

    /**
     * A [Composable] function that displays main components.
     */
    @Composable
    fun Components() {
        Column {
            CurrentWeather(this@MainActivity) // 当前天气
            PrecipitationPreview(this@MainActivity) // 降水概览
            HourlyWeather(this@MainActivity) // 逐小时天气
            ForecastWeather(context = this@MainActivity) // 未来天气
            Spacer(modifier = Modifier.padding(bottom = 24.dp))
        }
    }

    /**
     * 发送重新加载指令
     */
    override fun onResume() {
        super.onResume()
        refresh.value = !refresh.value
        Log.i("Refresh", "Refreshed Main ${refresh.value}")
    }
}