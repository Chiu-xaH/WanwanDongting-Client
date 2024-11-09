package com.chiuxah.wanwandongting.ui.activity

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.chiuxah.wanwandongting.MusicService
import com.chiuxah.wanwandongting.MyApplication
import com.chiuxah.wanwandongting.R
import com.chiuxah.wanwandongting.logic.dataModel.SearchResponse
import com.chiuxah.wanwandongting.logic.dataModel.SongsInfo
import com.chiuxah.wanwandongting.ui.utils.MyCard
import com.chiuxah.wanwandongting.ui.utils.MyToast
import com.chiuxah.wanwandongting.ui.utils.Round
import com.chiuxah.wanwandongting.ui.utils.RowHorizal
import com.chiuxah.wanwandongting.ui.utils.ScrollText
import com.chiuxah.wanwandongting.viewModel.MusicViewModel
import com.chiuxah.wanwandongting.viewModel.MyViewModel
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListenUI(innerPadding : PaddingValues,vm : MyViewModel,vmMusic: MusicViewModel,musicService: MusicService?) {
    var input by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    if(showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            shape = Round(sheetState)
        ) {
            PlayOnUI(vm,musicService,vmMusic)
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
                                vmMusic.songInfo.value = songList[index]
                                showBottomSheet = true
                            },
                         //   trailingContent = {
                           //     FilledTonalIconButton(onClick = {

                             //   }) {
                               //     Icon(painterResource(id = R.drawable.play_circle), contentDescription = "")
                                //}
                            //}
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
                var title = f[1]
                if(title.contains("(")) title = title.substringBefore("(")
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


suspend fun fetchDominantColor(imageUrl: String): Color? {
    try {
        val loader = ImageLoader(MyApplication.context)
        val request = ImageRequest.Builder(MyApplication.context)
            .data(imageUrl)
            .allowHardware(false)
            .build()
        val result = (loader.execute(request) as SuccessResult).drawable
        val bitmap = result.toBitmap()

        return withContext(Dispatchers.Default) {
            val palette = Palette.from(bitmap).generate()
            palette?.dominantSwatch?.rgb?.let { Color(it) }
        }
    } catch (_:Exception) {
        return null
    }
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

fun fetchSong(vm: MyViewModel, songId: String,musicViewModel: MusicViewModel, callback: (String) -> Unit) {
    CoroutineScope(Dispatchers.Main).launch {
        val songmid = getSongmid(vm, songId)
        if (songmid.isNotEmpty()) {
            musicViewModel.songmid.value = songmid
            val url = getSongUrl(vm, songmid)
            callback(url)
        } else {
            callback("")
        }
    }
}



@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PlayOnUI(vm : MyViewModel,musicService: MusicService?,musicViewModel: MusicViewModel) {
   // val musicViewModel: MusicViewModel = viewModel()
     val TAB_MAIN = 0
     val TAB_RIGHT = 1
    var canPlay by remember { mutableStateOf(false) }
    var songUrl by remember { mutableStateOf("") }
    var backgroundColor by remember { mutableStateOf<Color?>(null) }

    musicViewModel.songInfo.value?.let {
        fetchSong(vm, it.songId,musicViewModel = musicViewModel) { url ->
        songUrl = url
        canPlay = url.contains("http")
    }
    }
    LaunchedEffect(musicViewModel.songInfo.value?.albumImgId) {
        val color = fetchDominantColor(MyApplication.qmxApi + "/getAlbumPicture?id=" + musicViewModel.songInfo.value?.albumImgId)
        backgroundColor = color
    }

    musicViewModel.currentSongUrl.value = songUrl

    val pagerState = rememberPagerState(pageCount = { 2 })


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        backgroundColor = Color.Transparent // 设置背景透明
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor ?: Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.8f)) // 叠加半透明白色蒙版
            ) {
                Column(modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp)) {
                    HorizontalPager(state = pagerState) { page ->
                        when(page) {
                            TAB_MAIN -> {
                                Scaffold(
                                    modifier = Modifier.fillMaxSize(),
                                    backgroundColor = Color.Transparent // 设置背景透明
                                ) {
                                    Column {
                                        PlayUI(canPlay, songUrl,musicViewModel,musicService)
                                    }
                                }

                            }
                            TAB_RIGHT -> {
                                Scaffold(
                                    modifier = Modifier.fillMaxSize(),
                                    backgroundColor = Color.Transparent // 设置背景透明
                                ) {
                                    Column {
                                        lyricsUI(musicViewModel,vm)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlayUI(canPlay : Boolean,songUrl : String,musicViewModel: MusicViewModel,musicService: MusicService?) {

    val songInfo = musicViewModel.songInfo.value

    val scale = animateFloatAsState(
        targetValue = if (!musicViewModel.isPlaying.value!!) 0.9f else 1f, // 按下时为0.9，松开时为1
        //animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        animationSpec = tween(MyApplication.animationSpeed / 2, easing = LinearOutSlowInEasing),
        label = "" // 使用弹簧动画
    )



    val coroutineScope = rememberCoroutineScope()
    val duration = musicService?.getDuration() ?: 0
    // 定期更新当前播放位置
    LaunchedEffect(musicViewModel.isPlaying.value) {
        while (musicViewModel.isPlaying.value == true) {
            musicViewModel.currentProgress.value = musicService?.getCurrentPosition() ?: 0
            delay(1000L)
        }
    }




    Spacer(modifier = Modifier.height(30.dp))
    Box(modifier = Modifier.scale(scale.value)) {
        RowHorizal {
            songInfo?.let {
                AlbumImg(albumImgId = it.albumImgId,
                    modifier = Modifier
                        .size(300.dp)
                        .shadow(20.dp, RoundedCornerShape(15.dp))
                        .clip(RoundedCornerShape(15.dp))
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(20.dp))
    songInfo?.let { Text(text = it.title, color = MaterialTheme.colorScheme.primary, style = TextStyle(fontSize = 23.sp)) }
    Spacer(modifier = Modifier.height(5.dp))
    songInfo?.let { Text(text = it.singer, color = MaterialTheme.colorScheme.secondary, style = TextStyle(fontSize = 18.sp)) }
    Spacer(modifier = Modifier.height(20.dp))
    if(canPlay) {
        musicViewModel.currentProgress.value?.let {
            Slider(
                value = it.toFloat(),
                valueRange = 0f..duration.toFloat(),
                onValueChange = {
                    musicViewModel.currentProgress.value = it.toInt()
                    musicService?.seekTo(musicViewModel.currentProgress.value!!)
                }
            )
        }
        Box(modifier = Modifier
            .fillMaxWidth()) {
            Text(
                text = formatTime(musicViewModel.currentProgress.value ?: 0),
                modifier = Modifier.align(Alignment.CenterStart)
            )
            Text(
                text = formatTime(duration),
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }

        Spacer(modifier = Modifier.height(60.dp))
        RowHorizal {
            FilledTonalIconButton(
                onClick = {
                    MyToast(MyApplication.context.getString(R.string.developing))
                }, modifier = Modifier.size(50.dp)
            ) {
                Icon(painterResource(id = R.drawable.skip_previous), contentDescription = "",modifier = Modifier.size(30.dp))
            }
            Spacer(modifier = Modifier.width(30.dp))
            FilledTonalIconButton(
                onClick = {
                    musicViewModel.isPlaying.value = if (musicViewModel.isPlaying.value == true) {
                        musicService?.pauseMusic()
                        false
                    } else {
                        musicService?.playMusic(songUrl)
                        true
                    }
                    //musicViewModel.isPlaying.value = isPlaying // 更新 ViewModel 的播放状态
                    musicViewModel.currentSongUrl.value = songUrl // 更新 ViewModel 的歌曲 URL
                    musicViewModel.songInfo.value = songInfo
                },modifier = Modifier.size(50.dp)
            ) {
                Icon(painterResource(id = if(musicViewModel.isPlaying.value == true) R.drawable.pause else R.drawable.play_arrow), contentDescription = "",modifier = Modifier.size(30.dp))
            }
            Spacer(modifier = Modifier.width(30.dp))

            FilledTonalIconButton(
                onClick = {
                    MyToast(MyApplication.context.getString(R.string.developing))
                },modifier = Modifier.size(50.dp)
            ) {
                Icon(painterResource(id = R.drawable.skip_next), contentDescription = "",modifier = Modifier.size(30.dp))
            }
            Spacer(modifier = Modifier.width(30.dp))

            FilledTonalIconButton(
                onClick = {
                    musicViewModel.isPlaying.value = false
                    musicService?.stopMusic()
                },modifier = Modifier.size(50.dp)
            ) {
                Icon(painterResource(id = R.drawable.stop), contentDescription = "",modifier = Modifier.size(30.dp))
            }
        }
    }

}

// 将时长转换为 MM:SS 格式
fun formatTime(ms: Int): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}