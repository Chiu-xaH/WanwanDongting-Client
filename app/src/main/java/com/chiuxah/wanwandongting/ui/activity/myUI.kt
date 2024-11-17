package com.chiuxah.wanwandongting.ui.activity

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.chiuxah.wanwandongting.MusicService
import com.chiuxah.wanwandongting.R
import com.chiuxah.wanwandongting.logic.dao.SongListManager
import com.chiuxah.wanwandongting.ui.utils.ActivedTopBar
import com.chiuxah.wanwandongting.ui.utils.DividerText
import com.chiuxah.wanwandongting.ui.utils.LittleDialog
import com.chiuxah.wanwandongting.ui.utils.MyCard
import com.chiuxah.wanwandongting.ui.utils.Round
import com.chiuxah.wanwandongting.viewModel.MusicViewModel
import com.chiuxah.wanwandongting.viewModel.MyViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun myUI(innerPadding: PaddingValues,vm : MyViewModel,vmMusic : MusicViewModel,musicService : MusicService?) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    var showBottomSheet_delete by remember { mutableStateOf(false) }

    var listId by remember { mutableStateOf(0L) }
    var id by remember { mutableStateOf(8888) }
    var title by remember { mutableStateOf("") }
    if(showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            shape = Round(sheetState)
        ) {
            listUI(vm, vmMusic,musicService)
        }
    }

    if(showBottomSheet_delete) {
        LittleDialog(
            onDismissRequest = { showBottomSheet_delete = false },
            onConfirmation = {
                SongListManager.remove(id)
                showBottomSheet_delete = false
                             },
            dialogTitle = stringResource(id = R.string.delete_list_dialog_title),
        )
    }

    val sheetState_list = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet_list by remember { mutableStateOf(false) }
    if(showBottomSheet_list) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_list = false },
            sheetState = sheetState_list,
            shape = Round(sheetState_list)
        ) {
            count = 0
            savedListUI(vm,listId,vmMusic,musicService,title)
        }
    }
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        ActivedTopBar(title = stringResource(id = R.string.home_bottom_bar_my))
        DividerText(text = "歌单")
        ListItem(
            headlineContent = { Text(text = stringResource(id = R.string.lead_to_list)) },
            leadingContent = {
                Icon(painterResource(id = R.drawable.library_music), contentDescription = "")
            },
            modifier = Modifier.clickable { showBottomSheet = true }
        )
        val lists = SongListManager.queryAll()
        for(i in lists.indices) {
            MyCard {
                ListItem(
                    headlineContent = { Text(text = lists[i].name) },
                    supportingContent = { Text(text = lists[i].listId.toString()) },
                    leadingContent = {
                        //Icon(painterResource(id = R.drawable.queue_music), contentDescription = "")
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = ""//用保存URL
                                 ,
                                placeholder = painterResource(id = R.drawable.ic_launcher_background),
                                error = painterResource(id = R.drawable.ic_launcher_background)
                            ),
                            contentDescription = "" ,
                            modifier = Modifier
                                .clip(RoundedCornerShape(7.dp))
                                .size(80.dp)
                        )
                    },
                    modifier = Modifier.combinedClickable(
                        onLongClick = {
                            id = lists[i].id
                            showBottomSheet_delete = true
                        },
                        onDoubleClick = {},
                        onClick = {
                            listId = lists[i].listId
                            title = lists[i].name
                            count = 0
                            showBottomSheet_list = true
                        }
                    )
                )
            }
        }
        DividerText(text = stringResource(id = R.string.settings))
        ListItem(
            headlineContent = { Text(text = stringResource(id = R.string.update)) },
            leadingContent = {
                Icon(painterResource(id = R.drawable.arrow_upward), contentDescription = "")
            },
            supportingContent = { Text(text = stringResource(id = R.string.developing)) },
            modifier = Modifier.clickable {  }
        )
        Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding()))
    }
}
