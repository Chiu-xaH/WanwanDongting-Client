package com.cxh.qqmusictp.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import com.cxh.qqmusictp.ui.TransparentSystemBars
import com.cxh.qqmusictp.ui.composeUI.MainUI
import com.cxh.qqmusictp.ui.theme.QQMusicTPTheme
import com.cxh.qqmusictp.viewModel.MusicViewModel

class MainActivity : ComponentActivity() {
    private val vm by lazy { ViewModelProvider(this).get(MusicViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            QQMusicTPTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TransparentSystemBars()
                    MainUI(vm)
                }
            }
        }
    }
}
