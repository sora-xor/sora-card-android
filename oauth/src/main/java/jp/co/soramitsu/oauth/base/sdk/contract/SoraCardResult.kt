package jp.co.soramitsu.oauth.base.sdk.contract

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class SoraCardCommonVerification {
    Failed, Rejected, Pending, Successful, Retry, Started, NotFound
}

@Parcelize
sealed class SoraCardResult : Parcelable {

    @Parcelize
    data class Success(
        val status: SoraCardCommonVerification,
    ) : SoraCardResult()

    @Parcelize
    data class Failure(
        val status: SoraCardCommonVerification,
        val error: SoraCardError? = null
    ) : SoraCardResult()

    @Parcelize
    object Canceled : SoraCardResult()

    @Parcelize
    object Logout : SoraCardResult()

    @Parcelize
    data class NavigateTo(
        val screen: OutwardsScreen
    ) : SoraCardResult()
}

enum class OutwardsScreen {
    DEPOSIT, SWAP, BUY
}

enum class SoraCardError {

    USER_NOT_FOUND,
    EMAIL_VERIFICATION_NOT_FINISHED
}
