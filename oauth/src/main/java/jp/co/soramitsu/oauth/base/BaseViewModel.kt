package jp.co.soramitsu.oauth.base

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.co.soramitsu.oauth.base.uiscreens.DialogAlertState
import jp.co.soramitsu.ui_core.component.toolbar.Action
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState

open class BaseViewModel : ViewModel() {

    protected val mToolbarState = MutableLiveData<SoramitsuToolbarState>()
    val toolbarState: LiveData<SoramitsuToolbarState> = mToolbarState

    var dialogState by mutableStateOf<DialogAlertState?>(null)
        protected set

    open fun onToolbarAction() = Unit

    open fun onToolbarNavigation() = Unit

    open fun onToolbarMenuItemSelected(action: Action) = Unit
    open fun onToolbarSearch(value: String) = Unit
}
