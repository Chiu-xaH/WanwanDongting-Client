package com.chiuxah.wanwandongting.ui.activity

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.Observer
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.chiuxah.wanwandongting.service.MusicService
import com.chiuxah.wanwandongting.MyApplication
import com.chiuxah.wanwandongting.R
import com.chiuxah.wanwandongting.logic.bean.SearchResponse
import com.chiuxah.wanwandongting.logic.bean.SingleSongInfo
import com.chiuxah.wanwandongting.logic.bean.SongInfo
import com.chiuxah.wanwandongting.logic.utils.reEmptyLiveDta
import com.chiuxah.wanwandongting.ui.activity.AlbumImgApiType.*
import com.chiuxah.wanwandongting.ui.utils.components.MyCard
import com.chiuxah.wanwandongting.ui.utils.components.MyToast
import com.chiuxah.wanwandongting.ui.utils.style.Round
import com.chiuxah.wanwandongting.ui.utils.style.RowHorizontal
import com.chiuxah.wanwandongting.ui.utils.components.ScrollText
import com.chiuxah.wanwandongting.ui.utils.style.TextFiledTransplant
import com.chiuxah.wanwandongting.viewModel.MusicViewModel
import com.chiuxah.wanwandongting.viewModel.MyViewModel
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

enum class AlbumImgApiType {
    IMGID,ALBUM_MID
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListenUI(innerPadding : PaddingValues,vm : MyViewModel,vmMusic: MusicViewModel,musicService: MusicService?) {
    var input by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var show by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var page by remember { mutableStateOf(1) }
    var onclick by remember { mutableStateOf(false) }
    fun refreshData() {
        CoroutineScope(Job()).launch{
            async {
                reEmptyLiveDta(vm.searchResponse)
                loading = true
            }.await()
            async{ vm.searchSongs(input,page = page) }.await()
            async {
                Handler(Looper.getMainLooper()).post{
                    vm.searchResponse.observeForever { result ->
                        if (result != null) {
                            if(result.contains("{")) {
                                onclick = false
                                loading = false
                            }
                        }
                    }
                }
            }
        }
    }
    if(onclick) {
        refreshData()
    }
    if(showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
                count = 0
                               },
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
            RowHorizontal {
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
                                refreshData()
                                show = true
                            }) {
                            Icon(painter = painterResource(R.drawable.search), contentDescription = "description")
                        }
                    },
                    shape = MaterialTheme.shapes.medium,
                    colors = TextFiledTransplant(),
                )
            }
            AnimatedVisibility(
                visible = loading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                RowHorizontal {
                    Spacer(modifier = Modifier.height(5.dp))
                    CircularProgressIndicator()
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
            androidx.compose.animation.AnimatedVisibility(
                visible = !loading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                val songList = getSearchList(vm)
                Box() {
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
                                            .size(80.dp),
                                        apiType = IMGID
                                    ) },
                                    modifier = Modifier.clickable {
                                        vmMusic.songInfo.value = songList[index]
                                        showBottomSheet = true
                                    },
                                    trailingContent = {
                                        FilledTonalIconButton(onClick = {
                                            //添加到播放队列
                                            var mid = ""
                                            CoroutineScope(Job()).launch {
                                                async {
                                                    mid = getSongmid(vm =vm, songId = songList[index].songId)
                                                }.await()
                                                async {
                                                    count = 0
                                                    fetchSong(vm, songList[index].songId,musicViewModel = vmMusic) { url ->
                                                        Log.d("url", url)
                                                        if(url.contains("http")) {
                                                            val singleSong = SingleSongInfo(singer = songList[index].singer, title = songList[index].title, albumImgId = songList[index].albumImgId, album = songList[index].album, songmid = mid, url = url)
                                                            musicService?.addSongToPlaylist(singleSong)
                                                            vmMusic.songmid.value = mid
                                                        } else {
                                                            MyToast(MyApplication.context.getString(R.string.add_false))
                                                        }
                                                        count++;
                                                    }
                                                }
                                            }
                                        }) {
                                            Icon(painterResource(id = R.drawable.playlist_add), contentDescription = "")
                                        }
                                    }
                                )
                            }
                        }
                        item { Spacer(modifier = Modifier.height(85.dp)) }
                        item { Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding())) }

                    }

                    if(show){
                        androidx.compose.animation.AnimatedVisibility(
                            visible = !loading,
                            enter = scaleIn(),
                            exit = scaleOut(),
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(horizontal = 15.dp, vertical = 15.dp)
                                .padding(innerPadding)
                        ) {
                            FloatingActionButton(
                                onClick = {
                                    if (page > 1) {
                                        page--
                                        onclick = true
                                        loading = true
                                    } else {
                                        MyToast("第一页")
                                    }
                                },
                            ) { Icon(Icons.Filled.ArrowBack, "Add Button") }
                        }

                        androidx.compose.animation.AnimatedVisibility(
                            visible = !loading,
                            enter = scaleIn(),
                            exit = scaleOut(),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(horizontal = 15.dp, vertical = 15.dp)
                                .padding(innerPadding)
                        ) {
                            ExtendedFloatingActionButton(
                                onClick = {
                                    page = 1
                                    onclick = true
                                    loading = true
                                },
                            ) { Text(text = "第${page}页") }
                        }

                        androidx.compose.animation.AnimatedVisibility(
                            visible = !loading,
                            enter = scaleIn(),
                            exit = scaleOut(),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(horizontal = 15.dp, vertical = 15.dp)
                                .padding(innerPadding)
                        ) {
                            FloatingActionButton(
                                onClick = {
                                    page++
                                    onclick = true
                                    loading = true
                                },
                            ) { Icon(Icons.Filled.ArrowForward, "Add Button") }
                        }
                    }
                    }
            }

            ////////
        }

}


