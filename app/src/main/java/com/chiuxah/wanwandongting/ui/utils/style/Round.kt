package com.chiuxah.wanwandongting.ui.utils.style

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.chiuxah.wanwandongting.MyApplication

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Round(sheetState: SheetState) : RoundedCornerShape {
    return RoundedCornerShape(RoundDp(sheetState))
}
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun RoundDp(sheetState : SheetState) : Dp {
    val dpAnimation by animateDpAsState(
        targetValue = if (sheetState.currentValue != SheetValue.Expanded) 28.dp else 0.dp, label = ""
        ,animationSpec = tween(MyApplication.animationSpeed / 2, easing = LinearOutSlowInEasing),
    )
    return dpAnimation
}