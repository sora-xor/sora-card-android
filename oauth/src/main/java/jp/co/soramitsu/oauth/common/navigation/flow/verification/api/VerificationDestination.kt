package jp.co.soramitsu.oauth.common.navigation.flow.verification.api

import android.hardware.SensorAdditionalInfo


sealed interface VerificationDestination {

    object Start: VerificationDestination

    object VerificationInProgress: VerificationDestination

    object VerificationSuccessful: VerificationDestination

    class VerificationRejected(
        val additionalInfo: String?
    ): VerificationDestination

    class VerificationFailed(
        val additionalInfo: String?
    ): VerificationDestination

}