fun getSearchList(vm: MyViewModel) : List<SongInfo> {
    val json = vm.searchResponse.value
    val lists = mutableListOf<SongInfo>()
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
                val song = SongInfo(songId, title, singer, albumImgId, album)
                lists.add(song)
            }
        }
    } catch (_:Exception) {

    }
    return lists
}

@Composable
fun AlbumImg(albumImgId : String,modifier: Modifier,apiType : AlbumImgApiType) {
    val api = when(apiType) {
        IMGID -> "/getAlbumPicture?id=$albumImgId"
        ALBUM_MID -> "/getAlbumPicture2?albumMid=$albumImgId"
    }
    Box(
        modifier.aspectRatio(1f)
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = MyApplication.qmxApi + api,
                placeholder = painterResource(id = R.drawable.ic_launcher_background),
                error = painterResource(id = R.drawable.ic_launcher_background)
            ),
            contentDescription = "" ,
            contentScale = ContentScale.Crop, // 填满 Box 并裁剪
            modifier = Modifier.fillMaxSize() // 图片填满整个 Box
        )
    }


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
        vm.songmidResponse.observeForever(observer)
        vm.getSongmid(songId)
    }

    return resultChannel.receive().also {
        withContext(Dispatchers.Main) {
            vm.songmidResponse.removeObserver(observer)
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
    if(count == 0) {
        count++
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
}

var count = 0

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PlayOnUI(vm : MyViewModel, musicService: MusicService?, musicViewModel: MusicViewModel) {
   // val musicViewModel: MusicViewModel = viewModel()
    val TAB_PLAY = 1
    val TAB_LYRICS = 2
    val TAB_QUEUE = 0

    var canPlay by remember { mutableStateOf(false) }
    var songUrl by remember { mutableStateOf("") }
    var backgroundColor by remember { mutableStateOf<Color?>(null) }

    musicViewModel.songInfo.value?.let {
        fetchSong(vm, it.songId,musicViewModel = musicViewModel) { url ->
            songUrl = url
            canPlay = url.contains("http")
        }
    }

 //   val albumImgId by remember { mutableStateOf(musicViewModel.songInfo.value) }

    LaunchedEffect(musicViewModel.songmid.value) {
        val id = musicViewModel.songInfo.value?.albumImgId ?: ""

        val url = if(id.isDigitsOnly()) MyApplication.qmxApi + "/getAlbumPicture?id=" + id else MyApplication.qmxApi + "/getAlbumPicture2?albumMid=" + id
        val color = fetchDominantColor(url)
        backgroundColor = color
    }

    musicViewModel.currentSongUrl.value = songUrl

    val pagerState = rememberPagerState(pageCount = { 3 }, initialPage = TAB_PLAY)


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
                    .padding(innerPadding)) {
                    HorizontalPager(state = pagerState) { page ->
                        when(page) {
                            TAB_PLAY -> {
                                Scaffold(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 15.dp),
                                    backgroundColor = Color.Transparent // 设置背景透明
                                ) {
                                    Column {
                                        PlayUI(backgroundColor, songUrl,musicViewModel,musicService,canPlay)
                                    }
                                }

                            }
                            TAB_LYRICS -> {
                                Scaffold(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 20.dp),
                                    backgroundColor = Color.Transparent // 设置背景透明
                                ) {
                                    Column {
                                        lyricsUI(musicViewModel,vm,musicService,backgroundColor,innerPadding)
                                        Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding()))
                                    }
                                }
                            }
                            TAB_QUEUE -> {
                                Scaffold(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 20.dp),
                                    backgroundColor = Color.Transparent // 设置背景透明
                                ) {
                                    Column {
                                        Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
                                        playQueueUI(musicViewModel,vm,musicService)
                                        Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding()))
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
fun PlayUI(color : Color?, songUrl : String, musicViewModel: MusicViewModel, musicService: MusicService?, canPlay : Boolean) {


    var playing by remember { mutableStateOf(musicViewModel.isPlaying.value ?: false) }
    val songInfo = musicViewModel.songInfo.value

    val scale = animateFloatAsState(
        targetValue = if (!playing) 0.9f else 1f, // 按下时为0.9，松开时为1
        //animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        animationSpec = tween(MyApplication.animationSpeed / 2, easing = LinearOutSlowInEasing),
        label = "" // 使用弹簧动画
    )


    var currentPosition by remember { mutableStateOf(0) }

    val coroutineScope = rememberCoroutineScope()
    val duration = musicService?.getDuration() ?: 0
    // 定期更新当前播放位置
    LaunchedEffect(playing) {
        musicViewModel.isPlaying.value = playing
        while (playing) {
            currentPosition = musicService?.getCurrentPosition() ?: 0
            if(currentPosition == musicService?.getDuration()) {
                musicService.stopMusic()
                val info = musicService.playNext()
                musicViewModel.songInfo.value = SongInfo(title = info.title, songId = "", singer = info.singer, album = info.album, albumImgId = "")
                musicViewModel.currentSongUrl.value = info.url
                musicViewModel.songmid.value = info.songmid
            }
            delay(1000L)
        }
    }



    val shadowSize by animateDpAsState(
        targetValue = if (!playing) 20.dp else 5.dp,
        animationSpec = tween(
            durationMillis = MyApplication.animationSpeed,
            easing = LinearOutSlowInEasing
        ), label = ""
    )

    Spacer(modifier = Modifier.height(30.dp))

        RowHorizontal {
            songInfo?.let {
                AlbumImg(albumImgId = it.albumImgId,
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .scale(scale.value)
                        .shadow(shadowSize, RoundedCornerShape(15.dp))
                        .clip(RoundedCornerShape(15.dp))
                        ,
                    if(it.albumImgId.isDigitsOnly()) IMGID else ALBUM_MID
                )
            }
        }


    fun formatTime(ms: Int): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    val colorLight = color?.lighter() ?: MaterialTheme.colorScheme.secondary
    val colorDark = color?.darker() ?: MaterialTheme.colorScheme.primary
    val colorLightset = color?.lighter()?.lighter(1.5f) ?: MaterialTheme.colorScheme.secondary

    Spacer(modifier = Modifier.height(30.dp))

    songInfo?.let { Text(text = it.title, color = colorDark, style = TextStyle(fontSize = 23.sp), modifier = Modifier.padding(horizontal = 10.dp)) }
    Spacer(modifier = Modifier.height(5.dp))
    songInfo?.let {
        it.singer = it.singer.trimStart()
        Text(text = it.singer.replace(";"," "), color = colorLight, style = TextStyle(fontSize = 18.sp) ,modifier = Modifier.padding(horizontal = 10.dp))
    }
    Spacer(modifier = Modifier.height(20.dp))

    Slider(
        value = currentPosition.toFloat(),
        valueRange = 0f..duration.toFloat(),
        onValueChange = {
            currentPosition = it.toInt()
            musicService?.seekTo(currentPosition)
        },
        modifier = Modifier.padding(horizontal = 10.dp),
        colors = SliderDefaults.colors(thumbColor = colorDark,activeTrackColor = colorLight,inactiveTrackColor = colorLightset)
    )
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 15.dp)) {
        Text(
            text = formatTime(currentPosition),
            modifier = Modifier.align(Alignment.CenterStart)
        )
        Text(
            text = formatTime(duration),
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }

    Spacer(modifier = Modifier.height(25.dp))
    RowHorizontal {
        IconButton(
            onClick = {

                musicService?.stopMusic()
                val info = musicService?.playPrevious()
                musicViewModel.songInfo.value = info?.let { SongInfo(title = it.title, songId = "", singer = info.singer, album = info.album, albumImgId = info.albumImgId) }
                if (info != null) {
                    musicViewModel.currentSongUrl.value = info.url
                }
                if (info != null) {
                    musicViewModel.songmid.value = info.songmid
                }

            }, modifier = Modifier.size(50.dp), //colors = IconButtonDefaults.iconButtonColors(colorDark)
        ) {
            Icon(painterResource(id = R.drawable.skip_previous), contentDescription = "",modifier = Modifier.size(30.dp), tint = colorDark)
        }
        Spacer(modifier = Modifier.width(30.dp))
        FilledTonalButton(
            onClick = {
                playing = if (playing) {
                    musicService?.pauseMusic()
                    false
                } else {
                    musicService?.playMusic(songUrl)
                    true
                }
                musicViewModel.isPlaying.value = playing // 更新 ViewModel 的播放状态
                musicViewModel.currentSongUrl.value = songUrl // 更新 ViewModel 的歌曲 URL
                musicViewModel.songInfo.value = songInfo
            },
           // modifier = Modifier.size(50.dp),
           // enabled = canPlay ,
            colors = ButtonDefaults.filledTonalButtonColors(colorLightset)
        ) {
           // Text(text = "播放")
            if(canPlay) {
                Icon(painterResource(id = if(playing) R.drawable.pause else R.drawable.play_arrow), contentDescription = "",modifier = Modifier.size(30.dp), tint = colorLightset.adjustColorForIcon()
                )
            } else {
                Icon(painterResource(id = if(playing) R.drawable.pause else R.drawable.progress_activity), contentDescription = "",modifier = Modifier.size(30.dp),tint = colorLightset.adjustColorForIcon()
                )
            }
        }
        Spacer(modifier = Modifier.width(30.dp))

        IconButton(
            onClick = {
                musicService?.stopMusic()
                val info = musicService?.playNext()
                musicViewModel.songInfo.value = info?.let { SongInfo(title = it.title, songId = "", singer = info.singer, album = info.album, albumImgId = info.albumImgId) }
                if (info != null) {
                    musicViewModel.currentSongUrl.value = info.url
                }
                if (info != null) {
                    musicViewModel.songmid.value = info.songmid
                }
            },modifier = Modifier.size(50.dp),//colors = IconButtonDefaults.iconButtonColors(colorDark)
        ) {
            Icon(painterResource(id = R.drawable.skip_next), contentDescription = "",modifier = Modifier.size(30.dp), tint = colorDark)
        }
      //  Spacer(modifier = Modifier.width(30.dp))


    }
    Spacer(modifier = Modifier.height(25.dp))
    val pad = 10.dp
    RowHorizontal {
        IconButton(
            onClick = {
                playing = false
                musicViewModel.isPlaying.value = playing
                musicService?.stopMusic()
            },modifier = Modifier.size(50.dp)
        ) {
            Icon(painterResource(id = R.drawable.replay), contentDescription = "",modifier = Modifier.size(30.dp),tint = colorDark)
        }
        Spacer(modifier = Modifier.width(pad))
        IconButton(
            onClick = {
                MyToast(MyApplication.context.getString(R.string.developing))
            },modifier = Modifier.size(50.dp)
        ) {
            Icon(painterResource(id = R.drawable.system_update_alt), contentDescription = "",modifier = Modifier.size(30.dp),tint = colorDark)
        }
        Spacer(modifier = Modifier.width(pad))
        IconButton(
            onClick = {
                MyToast(MyApplication.context.getString(R.string.developing))
            },modifier = Modifier.size(50.dp)
        ) {
            Icon(painterResource(id = R.drawable.album), contentDescription = "",modifier = Modifier.size(30.dp),tint = colorDark)
        }
        Spacer(modifier = Modifier.width(pad))
        IconButton(
            onClick = {
                MyToast(MyApplication.context.getString(R.string.developing))
            },modifier = Modifier.size(50.dp)
        ) {
            Icon(painterResource(id = R.drawable.add_circle), contentDescription = "",modifier = Modifier.size(30.dp),tint = colorDark)
        }
        Spacer(modifier = Modifier.width(pad))
        IconButton(
            onClick = {
                MyToast(MyApplication.context.getString(R.string.developing))
            },modifier = Modifier.size(50.dp)
        ) {
            Icon(painterResource(id = R.drawable.shuffle), contentDescription = "",modifier = Modifier.size(30.dp),tint = colorDark)
        }
    }
}

