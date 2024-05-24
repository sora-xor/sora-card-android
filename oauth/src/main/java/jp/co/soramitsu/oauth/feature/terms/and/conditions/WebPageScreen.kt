package jp.co.soramitsu.oauth.feature.terms.and.conditions

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import jp.co.soramitsu.oauth.base.compose.Screen
import jp.co.soramitsu.ui_core.resources.Dimens
import jp.co.soramitsu.ui_core.theme.customColors

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebPageScreen(
    title: String,
    webUrl: String,
    lastPage: Boolean,
    viewModel: WebPageViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.setArgs(title, webUrl, lastPage)
    }
    BackHandler {
        viewModel.onToolbarNavigation()
    }

    Screen(
        viewModel = viewModel,
    ) {
        val state = viewModel.state

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    WebView(context).apply {
                        settings.domStorageEnabled = true
                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                viewModel.onFinishLoading()
                            }
                        }
                        settings.javaScriptEnabled = true
                        loadUrl(webUrl)
                    }
                },
            )

            if (state.value.loading) {
                ProgressDialog()
            }
        }
    }
}

@Composable
fun ProgressDialog() {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
        ),
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(Dimens.x2))
                .background(MaterialTheme.customColors.bgSurface),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(Dimens.x6)
                    .padding(Dimens.x1),
                color = MaterialTheme.customColors.fgPrimary,
            )
        }
    }
}
