package jp.co.soramitsu.oauth.feature.login.conditions

import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.oauth.common.navigation.flow.login.api.LoginFlow
import jp.co.soramitsu.oauth.feature.KycCallback
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import javax.inject.Inject

@HiltViewModel
class TermsAndConditionsViewModel @Inject constructor(
    private val loginFlow: LoginFlow
) : BaseViewModel() {

    init {
        _toolbarState.value = SoramitsuToolbarState(
            type = SoramitsuToolbarType.Small(),
            basic = BasicToolbarState(
                title = R.string.terms_and_conditions_title,
                visibility = true,
                navIcon = R.drawable.ic_cross,
            ),
        )
    }

    fun onGeneralTermsClick() {
        loginFlow.onGeneralTermsClicked()
    }

    fun onPrivacyPolicy() {
        loginFlow.onPrivacyPolicyClicked()
    }

    fun onConfirm() {
        loginFlow.onAcceptTermsAndConditions()
    }

    override fun onToolbarNavigation() {
        loginFlow.onExit()
    }
}
