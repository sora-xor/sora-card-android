package jp.co.soramitsu.oauth.feature.login.web

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.oauth.common.navigation.flow.login.api.LoginDestination
import jp.co.soramitsu.oauth.common.navigation.flow.login.api.LoginFlow
import jp.co.soramitsu.oauth.feature.login.web.model.WebPageState
import jp.co.soramitsu.oauth.feature.login.web.model.WebUrl
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType

@HiltViewModel
class WebPageViewModel @Inject constructor(
    private val loginFlow: LoginFlow
) : BaseViewModel() {

    var state by mutableStateOf(WebPageState())
        private set

    init {
        loginFlow.args[LoginDestination.WebPage::class.java.name]
            .apply {
                if (this == null)
                    return@apply


                _toolbarState.value = SoramitsuToolbarState(
                    type = SoramitsuToolbarType.Small(),
                    basic = BasicToolbarState(
                        title = getInt(LoginDestination.WebPage.TITLE_STRING_RES_KEY),
                        visibility = true,
                        navIcon = R.drawable.ic_toolbar_back
                    ),
                )

                state = state.copy(
                    url = getString(LoginDestination.WebPage.URL_KEY)!!,
                    loading = true
                )
            }
    }

    override fun onToolbarNavigation() {
        loginFlow.onBack()
    }

    fun onFinishLoading() {
        state = state.copy(loading = false)
    }
}
