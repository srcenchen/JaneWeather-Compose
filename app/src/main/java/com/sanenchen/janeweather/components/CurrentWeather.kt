package com.sanenchen.janeweather.components

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.google.gson.Gson
import com.qweather.sdk.bean.air.AirNowBean
import com.qweather.sdk.bean.base.Lang
import com.qweather.sdk.bean.base.Unit
import com.qweather.sdk.bean.weather.WeatherNowBean
import com.qweather.sdk.view.QWeather
import com.sanenchen.janeweather.activities.MainActivity
import com.sanenchen.janeweather.utils.APIKeys
import com.sanenchen.janeweather.utils.SharedPreferencesUtils

/**
 * @author sanenchen
 * @since 2022/12/27
 * @description 当前天气
 */

@Composable
fun CurrentWeather(context: Context) {
    val weatherNowBean = remember {
        mutableStateOf<WeatherNowBean?>(
            Gson().fromJson(
                SharedPreferencesUtils().getData(
                    context,
                    "weatherNowBean",
                    ""
                ) as String, WeatherNowBean::class.java
            ) // 载入缓存
        )
    }
    val airNowBean = remember {
        mutableStateOf<AirNowBean?>(
            Gson().fromJson(
                SharedPreferencesUtils().getData(
                    context,
                    "airNowBean",
                    ""
                ) as String, AirNowBean::class.java
            ) // 载入缓存
        )
    }

    LaunchedEffect(key1 = MainActivity.refresh.value, block = {
        // 获取当前天气
        QWeather.getWeatherNow(context, APIKeys.placeKey, Lang.ZH_HANS, Unit.METRIC, object : QWeather.OnResultWeatherNowListener {
            override fun onError(p0: Throwable?) {
                weatherNowBean.value = null
            }

            override fun onSuccess(p0: WeatherNowBean?) {
                weatherNowBean.value = p0
                // 持久化数据
                SharedPreferencesUtils().saveData(context, "weatherNowBean", Gson().toJson(p0))
                Log.i("Refresh", "Refreshed weatherNowBean")
            }
        })
        // 获取当前空气质量
        QWeather.getAirNow(context, APIKeys.placeKey, Lang.ZH_HANS, object : QWeather.OnResultAirNowListener {
            override fun onError(p0: Throwable?) {
                weatherNowBean.value = null
            }

            override fun onSuccess(p0: AirNowBean?) {
                airNowBean.value = p0
                // 持久化数据
                SharedPreferencesUtils().saveData(context, "airNowBean", Gson().toJson(p0))
            }
        })
    })
    TemperatureAndWeatherOverview(
        weatherNowBean = weatherNowBean.value, airNowBean = airNowBean.value,
    )
    Details(weatherNowBean = weatherNowBean.value)

}

/**
 * 温度和天气概览
 */
@Composable
fun TemperatureAndWeatherOverview(weatherNowBean: WeatherNowBean?, airNowBean: AirNowBean?) {
    val temp = weatherNowBean?.now?.temp
    val icon = weatherNowBean?.now?.icon
    val weatherNow = weatherNowBean?.now?.text
    val updateTime = weatherNowBean?.basic?.updateTime
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .padding(top = 8.dp, bottom = 0.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(text = "$temp°", fontSize = 128.sp)
            Column(
                Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 16.dp)
            ) {
                SubcomposeAsyncImage(
                    model = "https://a.hecdn.net/img/common/icon/202106d/${icon ?: "999"}.png",
                    contentDescription = "天气图标",
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.CenterHorizontally),
                    loading = {
                        Text(text = "loading...")
                    },
                    error = {
                        Text(text = "加载失败")
                    }) // 天气图标
                Text("${airNowBean?.now?.category}", modifier = Modifier.align(Alignment.CenterHorizontally))
            }

        }
        Text(text = "$weatherNow", fontSize = 20.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
        // 修正时间
        val updateTimeFixed = updateTime?.replace("T", " ")?.replace("+08:00", "")?.replace("-", "/")
        Text(
            text = "观测时间: $updateTimeFixed",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp)
        )
    }
}

/**
 * 一些详细信息
 */
@Composable
fun Details(weatherNowBean: WeatherNowBean?) {
    Card(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 18.dp)) {
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
                        text = "体感温度",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = "${weatherNowBean?.now?.feelsLike}°",
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
                        text = "${weatherNowBean?.now?.humidity}%",
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
                        text = "${weatherNowBean?.now?.windDir}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = "${weatherNowBean?.now?.windScale}级",
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
                        text = "能见度",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(text = "${weatherNowBean?.now?.vis}km", fontSize = 14.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
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
                        text = "降水量",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = "${weatherNowBean?.now?.precip}mm",
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
                        text = "${weatherNowBean?.now?.pressure}Pa",
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
                        text = "云量",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(text = "${weatherNowBean?.now?.cloud}％", fontSize = 14.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
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
                    Text(text = "${weatherNowBean?.now?.dew}°", fontSize = 14.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
        }

    }
}
