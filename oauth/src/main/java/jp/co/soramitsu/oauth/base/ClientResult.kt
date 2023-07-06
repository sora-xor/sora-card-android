package jp.co.soramitsu.oauth.base

import android.app.Activity
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardResult

fun mapSoraCardResult(result: SoraCardResult) =
    when (result) {
        is SoraCardResult.Success, is SoraCardResult.NavigateTo, SoraCardResult.Logout -> {
            Activity.RESULT_OK
        }

        is SoraCardResult.Failure, SoraCardResult.Canceled -> {
            Activity.RESULT_CANCELED
        }
    }
