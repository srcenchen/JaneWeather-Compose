package com.sanenchen.janeweather.components

import android.content.Context
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.google.gson.Gson
import com.qweather.sdk.bean.weather.WeatherHourlyBean
import com.qweather.sdk.view.QWeather
import com.sanenchen.janeweather.activities.MainActivity
import com.sanenchen.janeweather.utils.APIKeys
import com.sanenchen.janeweather.utils.SharedPreferencesUtils
import com.sanenchen.janeweather.utils.WeatherIconAdapter

/**
 * @author sanenchen
 * @since 2022/12/27
 * @description 逐小时天气
 */

val showDetailAlert = mutableStateOf(false)
val weatherHourlyDetail = mutableStateOf<WeatherHourlyBean.HourlyBean?>(null)

@Composable
fun HourlyWeather(context: Context) {
    val weatherHourlyBean = remember {
        mutableStateOf<WeatherHourlyBean?>(
            Gson().fromJson(SharedPreferencesUtils().getData(context, "weatherHourlyBean", "") as String, WeatherHourlyBean::class.java)
        )
    }
    // 获取24小时信息
    LaunchedEffect(key1 = MainActivity.refresh.value, block = {
        QWeather.getWeather24Hourly(context, APIKeys.placeKey, object : QWeather.OnResultWeatherHourlyListener {
            override fun onError(p0: Throwable?) {
                weatherHourlyBean.value = null
            }

            override fun onSuccess(p0: WeatherHourlyBean?) {
                weatherHourlyBean.value = p0
                // 缓存
                SharedPreferencesUtils().saveData(context, "weatherHourlyBean", Gson().toJson(p0))
                Log.i("Refresh", "Refreshed Hour")
            }
        })
    })
    Card(
        Modifier
            .padding(top = 8.dp, start = 16.dp, end = 16.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        LazyRow(contentPadding = PaddingValues(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            if (weatherHourlyBean.value?.hourly != null) {
                items(weatherHourlyBean.value?.hourly!!) {
                    HourlyItem(it)
                }
            }
        }
    }
    if (showDetailAlert.value)
        AlertDialog(
            onDismissRequest = { showDetailAlert.value = false },
            confirmButton = { Button(onClick = { showDetailAlert.value = false }) { Text("确定") } },
            title = { Text("${weatherHourlyDetail.value?.fxTime?.substring(11, 16) ?: "00:00"} 时详细信息") }, text = {
                HourlyDetail()
            }
        )
}

// 每小时组件
@Composable
fun HourlyItem(hourlyBean: WeatherHourlyBean.HourlyBean?) {
    Column(
        Modifier
            .padding()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        showDetailAlert.value = true
                        weatherHourlyDetail.value = hourlyBean
                    }
                )
            }) {
        // 截取时间
        Text(hourlyBean?.fxTime?.substring(11, 16) ?: "00:00", modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.padding(top = 8.dp))
        SubcomposeAsyncImage(
            model = WeatherIconAdapter.getNewIcon(hourlyBean?.icon ?: "999"),
            contentDescription = "天气图标",
            modifier = Modifier
                .size(32.dp)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.padding(top = 8.dp))
        Text("${hourlyBean?.temp}°", Modifier.align(Alignment.CenterHorizontally))
    }
}

@Composable
fun HourlyDetail() {
    Card(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 8.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Column {
            Row( // 第一列
                Modifier
                    .padding(top = 16.dp, bottom = 16.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    Modifier
                        .weight(1f)
                        .padding(start = 16.dp, end = 16.dp)
                ) {
                    Text(
                        text = "温度",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = "${weatherHourlyDetail.value?.temp}°",
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                Column(
                    Modifier
                        .weight(1f)
                        .padding(start = 16.dp, end = 16.dp)
                ) {
                    Text(
                        text = "天气状况",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = "${weatherHourlyDetail.value?.text}",
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
            Row( // 第二列
                Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    Modifier
                        .weight(1f)
                        .padding(start = 16.dp, end = 16.dp)
                ) {
                    Text(
                        text = "${weatherHourlyDetail.value?.windDir}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = "${weatherHourlyDetail.value?.windScale}级",
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                Column(
                    Modifier
                        .weight(1f)
                        .padding(start = 16.dp, end = 16.dp)
                ) {
                    Text(
                        text = "相对湿度",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = "${weatherHourlyDetail.value?.humidity}％",
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
            Row( // 第三列
                Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    Modifier
                        .weight(1f)
                        .padding(start = 16.dp, end = 16.dp)
                ) {
                    Text(
                        text = "降水量",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = "${weatherHourlyDetail.value?.precip}mm",
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                Column(
                    Modifier
                        .weight(1f)
                        .padding(start = 16.dp, end = 16.dp)
                ) {
                    Text(
                        text = "大气压强",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = "${weatherHourlyDetail.value?.pressure}Pa",
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
            Row( // 第四列
                Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    Modifier
                        .weight(1f)
                        .padding(start = 16.dp, end = 16.dp)
                ) {
                    Text(
                        text = "云量",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = "${weatherHourlyDetail.value?.cloud}％",
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                Column(
                    Modifier
                        .weight(1f)
                        .padding(start = 16.dp, end = 16.dp)
                ) {
                    Text(
                        text = "露点温度",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = "${weatherHourlyDetail.value?.dew}°",
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }

    }
}