package com.chiuxah.wanwandongting.ui.activity

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.chiuxah.wanwandongting.service.MusicService
import com.chiuxah.wanwandongting.MyApplication
import com.chiuxah.wanwandongting.R
import com.chiuxah.wanwandongting.logic.dao.SongListDataBaseManager
import com.chiuxah.wanwandongting.logic.bean.ListInfo
import com.chiuxah.wanwandongting.logic.bean.ListInfoResponse
import com.chiuxah.wanwandongting.logic.bean.SingleSongInfo
import com.chiuxah.wanwandongting.logic.bean.SongListItem
import com.chiuxah.wanwandongting.logic.bean.SongInfo
import com.chiuxah.wanwandongting.logic.utils.reEmptyLiveDta
import com.chiuxah.wanwandongting.ui.utils.components.BottomTip
import com.chiuxah.wanwandongting.ui.utils.components.MyCard
import com.chiuxah.wanwandongting.ui.utils.components.MyToast
import com.chiuxah.wanwandongting.ui.utils.style.Round
import com.chiuxah.wanwandongting.ui.utils.style.RowHorizontal
import com.chiuxah.wanwandongting.ui.utils.components.ScrollText
import com.chiuxah.wanwandongting.ui.utils.style.TextFiledTransplant
import com.chiuxah.wanwandongting.viewModel.MusicViewModel
import com.chiuxah.wanwandongting.viewModel.MyViewModel
import com.google.gson.Gson
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun parseListId(input: String): String? {
    // 检查输入是否全为数字
    if (input.all { it.isDigit() }) {
        return input // 直接返回输入的数字字符串
    }

    // 如果不是数字，则尝试从链接中提取 id
    val regex = """[?&]id=([^&]*)""".toRegex()
    val id = regex.find(input)?.groupValues?.get(1)
    return if (id != null) {
        if (id.all { it.isDigit() }) {
            id // 直接返回输入的数字字符串
        } else null
    } else null
}

