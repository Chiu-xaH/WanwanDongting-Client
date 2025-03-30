package com.chiuxah.wanwandongting.ui.activity

import android.annotation.SuppressLint
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.chiuxah.wanwandongting.service.MusicService
import com.chiuxah.wanwandongting.MyApplication
import com.chiuxah.wanwandongting.R
import com.chiuxah.wanwandongting.logic.bean.NavigationBarItemData
import com.chiuxah.wanwandongting.logic.utils.AndroidVersion
import com.chiuxah.wanwandongting.ui.utils.HomeBar
import com.chiuxah.wanwandongting.ui.utils.NavigateUtils.turnToBottomBar
import com.chiuxah.wanwandongting.viewModel.MusicViewModel
import com.chiuxah.wanwandongting.viewModel.MyViewModel
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeUI(vm : MyViewModel,vmMusic : MusicViewModel,musicService: MusicService?) {
    val SEARCH = 0
    val PLAY = 1
    val MY = 2
    val surfaceColor = MaterialTheme.colorScheme.surface
    val navController = rememberNavController()
    val animation = stringResource(id = R.string.animation_speed).toInt()
    val blur by remember { mutableStateOf(AndroidVersion.isSupportedBlur ) }
    val hazeState = remember { HazeState() }
    var bottomBarItems by remember { mutableStateOf(SEARCH) }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        /*topBar = {
            Column {
                TopAppBar(
                    title = { Text(text = stringResource(id = R.string.home_top_bar_title)) },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = //MaterialTheme.colorScheme.primaryContainer.copy(alpha = if(blur).50f else 1f),
                        Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    )
                )
                //Divider()
            }
        },*/

        bottomBar = {
            Column {
                if(!blur) {
                     if(bottomBarItems != PLAY) {
                        Divider()
                    }
                }

                NavigationBar(
                    containerColor =
                   // if(bottomBarItems != PLAY) {
                     //   if(blur) MaterialTheme.colorScheme.primaryContainer.copy(.25f) else ListItemDefaults.containerColor
                   // } else
                    if(blur) Color.Transparent else ListItemDefaults.containerColor

                    ,
                    modifier = Modifier
                        .hazeChild(state = hazeState,
                            style = HazeStyle(
                                tint = HazeTint(color =
                                    if(bottomBarItems != PLAY) {
                                        if(blur) MaterialTheme.colorScheme.surface else ListItemDefaults.containerColor
                                    } else Color.Transparent,
                                ),
                                backgroundColor =  Color.Transparent
                                ,blurRadius = MyApplication.blur,
                                noiseFactor = 0f)
                           // blurRadius = MyApplication.blur, tint = Color.Transparent, noiseFactor = 0f
                        ) {


                            //val colors = listOf<Color>(Color.White.copy(alpha = .5f), Color.Green.copy(alpha = .5f))
//                            mask =  Brush.verticalGradient(
//                                colors = listOf(
//                                    Color.Transparent, // 顶部完全透明
//                                    surfaceColor     // 底部完全白色
//                                )
//                            )
                            progressive = HazeProgressive.verticalGradient(startIntensity = 0f, endIntensity = .7f, startY = 85f, endY = Float.POSITIVE_INFINITY)
                        }
                ) {
                    val items = listOf(
                        NavigationBarItemData(
                            HomeBar.Home.name,
                            stringResource(id = R.string.home_bottom_bar_home),
                            painterResource(id = R.drawable.album),
                            painterResource(id = R.drawable.album_filled),
                        ),
                        NavigationBarItemData(
                            HomeBar.Play.name,
                            stringResource(id = R.string.home_bottom_bar_play),
                            painterResource(id = R.drawable.subscriptions),
                            painterResource(id = R.drawable.subscriptions_filled),
                        ),
                        NavigationBarItemData(
                            HomeBar.My.name,
                            stringResource(id = R.string.home_bottom_bar_my),
                            painterResource(id = R.drawable.person),
                            painterResource(id = R.drawable.person_filled),
                        )
                    )
                    items.forEach { item->
                        val interactionSource = remember { MutableInteractionSource() }
                        val isPressed by interactionSource.collectIsPressedAsState()
                        val scale = animateFloatAsState(
                            targetValue = if (isPressed) 0.8f else 1f, // 按下时为0.9，松开时为1
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                            label = "" // 使用弹簧动画
                        )
                        val route = item.route
                        val selected = navController.currentBackStackEntryAsState().value?.destination?.route == route
                        NavigationBarItem(
                            selected = selected,
                            modifier = Modifier.scale(scale.value),
                            interactionSource = interactionSource,
                            onClick = {
                                //     atEnd = !atEnd
                                if(item == items[0]) bottomBarItems = SEARCH
                                if(item == items[1]) bottomBarItems = PLAY
                                if(item == items[2]) bottomBarItems = MY
                                if (!selected) { turnToBottomBar(navController, route) }
                            },
                           // colors = if(item != items[1]) NavigationBarItemDefaults.colors() else NavigationBarItemDefaults.colors(Color.Transparent),
                            label = { Text(text = item.label) },
                            icon = {
                                BadgedBox(badge = {}) { Icon(if(selected)item.filledIcon else item.icon, contentDescription = item.label) }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding->
        NavHost(
            navController = navController,
            startDestination = HomeBar.Home.name,
            enterTransition = {
                scaleIn(animationSpec = tween(durationMillis = animation)) +
                        expandVertically(expandFrom = Alignment.Top,animationSpec = tween(durationMillis = animation))
            },
            exitTransition = {
                scaleOut(animationSpec = tween(durationMillis = animation)) +
                        shrinkVertically(shrinkTowards = Alignment.Top,animationSpec = tween(durationMillis = animation))
            },
            modifier = Modifier
                .haze(
                    state = hazeState,
                    //backgroundColor = MaterialTheme.colorScheme.surface,
                )
        ) {
            composable(HomeBar.Home.name) {
                Scaffold {
                    ListenUI(innerPadding,vm,vmMusic,musicService)
                }
            }
            composable(HomeBar.Play.name) {
                Scaffold { innerPadding->
                    Column {
                        Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
                        PlayOnUI(vm, musicService,vmMusic)
                        Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding()))
                    }
                }
            }
            composable(HomeBar.My.name) {
                Scaffold {
                    myUI(innerPadding,vm,vmMusic,musicService)
                }
            }
        }
    }
}

