package jp.co.soramitsu.oauth.feature.terms.and.conditions

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.navigation.SetActivityResult
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardResult
import jp.co.soramitsu.oauth.feature.terms.and.conditions.model.WebUrl
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType

@HiltViewModel
class TermsAndConditionsViewModel @Inject constructor(
    private val mainRouter: MainRouter,
    private val setActivityResult: SetActivityResult,
) : BaseViewModel() {

    init {
        mToolbarState.value = SoramitsuToolbarState(
            type = SoramitsuToolbarType.Small(),
            basic = BasicToolbarState(
                title = R.string.terms_and_conditions_title,
                visibility = true,
                navIcon = R.drawable.ic_cross,
            ),
        )
    }

    fun onGeneralTermsClick() {
        mainRouter.openWebPage(
            titleRes = R.string.terms_and_conditions_general_terms,
            url = WebUrl.GENERAL_TERMS,
        )
    }

    fun onPrivacyPolicy() {
        mainRouter.openWebPage(
            titleRes = R.string.terms_and_conditions_privacy_policy,
            url = WebUrl.PRIVACY_POLICY,
        )
    }

    fun onConfirm() {
        mainRouter.openEnterPhoneNumber()
    }

    override fun onToolbarNavigation() {
        setActivityResult.setResult(SoraCardResult.Canceled)
    }
}