//导入歌单
@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun listUI(vm : MyViewModel,vmMusic : MusicViewModel,musicService: MusicService?) {
    var input by remember { mutableStateOf("") }

    var title by remember { mutableStateOf("") }

    var loading by remember { mutableStateOf(false) }
    var hasParsed by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text(stringResource(id = R.string.lead_to_list)) },
                actions = {
                    FilledTonalButton(onClick = {
                        //保存歌单ID到数据库
                        parseListId(input)?.let { SongListDataBaseManager.addItem(it.toLongOrNull() ?: 0L,title) }
                        MyToast( parseListId(input).toString() +  " " + MyApplication.context.getString(R.string.save_successful))
                    },
                        enabled = hasParsed,
                        modifier = Modifier.padding(horizontal = 15.dp)
                    ) {
                        Text(text = stringResource(id = R.string.save_list))
                    }
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Row() {
                TextField(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 15.dp),
                    value = input,
                    onValueChange = {
                        input = it
                    },
                    label = { Text(stringResource(id = R.string.lead_song_textfield_tip) ) },
                    singleLine = true,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                CoroutineScope(Job()).launch{
                                    async {
                                        reEmptyLiveDta(vm.songListResponse)
                                        loading = true
                                    }.await()
                                    async{ parseListId(input)?.let { vm.getListInfo(it) } }.await()
                                    async {
                                        Handler(Looper.getMainLooper()).post{
                                            vm.songListResponse.observeForever { result ->
                                                if (result != null) {
                                                    if(result.contains("success")) {
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

                    colors = TextFiledTransplant(),

                )
            }

            if(!hasParsed)
            Row(modifier = Modifier.padding(horizontal = 15.dp, vertical = 5.dp)) {
                BottomTip(str = stringResource(id = R.string.lead_to_tips))
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
            AnimatedVisibility(
                visible = !loading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                val songList = getListSongs(vm)
                val listInfo = getListInfo(vm)
                if (listInfo != null) {
                    title = listInfo.title.toString()
                }
                if (songList != null) { hasParsed = songList.isNotEmpty() }
                ListInfos(vmMusic,songList,vm,musicService,listInfo)
            }
        }
    }
}


fun getList(vm: MyViewModel,id : String) = vm.getListInfo(id)

fun getListSongs(vm : MyViewModel) : List<SongListItem>? {
    return try {
        val json = vm.songListResponse.value
        val data = Gson().fromJson(json, ListInfoResponse::class.java).result.list
        data
    } catch (e:Exception) {
        Log.d("e",e.toString())
        null
    }
}

fun getListInfo(vm : MyViewModel) : ListInfo? {
    return try {
        val json = vm.songListResponse.value
        val data = Gson().fromJson(json, ListInfoResponse::class.java).result.info
        data
    } catch (e:Exception) {
        Log.d("e",e.toString())
        null
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListInfos(vmMusic : MusicViewModel, songList : List<SongListItem>?, vm: MyViewModel, musicService : MusicService?, listInfo: ListInfo?) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

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
    Spacer(modifier = Modifier.height(5.dp))
    LazyColumn {
//        item {
//         //   MyCard {
//                ListItem(
//                    headlineContent = {
//                        if (listInfo != null) {
//                            listInfo.title?.let { Text(text = it) }
//                        }
//                    },
//                    leadingContent = {
//                        Image(
//                            painter = rememberAsyncImagePainter(
//                                model = listInfo?.pictureUrl,
//                                placeholder = painterResource(id = R.drawable.ic_launcher_background),
//                                error = painterResource(id = R.drawable.ic_launcher_background)
//                            ),
//                            contentDescription = "" ,
//                            modifier = Modifier
//                                .clip(RoundedCornerShape(7.dp))
//                                .size(80.dp)
//                        )
//                    }
//                )
//            //}
//        }
        songList?.let {
            items(it.size) { index ->

                var singersName = ""
                val singerList= songList[index].singer
                if (singerList != null) {
                    for(i in singerList.indices) {
                        singersName += (" " + singerList[i].title)
                    }
                }
                MyCard {
                //Divider()
                var url : String? by remember { mutableStateOf(null) }
                val songId = songList[index].id.toString()
                val title = songList[index].name ?: ""
                val singer = singersName
                val album =  songList[index].album
                val songmid = songList[index].mid.toString()
                ListItem(
                    headlineContent = { songList[index].name?.let { it1 -> Text(text = it1) } },
                    supportingContent = { songList[index].album.name?.let { it1 -> ScrollText(text = it1) } },
                    overlineContent = { ScrollText(text = singersName) },
                    leadingContent = { AlbumImg(
                          albumImgId = songList[index].album.mid ?: "",
                          modifier = Modifier
                              .clip(RoundedCornerShape(7.dp))
                              .size(80.dp),
                          apiType = AlbumImgApiType.ALBUM_MID
                    ) },
                    modifier = Modifier.clickable {
                        vmMusic.songInfo.value = SongInfo(
                            songId,
                            title,
                            singer,
                            album=album.name.toString(),
                            albumImgId = album.mid ?: "",
                            )
                        vmMusic.songmid.value = songList[index].mid
                        showBottomSheet = true
                    },
                      trailingContent = {
                         FilledTonalIconButton(onClick = {
                             //添加到播放队列
                             CoroutineScope(Job()).launch {
                                 async { url = getSongUrl(songmid,vm) }.await()
                                 async {
                                     if(url != null) {
                                         val singleSong = SingleSongInfo(singer = singer, title = title, albumImgId = album.mid ?: "", album = album.name.toString(), songmid = songmid, url = url)
                                         musicService?.addSongToPlaylist(singleSong)
                                     } else {
                                         MyToast(MyApplication.context.getString(R.string.add_false))
                                     }
                                 }
                             }
                       }) {
                         Icon(painterResource(id = R.drawable.playlist_add), contentDescription = "")
                    }
                    },
                    colors = ListItemDefaults.colors()
                )
                  }
            }
        }
    }
}


suspend fun getSongUrl(songmid: String, vm: MyViewModel): String? {
    return withContext(Dispatchers.IO) {
        var url: String = ""
        val job = CompletableDeferred<String?>()

        try {
            // Launch the coroutine and perform network request
            CoroutineScope(Dispatchers.Main).launch {
               // Log.d("1",vm.songUrlResponse.value.toString())
               vm.songUrlResponse.value = ""
                //Log.d("2",vm.songUrlResponse.value.toString())
                vm.getSongUrl(songmid)
                vm.songUrlResponse.observeForever { result ->
                    if (result != null && result.contains("success")) {
                    //    Log.d("3",vm.songUrlResponse.value.toString())
                        url = Gson().fromJson(result, GetSongUrlResponse::class.java).songUrl ?: ""
                        job.complete(url)
                    }
                }
            }
        } catch (e: Exception) {
            job.complete(null)
        }

        // Wait for the job to complete
        job.await()

        // 检查是否为有效链接
        if (url.contains("http")) {
            if (url.contains("vkey")) {
                url
            } else {
                Log.e("URL NOT CONTAIN", url)
                null
            }
        } else {
            Log.e("URL ERROR", url)
            null
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun savedListUI(vm: MyViewModel, listId : Long, vmMusic: MusicViewModel, musicService: MusicService?, listTitle : String) {
    var input by remember { mutableStateOf("") }

    var loading by remember { mutableStateOf(true) }
    var num by remember { mutableStateOf(0) }

    if(count == 0) {
        CoroutineScope(Job()).launch{
            async { loading = true }.await()
            async{ vm.getListInfo(listId.toString())  }.await()
            async {
                Handler(Looper.getMainLooper()).post{
                    vm.songListResponse.observeForever { result ->
                        if (result != null) {
                            if(result.contains("success")) {
                                loading = false
                                count++
                            }
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { ScrollText(listTitle) },
                actions = {
                    TextButton(onClick = { /*TODO*/ }) {
                        Text(text = "共 $num 首")
                    }
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
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
            AnimatedVisibility(
                visible = !loading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                val list = getListSongs(vm)
                val listInfo = getListInfo(vm)
                if (list != null) {
                    num = list.size
                }
                val newList = mutableListOf<SongListItem>()
                Column {
                    Row() {
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
                                    }) {
                                    Icon(painter = painterResource(R.drawable.search), contentDescription = "description")
                                }
                            },
                            shape = MaterialTheme.shapes.medium,
                            colors = TextFiledTransplant(),
                        )
                    }

                    if (list != null) {
                        list.forEach { item->
                            if(item.name?.contains(input) == true || item.singer.toString().contains(input) || item.album.name.toString().contains(input)) {
                                newList.add(item)
                            }
                        }
                    }

                    ListInfos(vmMusic,newList,vm, musicService, listInfo)
                }

            }
        }
    }
}


