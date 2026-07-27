package ai.achaialabs.helios.heliosApp.utils

import ai.achaialabs.helios.heliosApp.ui.navigation.ChromeState
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow

@Composable
fun ObserveScroll(
    listState: LazyListState,
    chromeState: ChromeState
) {

    LaunchedEffect(listState) {

        var previousIndex = 0
        var previousScrollOffset = 0

        snapshotFlow {

            listState.firstVisibleItemIndex to
                    listState.firstVisibleItemScrollOffset

        }.collect { (index, offset) ->

            val isScrollingDown =

                if (index != previousIndex) {

                    index > previousIndex

                } else {

                    offset > previousScrollOffset
                }

            chromeState.bottomBarVisible =
                !isScrollingDown

            previousIndex = index
            previousScrollOffset = offset
        }
    }
}


@Composable
fun ObserveScroll(
    gridState: LazyGridState,
    chromeState: ChromeState
) {
    LaunchedEffect(gridState) {

        var previousIndex = 0
        var previousOffset = 0

        snapshotFlow {
            gridState.firstVisibleItemIndex to
                    gridState.firstVisibleItemScrollOffset
        }.collect { (index, offset) ->

            val isScrollingDown =
                if (index != previousIndex) {
                    index > previousIndex
                } else {
                    offset > previousOffset
                }

            chromeState.bottomBarVisible = !isScrollingDown

            previousIndex = index
            previousOffset = offset
        }
    }
}

@Composable
fun ObserveScroll(
    scrollState: ScrollState,
    chromeState: ChromeState
) {
    LaunchedEffect(scrollState) {

        var previousOffset = scrollState.value

        snapshotFlow { scrollState.value }
            .collect { offset ->

                val isScrollingDown = offset > previousOffset

                chromeState.bottomBarVisible = !isScrollingDown

                previousOffset = offset
            }
    }
}