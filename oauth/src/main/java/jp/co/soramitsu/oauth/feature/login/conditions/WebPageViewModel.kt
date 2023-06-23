package jp.co.soramitsu.oauth.feature.login.conditions

import androidx.compose.runtime.mutableStateOf
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.common.navigation.flow.login.api.LoginFlow
import jp.co.soramitsu.oauth.common.navigation.flow.verification.api.VerificationFlow
import jp.co.soramitsu.oauth.feature.login.conditions.model.WebPageState
import jp.co.soramitsu.oauth.feature.login.conditions.model.WebUrl
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType

@HiltViewModel
class WebPageViewModel @Inject constructor(
    private val loginFlow: LoginFlow
) : BaseViewModel() {

    var state = mutableStateOf(WebPageState())
        private set

    init {
        _toolbarState.value = SoramitsuToolbarState(
            type = SoramitsuToolbarType.Small(),
            basic = BasicToolbarState(
                title = "",
                visibility = true,
                navIcon = R.drawable.ic_toolbar_back
            ),
        )
    }

    fun setArgs(title: String, webUrl: String) {
        _toolbarState.value = _toolbarState.value?.copy(
            basic = BasicToolbarState(
                title = title,
                visibility = true,
                navIcon = R.drawable.ic_toolbar_back
            )
        )
        state.value = state.value.copy(url = WebUrl.valueOf(webUrl).url)
    }

    fun onFinishLoading() {
        state.value = state.value.copy(loading = false)
    }

    override fun onToolbarNavigation() {
        loginFlow.onBack()
    }
}
