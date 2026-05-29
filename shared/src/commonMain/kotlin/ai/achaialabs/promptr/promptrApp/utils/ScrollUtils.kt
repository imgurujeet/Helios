package ai.achaialabs.promptr.promptrApp.utils

import ai.achaialabs.promptr.promptrApp.ui.navigation.ChromeState
import androidx.compose.foundation.lazy.LazyListState
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