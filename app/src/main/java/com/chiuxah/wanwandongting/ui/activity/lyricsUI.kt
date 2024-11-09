package com.chiuxah.wanwandongting.ui.activity

import android.os.Looper
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.chiuxah.wanwandongting.R
import com.chiuxah.wanwandongting.viewModel.MusicViewModel
import com.chiuxah.wanwandongting.viewModel.MyViewModel
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
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
fun lyricsUI(vmMusic : MusicViewModel,vm  : MyViewModel) {
    var loading by remember { mutableStateOf(true) }
    val songmid = vmMusic.songmid.value

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

    val lyrics = getLyrics(vm)
    Box(modifier = Modifier.background(Color.Transparent)) {
        if(!loading) {
            val parsed = parseLyrics(lyrics)
            LazyColumn {
                item { Spacer(modifier = Modifier.height(5.dp)) }
                items(parsed.size) { index->
                    val time = parsed[index].first
                    Text(text = parsed[index].second, modifier = Modifier.padding(vertical = 8.dp)
                        ,color = if(time == vmMusic.currentProgress.value?.let { formatTime(it) }) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                        style = TextStyle(
                            fontSize = if(time == vmMusic.currentProgress.value?.let { formatTime(it) }) 22.sp else 18.sp,
                            fontWeight = if(time == vmMusic.currentProgress.value?.let { formatTime(it) }) FontWeight.Bold else FontWeight.Normal,
                        )
                    )
                }
            }
        } else {
            Text(text = stringResource(id = R.string.loading_lyrics) )
        }
    }
}

