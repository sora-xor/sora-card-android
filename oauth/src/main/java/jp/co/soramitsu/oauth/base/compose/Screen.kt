package jp.co.soramitsu.oauth.base.compose

import android.util.Log
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.ui_core.component.toolbar.Action
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbar
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.theme.customColors

@Composable
fun Screen(
    modifier: Modifier = Modifier,
    viewModel: BaseViewModel,
    backgroundColor: Color = MaterialTheme.customColors.bgSurface,
    content: @Composable (scrollState: ScrollState) -> Unit,
) {
    val scaffoldState = rememberScaffoldState()
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        backgroundColor = backgroundColor,
        topBar = {
            Toolbar(
                toolbarState = viewModel.toolbarState.observeAsState().value,
                scrollState = scrollState,
                backgroundColor = backgroundColor,
                tintColor = MaterialTheme.customColors.fgPrimary,
                onNavClick = viewModel::onToolbarNavigation,
                onActionClick = viewModel::onToolbarAction,
                onMenuItemClick = viewModel::onToolbarMenuItemSelected,
                onSearch = viewModel::onToolbarSearch,
            )
        },
    ) { padding ->
        Log.e("foxx", "scree $padding")
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
        ) {
            content(scrollState)
        }

        viewModel.dialogState?.let { state ->
            DialogAlert(state = state)
        }
    }
}

@Composable
fun Toolbar(
    toolbarState: SoramitsuToolbarState?,
    scrollState: ScrollState?,
    backgroundColor: Color,
    tintColor: Color,
    onNavClick: (() -> Unit)? = null,
    onActionClick: (() -> Unit)? = null,
    onMenuItemClick: ((Action) -> Unit)? = null,
    onSearch: ((String) -> Unit)? = null,
) {
    if (toolbarState != null && toolbarState.basic.visibility) {
        val elevation = remember(scrollState) {
            derivedStateOf {
                if (scrollState == null || scrollState.value == 0) {
                    0.dp
                } else {
                    AppBarDefaults.TopAppBarElevation
                }
            }
        }

        SoramitsuToolbar(
            state = toolbarState,
            elevation = elevation.value,
            backgroundColor = backgroundColor,
            tint = tintColor,
            onNavigate = onNavClick,
            onAction = onActionClick,
            onMenuItemClicked = onMenuItemClick,
            onSearch = onSearch,
        )
    }
}
