package jp.co.soramitsu.oauth.common.interactors.account.api

import jp.co.soramitsu.oauth.theme.views.Text

sealed interface AccountOperationResult {

    object Executed: AccountOperationResult

    @JvmInline
    value class Error(
        val text: Text
    ): AccountOperationResult

}