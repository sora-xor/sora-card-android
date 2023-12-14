package jp.co.soramitsu.oauth.common.navigation.flow.api.destinations

sealed class KycRequirementsUnfulfilledDestination(destination: String) : NavigationFlowDestination(
    destination,
) {
    class CardIssuanceOptionsScreen(
        destination: String = "/CardIssuanceOptions",
    ) : KycRequirementsUnfulfilledDestination(destination)

    class GetMoreXorDialog(
        destination: String = "/GetMoreXorDialog",
    ) : KycRequirementsUnfulfilledDestination(destination)
}
