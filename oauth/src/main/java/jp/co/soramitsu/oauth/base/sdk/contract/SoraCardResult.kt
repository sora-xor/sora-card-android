package jp.co.soramitsu.oauth.base.sdk.contract

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class IbanInfo(
    val iban: String,
    val active: Boolean,
    val balance: String,
)

enum class SoraCardCommonVerification {
    Failed,
    Rejected,
    Pending,
    Successful,
    Retry,
    Started,
    NotFound,
}

@Parcelize
sealed class SoraCardResult : Parcelable {

    @Parcelize
    data class Success(
        val status: SoraCardCommonVerification,
    ) : SoraCardResult()

    @Parcelize
    data object SuccessWithIban : SoraCardResult()

    @Parcelize
    data class Failure(
        val status: SoraCardCommonVerification,
        val error: SoraCardError? = null,
    ) : SoraCardResult()

    @Parcelize
    data object Canceled : SoraCardResult()

    @Parcelize
    data object Logout : SoraCardResult()

    @Parcelize
    data class NavigateTo(
        val screen: OutwardsScreen,
    ) : SoraCardResult()
}

enum class OutwardsScreen {
    DEPOSIT,
    SWAP,
    BUY,
}

enum class SoraCardError {

    USER_NOT_FOUND,
    EMAIL_VERIFICATION_NOT_FINISHED,
}
