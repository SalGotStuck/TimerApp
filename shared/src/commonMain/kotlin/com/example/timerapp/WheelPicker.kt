package com.example.timerapp

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.filter

/**
 * A scrollable wheel (like the iPhone timer) that lets the user pick a number in [range].
 * Three rows are visible; the middle (highlighted) row is the selected value.
 */
@Composable
fun WheelPicker(
    value: Int,
    range: IntRange,
    label: String,
    enabled: Boolean,
    onValueChange: (Int) -> Unit,
) {
    val itemHeight = 48.dp
    val itemHeightPx = with(LocalDensity.current) { itemHeight.toPx() }
    val count = range.last - range.first + 1

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = value - range.first)
    val flingBehavior = rememberSnapFlingBehavior(listState, SnapPosition.Start)

    // Index of the number currently sitting in the middle row.
    val centeredIndex by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex +
                    if (listState.firstVisibleItemScrollOffset > itemHeightPx / 2) 1 else 0
        }
    }

    val currentValue by rememberUpdatedState(value)
    val currentOnValueChange by rememberUpdatedState(onValueChange)

    // When the user stops scrolling, report the new value.
    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .filter { inProgress -> !inProgress }
            .collect {
                val picked = range.first + centeredIndex.coerceIn(0, count - 1)
                if (picked != currentValue) currentOnValueChange(picked)
            }
    }

    // When the value changes from outside (e.g. Reset), scroll the wheel to match.
    LaunchedEffect(value) {
        val target = value - range.first
        if (!listState.isScrollInProgress && centeredIndex != target) {
            listState.animateScrollToItem(target)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.alpha(if (enabled) 1f else 0.4f),
    ) {
        Box(Modifier.width(72.dp).height(itemHeight * 3)) {
            // Highlight behind the selected (middle) row.
            Box(
                Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .height(itemHeight)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
            )
            LazyColumn(
                state = listState,
                flingBehavior = flingBehavior,
                userScrollEnabled = enabled,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize(),
            ) {
                // Blank rows above and below so the first and last numbers can reach the middle.
                item { Spacer(Modifier.height(itemHeight)) }
                items(count) { i ->
                    val selected = i == centeredIndex
                    Box(
                        Modifier.fillMaxWidth().height(itemHeight),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = (range.first + i).toString().padStart(2, '0'),
                            fontSize = if (selected) 28.sp else 20.sp,
                            color = if (selected) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
                            },
                        )
                    }
                }
                item { Spacer(Modifier.height(itemHeight)) }
            }
        }
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}