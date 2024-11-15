package com.chiuxah.wanwandongting.ui.activity

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.chiuxah.wanwandongting.MusicService
import com.chiuxah.wanwandongting.R
import com.chiuxah.wanwandongting.logic.dataModel.SongInfo
import com.chiuxah.wanwandongting.ui.utils.ActivedTopBar
import com.chiuxah.wanwandongting.ui.utils.ScrollText
import com.chiuxah.wanwandongting.viewModel.MusicViewModel
import com.chiuxah.wanwandongting.viewModel.MyViewModel

@Composable
fun playQueueUI(vmMusic : MusicViewModel,vm : MyViewModel,musicService: MusicService?) {
    val playQueue = musicService?.getQueue()
    var playingIndex by remember { mutableStateOf(musicService?.getCurrentIndex()) }
    ActivedTopBar(title = stringResource(id = R.string.play_queue))
    LazyColumn {
        playQueue?.let {
            items(it.size) { index ->
                val item = playQueue[index]
                Divider()
                ListItem(
                    headlineContent = { ScrollText(text = item.title) },
                    overlineContent = { Text(text = item.album + " | " + item.singer)},
                    modifier = Modifier.clickable {
                        //播放
                        item.url?.let { it1 ->
                            //清空
                            musicService.stopMusic()
                            vmMusic.currentSongUrl.value = null
                            //播放
                            vmMusic.isPlaying.value = true
                            vmMusic.songmid.value = item.songmid
                            vmMusic.currentSongUrl.value = item.url
                            vmMusic.songInfo.value = SongInfo("",item.title,item.singer,"",item.album)
                            musicService.playMusic(it1)
                            musicService.setCurrentIndex(index)
                            playingIndex = musicService?.getCurrentIndex()
                        }

                    },
                    leadingContent = { Text(text = (index + 1).toString()) },
                    trailingContent = {
                        if( playingIndex == index) {
                            Icon(painterResource(id = R.drawable.equalizer), contentDescription = "")
                        }
                    }
                )
            }
        }
    }
}