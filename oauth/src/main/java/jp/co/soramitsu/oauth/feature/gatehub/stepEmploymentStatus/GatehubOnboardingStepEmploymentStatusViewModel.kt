package jp.co.soramitsu.oauth.feature.gatehub.stepEmploymentStatus

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import jp.co.soramitsu.androidfoundation.format.TextValue
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.navigation.SetActivityResult
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardResult
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class GatehubOnboardingStepEmploymentStatusViewModel @Inject constructor(
    private val mainRouter: MainRouter,
    private val setActivityResult: SetActivityResult,
    private val inMemoryRepo: InMemoryRepo,
) : BaseViewModel() {

    private val statuses: List<TextValue> = listOf(
        TextValue.StringRes(id = R.string.gatehub_item_employed),
        TextValue.StringRes(id = R.string.gatehub_item_student),
        TextValue.StringRes(id = R.string.gatehub_item_selfemployed),
        TextValue.StringRes(id = R.string.gatehub_item_unemployed),
        TextValue.StringRes(id = R.string.gatehub_item_retired),
    )

    private val _state = MutableStateFlow(
        value = GatehubOnboardingStepEmploymentStatusState(
            buttonEnabled = false,
            statuses = statuses,
            selectedPos = null,
        ),
    )
    internal val state = _state.asStateFlow()

    init {
        mToolbarState.value = SoramitsuToolbarState(
            type = SoramitsuToolbarType.Small(),
            basic = BasicToolbarState(
                title = R.string.onboarding_questions,
                visibility = true,
                navIcon = R.drawable.ic_toolbar_back,
            ),
        )
        inMemoryRepo.ghEmploymentStatus = null
    }

    override fun onToolbarNavigation() {
        super.onToolbarNavigation()
        setActivityResult.setResult(SoraCardResult.Canceled)
    }

    fun onItemSelect(pos: Int) {
        check(pos in 0..statuses.lastIndex)
        inMemoryRepo.ghEmploymentStatus = pos + 1
        _state.update {
            it.copy(buttonEnabled = true, selectedPos = pos)
        }
    }

    fun onNext() {
        mainRouter.openGatehubOnboardingStep1()
    }
}
