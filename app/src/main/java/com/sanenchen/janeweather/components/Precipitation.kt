package com.sanenchen.janeweather.components

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.gson.Gson
import com.qweather.sdk.bean.MinutelyBean
import com.qweather.sdk.bean.base.Lang
import com.qweather.sdk.view.QWeather
import com.sanenchen.janeweather.R
import com.sanenchen.janeweather.activities.MainActivity
import com.sanenchen.janeweather.utils.SharedPreferencesUtils
import java.lang.Thread.sleep
import kotlin.concurrent.thread

/**
 * @author sanenchen
 * @since 2022/12/27
 * @description 降水概览
 */
@Composable
fun PrecipitationPreview(context: Context) {
    val minutelyBean = remember {
        mutableStateOf<MinutelyBean?>(
            Gson().fromJson(SharedPreferencesUtils().getData(context, "minutelyBean", "") as String, MinutelyBean::class.java)
        )
    }
    // 获取降水信息
    LaunchedEffect(key1 = MainActivity.refresh.value, block = {
        QWeather.getMinuteLy(context, "119.1769091,35.0505468", Lang.ZH_HANS, object : QWeather.OnResultMinutelyListener {
            override fun onError(p0: Throwable?) {
                minutelyBean.value = null
            }

            override fun onSuccess(p0: MinutelyBean?) {
                minutelyBean.value = p0
                // 缓存
                SharedPreferencesUtils().saveData(context, "minutelyBean", Gson().toJson(p0))
                Log.i("Refresh", "Refreshed MinutelyBean")
            }
        })
    })
    Card(Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.water),
                contentDescription = "降水",
                modifier = Modifier.size(24.dp).align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.padding(start = 8.dp))
            Text(text = "${minutelyBean.value?.summary}", Modifier.align(Alignment.CenterVertically), fontSize = 18.sp)
        }
    }
}