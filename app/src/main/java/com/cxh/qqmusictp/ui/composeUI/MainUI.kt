package com.cxh.qqmusictp.ui.composeUI

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.cxh.qqmusictp.MyApplication
import com.cxh.qqmusictp.logic.Prefs
import com.cxh.qqmusictp.logic.dataModel.SearchResponse
import com.cxh.qqmusictp.logic.dataModel.SongsInfo
import com.cxh.qqmusictp.viewModel.MusicViewModel
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


fun Result() : MutableList<SongsInfo> {
    val json = Prefs.prefs.getString("searchJson",MyApplication.Null)
    val data = Gson().fromJson(json, SearchResponse::class.java)
    val song = data.data.song
    val list = song.list
    val page = song.curnum
    val totalPage = song.curpage
    var infos = mutableListOf<SongsInfo>()

    for (i in 0 until list.size) {
        val f = list[i].f.split("|")
       // for (j in 0 until f.size) {
            val songId = f[0]
            val title = f[1]
            val singer = f[3]
            val AlbumImgId = f[4]
            val album = f[5]
            val song = SongsInfo(songId, title, singer, AlbumImgId, album)
            infos.add(song)
      //  }
    }

    return infos
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultLists(vm : MusicViewModel) {

        LazyColumn {

            items(Result().size) {item ->
                Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
                    Spacer(modifier = Modifier.height(100.dp))
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp, vertical = 5.dp),
                        shape = MaterialTheme.shapes.medium,
                        ) {
                        androidx.compose.material3.ListItem(
                            headlineContent = { Text(text = Result()[item].title) },
                            supportingContent = { Text(text = Result()[item].album)},
                            overlineContent = { Text(text = Result()[item].album)},
                            leadingContent = {
                                             AsyncImage(
                                                 model = "${MyApplication.ImgURL}${Result()[item].AlbumImgId.toInt() % 100}/300_albumpic_${Result()[item].AlbumImgId.toInt()}_0.jpg",
                                                 contentDescription = "",
                                                 modifier = Modifier.clip(RoundedCornerShape(7.dp))
                                             )
                            },
                            modifier = Modifier.clickable { vm.getSongmid(Result()[item].songId) }
                        )
                    }
                }
            }
        }


}


@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainUI(vm : MusicViewModel) {
    var loading by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var input by remember { mutableStateOf("") }
    
    if(showBottomSheet) {
        ModalBottomSheet(onDismissRequest = {showBottomSheet = false}, sheetState = sheetState) {
            ResultLists(vm)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("QQ音乐 TP") }
            )
        },) {innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            // .verticalScroll(rememberScrollState())
            .fillMaxSize()) {
            Spacer(modifier = Modifier.height(30.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                TextField(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 15.dp),
                    value = input,
                    onValueChange = {
                        input = it
                    },
                    label = { Text("搜索音乐" ) },
                    singleLine = true,
                    trailingIcon = {
                        IconButton(
                            // shape = RoundedCornerShape(5.dp),
                            onClick = {
                                CoroutineScope(Job()).launch {
                                    async {
                                        //loading = true
                                        vm.searchMusic(input)
                                    }.await()
                                    async {
                                        delay(2000)
                                        Result()
                                    }.await()
                                    async {
                                        delay(2000)
                                        showBottomSheet  = true}
                                }
                            }) {
                            Icon( Icons.Filled.Search, "description")
                        }
                    },
                    shape = MaterialTheme.shapes.medium,
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent, // 有焦点时的颜色，透明
                        unfocusedIndicatorColor = Color.Transparent, // 无焦点时的颜色，绿色
                    ),
                    // leadingIcon = { Icon( painterResource(R.drawable.search), contentDescription = "Localized description") },
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
            
            //ResultLists()
            

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun searchUI(vm : MusicViewModel) {
    var input by remember { mutableStateOf("恶之必要") }


    
}