package com.chiuxah.wanwandongting.ui.activity

import android.app.Activity
import android.os.Looper
import android.view.WindowManager
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chiuxah.wanwandongting.MusicService
import com.chiuxah.wanwandongting.MyApplication
import com.chiuxah.wanwandongting.R
import com.chiuxah.wanwandongting.viewModel.MusicViewModel
import com.chiuxah.wanwandongting.viewModel.MyViewModel
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

data class LyricsRespnse(val songLyrics : String?)

fun getLyrics(vm : MyViewModel) : String {
    var lyrics = ""
    try {
        val data = vm.songLyricsResponse.value
        lyrics = Gson().fromJson(data,LyricsRespnse::class.java).songLyrics ?: ""
    } catch (_:Exception) {

    }
    return lyrics
}

fun parseLyrics(lyrics: String): List<Pair<String, String>> {
    val lines = lyrics.split("\n")
    val lyricList = mutableListOf<Pair<String, String>>()

    val timeRegex = "\\[(\\d{2}):(\\d{2})\\.\\d{2}\\]".toRegex()

    for (line in lines) {
        val matchResult = timeRegex.find(line)
        if (matchResult != null) {
            val (minutes, seconds) = matchResult.destructured
            val formattedTime = "$minutes:$seconds" // 格式化时间为 "MM:SS"
            val lyricText = line.substring(matchResult.range.last + 1).trim()
            if (lyricText.isNotEmpty()) {
                lyricList.add(formattedTime to lyricText)
            }
        }
    }
    return lyricList
}


@Composable
fun lyricsUI(vmMusic : MusicViewModel,vm  : MyViewModel,musicService : MusicService?,color : Color?,innerPadding : PaddingValues) {
    var loading by remember { mutableStateOf(true) }
    val songmid = vmMusic.songmid.value

    var nowTime by remember { mutableStateOf(musicService?.getCurrentPosition() ) }
    val context = LocalContext.current
    val playing by remember { mutableStateOf(vmMusic.isPlaying.value) }

    // 定期更新当前播放位置
    if(playing == true) {
        //播放状态保持屏幕唤醒
        (context as? Activity)?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    } else {
        (context as? Activity)?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
    LaunchedEffect(playing) {
        while (playing == true) {
            nowTime = musicService?.getCurrentPosition() ?: 0
            delay(1000L)
        }
    }



    if(songmid != null && songmid != "") {
        CoroutineScope(Job()).launch {
            async { vm.getSongLyrics(songmid) }.await()
            async {
                android.os.Handler(Looper.getMainLooper()).post{
                    vm.songLyricsResponse.observeForever { result ->
                        if (result != null) {
                            if(result.contains("success")) {
                                loading = false
                            }
                        }
                    }
                }
            }
        }
    }

    fun formatTime(ms: Int): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
    fun parseTime(time: String): Int {
        val parts = time.split(":")
        val minutes = parts[0].toInt()
        val seconds = parts[1].toInt()
        return (minutes * 60 + seconds) * 1000
    }


    val lyrics = getLyrics(vm)
    val listState = rememberLazyListState()
    var blurStatus by remember { mutableStateOf(true) }
    Box(modifier = Modifier
        .background(Color.Transparent)
        .fillMaxWidth()) {
        FloatingActionButton(
            onClick = {  blurStatus = !blurStatus  },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp),
            containerColor = Color.Transparent,
            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(0.dp),
            contentColor = color?.darker() ?: MaterialTheme.colorScheme.primary
            ) {
            Icon(painterResource(id = if (!blurStatus) R.drawable.visibility else R.drawable.visibility_off), contentDescription = "")
        }
        if(!loading) {
            val parsed = parseLyrics(lyrics)

            val currentIndex = parsed.indexOfFirst { (time, _) ->
                nowTime?.let { current ->
                    val formattedCurrent = formatTime(current)
                    formattedCurrent >= time && (parsed.getOrNull(parsed.indexOfFirst { it.first == time } + 1)?.first
                        ?: "") > formattedCurrent
                } ?: false
            }.coerceAtLeast(0)

            val isUserScrolling = remember { mutableStateOf(false) }
            var previousIndex by remember { mutableStateOf(listState.firstVisibleItemIndex) }
            var previousOffset by remember { mutableStateOf(listState.firstVisibleItemScrollOffset) }

            // 定义一个变量来跟踪是否需要暂停滚动
            val shouldPauseScroll = remember { mutableStateOf(false) }

            // 监听滚动状态和幅度
            LaunchedEffect(listState.isScrollInProgress) {
                snapshotFlow { listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset }
                    .collect { (index, offset) ->
                        // 检测滚动幅度
                        val indexChange = (index - previousIndex).absoluteValue
                        val offsetChange = (offset - previousOffset).absoluteValue

                        if(indexChange > 2) {
                            isUserScrolling.value = true
                            blurStatus = false
                        }

                        previousIndex = index
                        previousOffset = offset
                    }
            }

            LaunchedEffect(currentIndex) {
                isUserScrolling.value = false // 表示自动滚动
                listState.animateScrollToItem(
                    //-3代表歌词在第四行放大
                    index = (currentIndex - 3).coerceAtLeast(0) // 保证歌词固定在第二或第三行
                )
            }

            LazyColumn(state = listState) {
                item { Spacer(modifier = Modifier.height(10.dp)) }
                items(parsed.size) { index->
                    val time = parsed[index].first
                    val nextTime = if (index < parsed.size - 1) parsed[index + 1].first else null


                    val isCurrentLine = nowTime?.let { current ->
                        val formattedCurrent = formatTime(current)
                        formattedCurrent >= time && (nextTime == null || formattedCurrent < nextTime)
                    } ?: false

                    // 使用 animateFloatAsState 为 sp 创建动画
                    val fontSize by animateFloatAsState(
                        targetValue = if (isCurrentLine) 22f else 18f,
                        animationSpec = tween(
                            durationMillis = MyApplication.animationSpeed/2,
                            easing = LinearOutSlowInEasing
                        ), label = ""
                    )

                    // 如果用户正在滚动，去掉模糊，否则根据当前歌词状态应用模糊
                    val blurSize by animateDpAsState(
                        targetValue = if (isCurrentLine || isUserScrolling.value) 0.dp else 2.dp,
                        animationSpec = tween(
                            durationMillis = MyApplication.animationSpeed/2,
                            easing = LinearOutSlowInEasing
                        ), label = ""
                    )

                    Text(text = parsed[index].second, modifier = Modifier
                        .padding(vertical = 8.dp)
                        .blur(if (blurStatus) blurSize else 0.dp)
                        .clickable {
                            //歌词点击动作
                            musicService?.seekTo(parseTime(time))
                        }
                        ,color =
                        if(color != null) {
                            if (isCurrentLine) color.darker(0.5f) else color.lighter(.7f)
                        } else {
                            if (isCurrentLine) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        },
                        style = TextStyle(
                            fontSize = fontSize.sp,
                            fontWeight = if (isCurrentLine) FontWeight.Bold else FontWeight.Normal,
                        ),
                    )
                }
                items(5) {
                    Text(text = "", modifier = Modifier
                        .padding(vertical = 8.dp),
                        style = TextStyle(
                            fontSize = 18.sp,
                        ),
                    )
                }
                item { Spacer(modifier = Modifier.height(10.dp)) }
            }
        }
    }
}


