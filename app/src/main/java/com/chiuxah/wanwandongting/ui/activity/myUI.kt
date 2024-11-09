package com.chiuxah.wanwandongting.ui.activity

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.chiuxah.wanwandongting.MyApplication
import com.chiuxah.wanwandongting.R
import com.chiuxah.wanwandongting.ui.utils.ActivedTopBar
import com.chiuxah.wanwandongting.ui.utils.DividerText
import com.chiuxah.wanwandongting.ui.utils.MyToast
import com.chiuxah.wanwandongting.ui.utils.Round
import com.chiuxah.wanwandongting.ui.utils.ScrollText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun myUI(innerPadding: PaddingValues) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    if(showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            //shape = Round(sheetState)
        ) {

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
            modifier = Modifier.clickable { MyToast(MyApplication.context.getString(R.string.developing)) }
        )
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
