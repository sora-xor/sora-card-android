package jp.co.soramitsu.oauth.common.navigation.flow.verification.api


sealed interface VerificationDestination {

    object Start: VerificationDestination

    object VerificationInProgress: VerificationDestination

    object VerificationSuccessful: VerificationDestination

    object VerificationRejected: VerificationDestination

}