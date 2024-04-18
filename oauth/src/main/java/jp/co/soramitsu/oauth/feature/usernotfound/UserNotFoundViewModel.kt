package jp.co.soramitsu.oauth.feature.usernotfound

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class UserNotFoundViewModel @Inject constructor(
    private val mainRouter: MainRouter,
    private val inMemoryRepo: InMemoryRepo,
    private val userSessionRepository: UserSessionRepository,
) : BaseViewModel() {

    private val _state = MutableStateFlow("")
    val state = _state.asStateFlow()

    init {
        mToolbarState.value = SoramitsuToolbarState(
            type = SoramitsuToolbarType.Small(),
            basic = BasicToolbarState(
                title = "",
                visibility = true,
                navIcon = R.drawable.ic_cross,
            ),
        )

        viewModelScope.launch {
            _state.value = userSessionRepository.getPhoneNumber()
        }
    }

    override fun onToolbarNavigation() {
        super.onToolbarNavigation()
        mainRouter.back()
    }

    fun onTryAnotherNumber() {
        mainRouter.openEnterPhoneNumber(true)
    }

    fun onRegisterNewAccount() {
        inMemoryRepo.logIn = false
        mainRouter.openEnterPhoneNumber(true)
    }
}
