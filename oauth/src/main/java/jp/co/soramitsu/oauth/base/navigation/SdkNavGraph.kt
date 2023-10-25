package jp.co.soramitsu.oauth.base.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import jp.co.soramitsu.oauth.common.navigation.flow.api.destinations.KycRequirementsUnfulfilledDestination
import jp.co.soramitsu.oauth.feature.OAuthCallback
import jp.co.soramitsu.oauth.feature.cardissuance.CardIssuanceScreen
import jp.co.soramitsu.oauth.feature.change.email.ChangeEmailScreen
import jp.co.soramitsu.oauth.feature.getmorexor.ChooseXorPurchaseMethodDialog
import jp.co.soramitsu.oauth.feature.getprepared.GetPreparedScreen
import jp.co.soramitsu.oauth.feature.kyc.result.VerificationFailedScreen
import jp.co.soramitsu.oauth.feature.kyc.result.VerificationInProgressScreen
import jp.co.soramitsu.oauth.feature.kyc.result.VerificationSuccessfulScreen
import jp.co.soramitsu.oauth.feature.kyc.result.verificationrejected.VerificationRejectedScreen
import jp.co.soramitsu.oauth.feature.registration.RegisterUserScreen
import jp.co.soramitsu.oauth.feature.terms.and.conditions.TermsAndConditionsScreen
import jp.co.soramitsu.oauth.feature.terms.and.conditions.WebPageScreen
import jp.co.soramitsu.oauth.feature.verify.email.EnterEmailScreen
import jp.co.soramitsu.oauth.feature.verify.email.VerifyEmailScreen
import jp.co.soramitsu.oauth.feature.verify.phone.CountryListScreen
import jp.co.soramitsu.oauth.feature.verify.phone.EnterPhoneNumberScreen
import jp.co.soramitsu.oauth.feature.verify.phone.VerifyPhoneNumberScreen

@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun SdkNavGraph(
    navHostController: NavHostController,
    startDestination: Destination,
    authCallback: OAuthCallback,
) {
    NavHost(navHostController, startDestination = startDestination.route) {
        animatedComposable(Destination.TERMS_AND_CONDITIONS.route) {
            TermsAndConditionsScreen()
        }

        animatedComposable(Destination.GET_PREPARED.route) {
            GetPreparedScreen(authCallback)
        }

        animatedComposable(Destination.ENTER_PHONE_NUMBER.route) {
            val code = it.savedStateHandle.getLiveData<String>(COUNTRY_CODE).observeAsState()
            EnterPhoneNumberScreen(code.value)
        }

        animatedComposable(Destination.SELECT_COUNTRY.route) {
            CountryListScreen()
        }

        animatedComposable(
            Destination.VERIFY_PHONE_NUMBER.route + Argument.PHONE_NUMBER.path() + Argument.OTP_LENGTH.path(),
            arguments = listOf(
                navArgument(Argument.PHONE_NUMBER.arg) { type = NavType.StringType },
                navArgument(Argument.OTP_LENGTH.arg) { type = NavType.IntType },
            )
        ) { backStackEntry ->
            VerifyPhoneNumberScreen(
                phoneNumber = backStackEntry.arguments?.getString(Argument.PHONE_NUMBER.arg),
                otpLength = backStackEntry.arguments?.getInt(Argument.OTP_LENGTH.arg),
                authCallback = authCallback,
            )
        }

        animatedComposable(Destination.REGISTER_USER.route) {
            RegisterUserScreen()
        }

        animatedComposable(
            Destination.ENTER_EMAIL.route + Argument.FIRST_NAME.path() + Argument.LAST_NAME.path()
        ) { backStackEntry ->
            EnterEmailScreen(
                firstName = backStackEntry.arguments?.getString(Argument.FIRST_NAME.arg),
                lastName = backStackEntry.arguments?.getString(Argument.LAST_NAME.arg),
                authCallback = authCallback
            )
        }

        animatedComposable(
            Destination.VERIFY_EMAIL.route + Argument.EMAIL.path() + Argument.AUTO_EMAIL_SENT.path(),
            arguments = listOf(
                navArgument(Argument.EMAIL.arg) { type = NavType.StringType },
                navArgument(Argument.AUTO_EMAIL_SENT.arg) { type = NavType.BoolType },
            )
        ) { backStackEntry ->
            VerifyEmailScreen(
                email = backStackEntry.requireArguments().requireString(Argument.EMAIL.arg),
                autoEmailSent = backStackEntry.requireArguments()
                    .getBoolean(Argument.AUTO_EMAIL_SENT.arg),
                authCallback = authCallback
            )
        }

        animatedComposable(
            Destination.WEB_PAGE.route + Argument.TITLE.path() + Argument.URL.path()
        ) { backStackEntry ->
            WebPageScreen(
                title = backStackEntry.requireArguments().requireString(Argument.TITLE.arg),
                webUrl = backStackEntry.requireArguments().requireString(Argument.URL.arg)
            )
        }

        animatedComposable(Destination.CHANGE_EMAIL.route) {
            ChangeEmailScreen()
        }

        animatedComposable(Destination.VERIFICATION_FAILED.route) {
            VerificationFailedScreen(
                additionalDescription = navHostController.previousBackStackEntry
                    ?.arguments
                    ?.getString(Argument.ADDITIONAL_DESCRIPTION.arg),
            )
        }

        animatedComposable(Destination.VERIFICATION_REJECTED.route) {
            VerificationRejectedScreen()
        }

        animatedComposable(Destination.VERIFICATION_IN_PROGRESS.route) {
            VerificationInProgressScreen()
        }

        animatedComposable(Destination.VERIFICATION_SUCCESSFUL.route) {
            VerificationSuccessfulScreen()
        }

        animatedComposable(
            route = KycRequirementsUnfulfilledDestination.CardIssuanceOptionsScreen().destination
        ) {
            CardIssuanceScreen()
        }

        dialog(
            route = KycRequirementsUnfulfilledDestination.GetMoreXorDialog().destination
        ) {
            ChooseXorPurchaseMethodDialog()
        }
    }
}
