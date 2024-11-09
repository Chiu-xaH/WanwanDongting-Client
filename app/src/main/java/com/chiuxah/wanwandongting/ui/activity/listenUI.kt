package com.chiuxah.wanwandongting.ui.activity

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import coil.compose.rememberAsyncImagePainter
import com.chiuxah.wanwandongting.MyApplication
import com.chiuxah.wanwandongting.R
import com.chiuxah.wanwandongting.logic.dataModel.SearchResponse
import com.chiuxah.wanwandongting.logic.dataModel.SongsInfo
import com.chiuxah.wanwandongting.ui.utils.MyCard
import com.chiuxah.wanwandongting.ui.utils.MyToast
import com.chiuxah.wanwandongting.ui.utils.Round
import com.chiuxah.wanwandongting.ui.utils.RowHorizal
import com.chiuxah.wanwandongting.ui.utils.ScrollText
import com.chiuxah.wanwandongting.viewModel.MyViewModel
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListenUI(innerPadding : PaddingValues,vm : MyViewModel) {
    var input by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    var songInfo by remember { mutableStateOf(SongsInfo("","","","","")) }
    if(showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            shape = Round(sheetState)
        ) {
            PlayOnUI(vm,songInfo)
        }
    }

    Column() {
        Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
        Spacer(modifier = Modifier.height(25.dp))
        //内容主体
        RowHorizal {
            TextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 15.dp),
                value = input,
                onValueChange = {
                    input = it
                },
                label = { Text(stringResource(id = R.string.search_song_tip) ) },
                singleLine = true,
                trailingIcon = {
                    IconButton(
                        onClick = {
                            CoroutineScope(Job()).launch{
                                async { loading = true }.await()
                                async{ vm.searchSongs(input) }.await()
                                async {
                                    Handler(Looper.getMainLooper()).post{
                                        vm.searchResponse.observeForever { result ->
                                            if (result != null) {
                                                if(result.contains("{")) {
                                                    loading = false
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }) {
                        Icon(painter = painterResource(R.drawable.search), contentDescription = "description")
                    }
                },
                shape = MaterialTheme.shapes.medium,
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent, // 有焦点时的颜色，透明
                    unfocusedIndicatorColor = Color.Transparent, // 无焦点时的颜色，绿色
                ),
            )
        }
        AnimatedVisibility(
            visible = loading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            RowHorizal {
                Spacer(modifier = Modifier.height(5.dp))
                CircularProgressIndicator()
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        AnimatedVisibility(
            visible = !loading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            val songList = getSearchList(vm)
            LazyColumn {
                items(songList.size) { index ->
                    MyCard {
                        ListItem(
                            headlineContent = { Text(text = songList[index].title) },
                            supportingContent = { ScrollText(text = songList[index].album) },
                            overlineContent = { ScrollText(text = songList[index].singer) },
                            leadingContent = { AlbumImg(
                                albumImgId = songList[index].albumImgId,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(7.dp))
                                    .size(80.dp)
                            ) },
                            modifier = Modifier.clickable {
                                MyToast("正在开发")
                            },
                            trailingContent = {
                                FilledTonalIconButton(onClick = {
                                    songInfo = songList[index]
                                    showBottomSheet = true
                                }) {
                                    Icon(painterResource(id = R.drawable.play_circle), contentDescription = "")
                                }
                            }
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding())) }
            }
        }
        ////////
    }
}


fun getSearchList(vm: MyViewModel) : List<SongsInfo> {
    val json = vm.searchResponse.value
    val lists = mutableListOf<SongsInfo>()
    try {
        val data = Gson().fromJson(json,SearchResponse::class.java).data.song.list
        for(index in data.indices) {
            val str = data[index].f
            val f = str.split("|")
            if(f.size > 1) {
                val songId = f[0]
                val title = f[1]
                val singer = f[3]
                val albumImgId = f[4]
                val album = f[5]
                val song = SongsInfo(songId, title, singer, albumImgId, album)
                lists.add(song)
            }
        }
    } catch (_:Exception) {

    }
    return lists
}

@Composable
fun AlbumImg(albumImgId : String,modifier: Modifier) {
    Image(
        painter = rememberAsyncImagePainter(
            model = MyApplication.qmxApi + "/getAlbumPicture?id=" + albumImgId,
            placeholder = painterResource(id = R.drawable.ic_launcher_background),
            error = painterResource(id = R.drawable.ic_launcher_background)
        ),
        contentDescription = "" ,
        modifier = modifier
    )
}

data class GetSongmidResponse(val songmid : String)

data class GetSongUrlResponse(val songUrl : String?)


suspend fun getSongmid(vm: MyViewModel, songId: String): String {
    val resultChannel = Channel<String>()
    val observer = Observer<String?> { result ->
        if (result != null && result.contains("success")) {
            val songmid = Gson().fromJson(result, GetSongmidResponse::class.java).songmid
            resultChannel.trySend(songmid)
        } else {
            resultChannel.trySend("")
        }
    }

    withContext(Dispatchers.Main) {
        vm.SongmidResponse.observeForever(observer)
        vm.getSongmid(songId)
    }

    return resultChannel.receive().also {
        withContext(Dispatchers.Main) {
            vm.SongmidResponse.removeObserver(observer)
        }
    }
}

suspend fun getSongUrl(vm: MyViewModel, songmid: String): String {
    val resultChannel = Channel<String>()
    val observer = Observer<String?> { result ->
        if (result != null && result.contains("success")) {
            val res = Gson().fromJson(result, GetSongUrlResponse::class.java).songUrl
            resultChannel.trySend(res ?: "")
        } else {
            resultChannel.trySend("")
        }
    }

    withContext(Dispatchers.Main) {
        vm.songUrlResponse.observeForever(observer)
        vm.getSongUrl(songmid)
    }

    return resultChannel.receive().also {
        withContext(Dispatchers.Main) {
            vm.songUrlResponse.removeObserver(observer)
        }
    }
}

fun fetchSong(vm: MyViewModel, songId: String, callback: (String) -> Unit) {
    CoroutineScope(Dispatchers.Main).launch {
        val songmid = getSongmid(vm, songId)
        if (songmid.isNotEmpty()) {
            val url = getSongUrl(vm, songmid)
            callback(url)
        } else {
            callback("")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayOnUI(vm : MyViewModel,songInfo : SongsInfo) {



    var canPlay by remember { mutableStateOf(false) }
    var songUrl by remember { mutableStateOf("") }
    fetchSong(vm, songInfo.songId) { url ->
        songUrl = url
        canPlay = url.contains("http")
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding->
        Column(modifier = Modifier.padding(innerPadding)) {
            PlayUI(songInfo,canPlay, songUrl)
        }
    }
}

@Composable
fun PlayUI(songInfo : SongsInfo,canPlay : Boolean,songUrl : String) {
    var mediaPlayer: MediaPlayer? = null
    var isPlaying by remember { mutableStateOf(false) }
    val scale = animateFloatAsState(
        targetValue = if (!isPlaying) 0.9f else 1f, // 按下时为0.9，松开时为1
        //animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        animationSpec = tween(MyApplication.animationSpeed / 2, easing = LinearOutSlowInEasing),
        label = "" // 使用弹簧动画
    )

    Spacer(modifier = Modifier.height(50.dp))
    RowHorizal {
        AlbumImg(albumImgId = songInfo.albumImgId,
            modifier = Modifier
                .shadow(40.dp, RoundedCornerShape(15.dp)) // 添加阴影
                .size(300.dp)
                .scale(scale.value)
                .clip(RoundedCornerShape(15.dp))
            )
    }
    Spacer(modifier = Modifier.height(50.dp))
    if(canPlay)
        RowHorizal {

            FilledTonalIconButton(
                onClick = {
                    MyToast("正在开发")
                }, modifier = Modifier.size(50.dp)
            ) {
                Icon(painterResource(id = R.drawable.skip_previous), contentDescription = "",modifier = Modifier.size(30.dp))
            }
            Spacer(modifier = Modifier.width(30.dp))
            FilledTonalIconButton(
                onClick = {
                    if(mediaPlayer == null) {
                        mediaPlayer = MediaPlayer().apply {
                            setDataSource(songUrl)
                            prepareAsync()
                            setOnPreparedListener {
                                start()
                                isPlaying = true
                            }
                        }
                    } else {
                        isPlaying = if(isPlaying) {
                            mediaPlayer?.pause()
                            false
                        } else {
                            mediaPlayer?.start()
                            true
                        }
                    }
                },modifier = Modifier.size(50.dp)
            ) {
                Icon(painterResource(id = if(isPlaying) R.drawable.pause else R.drawable.play_arrow), contentDescription = "",modifier = Modifier.size(30.dp))
            }
            Spacer(modifier = Modifier.width(30.dp))

            FilledTonalIconButton(
                onClick = {
                    MyToast("正在开发")
                },modifier = Modifier.size(50.dp)
            ) {
                Icon(painterResource(id = R.drawable.skip_next), contentDescription = "",modifier = Modifier.size(30.dp))
            }
        }

}