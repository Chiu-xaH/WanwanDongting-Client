package com.chiuxah.wanwandongting.ui.activity

import android.os.Looper
import android.util.Log
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
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
fun lyricsUI(vmMusic : MusicViewModel,vm  : MyViewModel,musicService : MusicService?) {
    var loading by remember { mutableStateOf(true) }
    val songmid = vmMusic.songmid.value

    var nowTime by remember { mutableStateOf(musicService?.getCurrentPosition() ) }
    // 定期更新当前播放位置
    LaunchedEffect(vmMusic.isPlaying.value) {
        while (vmMusic.isPlaying.value == true) {
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
    Box(modifier = Modifier.background(Color.Transparent)) {
        if(!loading) {
            val parsed = parseLyrics(lyrics)

            val currentIndex = parsed.indexOfFirst { (time, _) ->
                nowTime?.let { current ->
                    val formattedCurrent = formatTime(current)
                    formattedCurrent >= time && (parsed.getOrNull(parsed.indexOfFirst { it.first == time } + 1)?.first
                        ?: "") > formattedCurrent
                } ?: false
            }.coerceAtLeast(0)

            LaunchedEffect(currentIndex) {
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
                            durationMillis = MyApplication.animationSpeed / 2,
                            easing = LinearOutSlowInEasing
                        ), label = ""
                    )

                    Text(text = parsed[index].second, modifier = Modifier.padding(vertical = 8.dp)
                        .clickable {
                            //歌词点击动作
                            musicService?.seekTo(parseTime(time))
                                   }
                        ,color = if (isCurrentLine) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                        style = TextStyle(
                            fontSize = fontSize.sp,
                            fontWeight = if (isCurrentLine) FontWeight.Bold else FontWeight.Normal,
                        ),
                    )
                }
                item { Spacer(modifier = Modifier.height(10.dp)) }
            }
        } else {
            Text(text = stringResource(id = R.string.loading_lyrics) )
        }
    }
}



