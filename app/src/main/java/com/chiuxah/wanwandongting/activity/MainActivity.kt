package com.chiuxah.wanwandongting.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import com.chiuxah.wanwandongting.MusicService
import com.chiuxah.wanwandongting.logic.dao.SongListDB
import com.chiuxah.wanwandongting.ui.activity.HomeUI
import com.chiuxah.wanwandongting.ui.theme.WanwanDongtingTheme
import com.chiuxah.wanwandongting.ui.utils.TransparentSystemBars
import com.chiuxah.wanwandongting.viewModel.MusicViewModel
import com.chiuxah.wanwandongting.viewModel.MyViewModel
class MainActivity : ComponentActivity() {
    private val vm by lazy { ViewModelProvider(this).get(MyViewModel::class.java) }
    private val vmMusic by lazy { ViewModelProvider(this).get(MusicViewModel::class.java) }

    private var musicService: MusicService? = null
    private var isBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MusicBinder
            musicService = binder.getService()
            isBound = true
            // 更新 UI 或通知 Composable 函数
            updateUI()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // 启动并绑定服务
        Intent(this, MusicService::class.java).also { intent ->
            startService(intent)
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
        vmMusic.updatePlayState(false, null)

        // 初次设置 UI，musicService 可能仍然为 null
        setContent {
            WanwanDongtingTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TransparentSystemBars()
                    HomeUI(vm, vmMusic, musicService)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
        }
    }

    private fun updateUI() {
        // 重新设置内容，确保传递最新的 musicService
        setContent {
            WanwanDongtingTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TransparentSystemBars()
                    HomeUI(vm, vmMusic, musicService)
                }
            }
        }
    }
}
