package jp.co.soramitsu.oauth.base

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.co.soramitsu.oauth.theme.views.state.DialogAlertState
import jp.co.soramitsu.ui_core.component.toolbar.Action
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState

open class DisposableViewModel : ViewModel() {

    protected val _toolbarState = MutableLiveData<SoramitsuToolbarState>()
    val toolbarState: LiveData<SoramitsuToolbarState> = _toolbarState

    var dialogState by mutableStateOf<DialogAlertState?>(null)
        protected set

    open fun onToolbarAction() = Unit

    open fun onToolbarNavigation() = Unit

    open fun onToolbarMenuItemSelected(action: Action) = Unit

    open fun onStart() = Unit

    open fun onStop() = Unit
}