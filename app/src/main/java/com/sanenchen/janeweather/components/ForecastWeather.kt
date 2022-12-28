package com.sanenchen.janeweather.components

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.gson.Gson
import com.qweather.sdk.bean.weather.WeatherDailyBean
import com.qweather.sdk.view.QWeather
import com.sanenchen.janeweather.activities.MainActivity
import com.sanenchen.janeweather.utils.APIKeys
import com.sanenchen.janeweather.utils.SharedPreferencesUtils
import com.sanenchen.janeweather.utils.WeatherIconAdapter

/**
 * @author sanenchen
 * @since 2022/12/28
 * @description 未来天气
 */
@Composable
fun ForecastWeather(context: Context) {
    // 获取未来天气
    val forecastWeather = remember {
        mutableStateOf<WeatherDailyBean?>(
            Gson().fromJson(SharedPreferencesUtils().getData(context, "forecastWeather", "") as String, WeatherDailyBean::class.java)
        )
    }
    LaunchedEffect(key1 = MainActivity.refresh.value, block = {
        QWeather.getWeather7D(context, APIKeys.placeKey, object : QWeather.OnResultWeatherDailyListener {
            override fun onError(p0: Throwable?) {
                forecastWeather.value = null
            }

            override fun onSuccess(p0: WeatherDailyBean?) {
                p0!!.daily[0].fxDate = "今天"
                p0.daily[1].fxDate = "明天"
                p0.daily[2].fxDate = "后天"
                for (index in 3 until p0.daily.size)
                    p0.daily[index].fxDate = p0.daily[index].fxDate.substring(5, 10).replace("-", "月") + "日"
                forecastWeather.value = p0
                // 缓存
                SharedPreferencesUtils().saveData(context, "forecastWeather", Gson().toJson(p0))
            }
        })
    })
    Card(
        modifier = Modifier
            .padding(top = 8.dp, start = 16.dp, end = 16.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            Text("天气预报", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 16.dp , start = 16.dp, bottom = 4.dp))
            if (forecastWeather.value?.daily != null)
                for (index in forecastWeather.value?.daily!!)
                    ForecastWeatherItem(index)
            Spacer(modifier = Modifier.padding(top = 16.dp))

        }
    }
}

/**
 * 子Item
 */
@Composable
fun ForecastWeatherItem(dailyBean: WeatherDailyBean.DailyBean?) {
    Row(Modifier.padding(start = 16.dp, end = 16.dp)) {
        AsyncImage(
            model = WeatherIconAdapter.getNewIcon(dailyBean?.iconDay ?: "999"),
            contentDescription = "天气图标",
            Modifier
                .size(43.dp)
                .align(Alignment.CenterVertically)

        )
        Box(
            Modifier
                .fillMaxWidth()
                .align(Alignment.CenterVertically)
        ) {
            Text(
                text = "${dailyBean?.fxDate} · ${dailyBean?.textDay}", modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxWidth()
                    .padding(start = 16.dp), fontWeight = FontWeight.Bold
            )
            Text(
                text = "${dailyBean?.tempMax}° / ${dailyBean?.tempMin}°", modifier = Modifier
                    .align(Alignment.CenterEnd), fontSize = 18.sp, fontWeight = FontWeight.Bold
            )
        }
    }
}