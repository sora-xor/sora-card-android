package jp.co.soramitsu.oauth.feature.gatehub.onboarding

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.oauth.base.navigation.SetActivityResult
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardResult
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType

@HiltViewModel
class GatehubOnboardingProgressViewModel @Inject constructor(
    private val setActivityResult: SetActivityResult,
) : BaseViewModel() {

    init {
        mToolbarState.value = SoramitsuToolbarState(
            type = SoramitsuToolbarType.Small(),
            basic = BasicToolbarState(
                title = R.string.common_onboarding,
                visibility = true,
                navIcon = R.drawable.ic_toolbar_back,
            ),
        )
    }

    override fun onToolbarNavigation() {
        super.onToolbarNavigation()
        setActivityResult.setResult(
            soraCardResult = SoraCardResult.Canceled,
        )
    }
}
