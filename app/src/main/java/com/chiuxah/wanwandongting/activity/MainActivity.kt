package com.chiuxah.wanwandongting.activity

import android.os.Bundle
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
import com.chiuxah.wanwandongting.ui.activity.HomeUI
import com.chiuxah.wanwandongting.ui.theme.WanwanDongtingTheme
import com.chiuxah.wanwandongting.ui.utils.TransparentSystemBars
import com.chiuxah.wanwandongting.viewModel.MyViewModel

class MainActivity : ComponentActivity() {
    private val vm by lazy { ViewModelProvider(this).get(MyViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            WanwanDongtingTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TransparentSystemBars()
                    HomeUI(vm)
                }
            }
        }
    }
}