fun Color.darker(factor: Float = 0.5f): Color {
    return Color(
        red = (this.red * factor).coerceIn(0f, 1f),
        green = (this.green * factor).coerceIn(0f, 1f),
        blue = (this.blue * factor).coerceIn(0f, 1f),
        alpha = this.alpha
    )
}

fun Color.lighter(factor: Float = 0.7f): Color {
    return Color(
        red = (this.red * factor).coerceIn(0f, 1f),
        green = (this.green * factor).coerceIn(0f, 1f),
        blue = (this.blue * factor).coerceIn(0f, 1f),
        alpha = this.alpha
    )
}

fun Color.complementaryColor(): Color {
    // 获取RGB分量
    val red = (this.red * 255).toInt()
    val green = (this.green * 255).toInt()
    val blue = (this.blue * 255).toInt()
    val alpha = (this.alpha * 255).toInt()

    // 计算反色
    val invertedRed = 255 - red
    val invertedGreen = 255 - green
    val invertedBlue = 255 - blue

    // 返回新的Color
    return Color(
        red = invertedRed / 255f,
        green = invertedGreen / 255f,
        blue = invertedBlue / 255f,
        alpha = alpha / 255f
    )
}

fun Color.adjustColorForIcon(): Color {
    val color = this
    val luminance = color.luminance() // 获取亮度，范围为 0 到 1

    return if (luminance < 0.5) {
        // 深色变为非常浅的颜色 (接近白色)
        Color(
            red = (color.red + (1f - color.red) * 0.8f).coerceIn(0f, 1f),
            green = (color.green + (1f - color.green) * 0.8f).coerceIn(0f, 1f),
            blue = (color.blue + (1f - color.blue) * 0.8f).coerceIn(0f, 1f),
            alpha = color.alpha
        )
    } else {
        // 浅色变为非常深的颜色 (接近黑色)
        Color(
            red = (color.red * 0.2f).coerceIn(0f, 1f),
            green = (color.green * 0.2f).coerceIn(0f, 1f),
            blue = (color.blue * 0.2f).coerceIn(0f, 1f),
            alpha = color.alpha
        )
    }
}

