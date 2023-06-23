package jp.co.soramitsu.oauth.base.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import jp.co.soramitsu.oauth.common.navigation.flow.api.destinations.KycRequirementsUnfulfilledFlowDestination
import jp.co.soramitsu.oauth.common.navigation.flow.api.destinations.LoginFlowDestination
import jp.co.soramitsu.oauth.feature.KycCallback
import jp.co.soramitsu.oauth.feature.OAuthCallback
import jp.co.soramitsu.oauth.feature.verification.result.cardissuance.CardIssuanceScreen
import jp.co.soramitsu.oauth.feature.registration.email.ChangeEmailScreen
import jp.co.soramitsu.oauth.feature.verification.result.prepared.GetPreparedScreen
import jp.co.soramitsu.oauth.feature.verification.result.getmorexor.ChooseXorPurchaseMethodDialog
import jp.co.soramitsu.oauth.feature.verification.result.VerificationFailedScreen
import jp.co.soramitsu.oauth.feature.verification.result.VerificationInProgressScreen
import jp.co.soramitsu.oauth.feature.verification.result.verificationrejected.VerificationRejectedScreen
import jp.co.soramitsu.oauth.feature.verification.result.VerificationSuccessfulScreen
import jp.co.soramitsu.oauth.feature.registration.RegisterUserScreen
import jp.co.soramitsu.oauth.feature.login.conditions.TermsAndConditionsScreen
import jp.co.soramitsu.oauth.feature.login.conditions.WebPageScreen
import jp.co.soramitsu.oauth.feature.registration.email.EnterEmailScreen
import jp.co.soramitsu.oauth.feature.registration.email.VerifyEmailScreen
import jp.co.soramitsu.oauth.feature.login.conditions.phone.EnterPhoneNumberScreen
import jp.co.soramitsu.oauth.feature.login.conditions.phone.VerifyPhoneNumberScreen

@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun SdkNavGraph(
    navHostController: NavHostController,
    startDestination: Destination,
    authCallback: OAuthCallback,
    kycCallback: KycCallback,
) {
    AnimatedNavHost(navHostController, startDestination = startDestination.route) {
        animatedComposable(LoginFlowDestination.TermsAndConditionsScreen().destination) {
            TermsAndConditionsScreen(kycCallback)
        }

        animatedComposable(LoginFlowDestination.EnterPhoneScreen().destination) {
            EnterPhoneNumberScreen()
        }

        animatedComposable(
            LoginFlowDestination.VerifyPhoneScreen().destination + Argument.PHONE_NUMBER.path() + Argument.OTP_LENGTH.path(),
            arguments = listOf(
                navArgument(Argument.PHONE_NUMBER.arg) { type = NavType.StringType },
                navArgument(Argument.OTP_LENGTH.arg) { type = NavType.IntType },
            )
        ) { backStackEntry ->
            VerifyPhoneNumberScreen(
                phoneNumber = backStackEntry.arguments?.getString(Argument.PHONE_NUMBER.arg),
                otpLength = backStackEntry.arguments?.getInt(Argument.OTP_LENGTH.arg),
                authCallback = authCallback,
                kycCallback = kycCallback
            )
        }

        animatedComposable(Destination.GET_PREPARED.route) {
            GetPreparedScreen(authCallback)
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
                    ?.requireArguments()
                    ?.getString(Argument.ADDITIONAL_DESCRIPTION.arg),
                kycCallback = kycCallback
            )
        }

        animatedComposable(Destination.VERIFICATION_REJECTED.route) {
            VerificationRejectedScreen(
                additionalDescription = navHostController.previousBackStackEntry
                    ?.requireArguments()
                    ?.getString(Argument.ADDITIONAL_DESCRIPTION.arg),
            )
        }

        animatedComposable(Destination.VERIFICATION_IN_PROGRESS.route) {
            VerificationInProgressScreen(kycCallback)
        }

        animatedComposable(Destination.VERIFICATION_SUCCESSFUL.route) {
            VerificationSuccessfulScreen(kycCallback)
        }

        animatedComposable(
            route = KycRequirementsUnfulfilledFlowDestination.CardIssuanceOptionsScreen().destination
        ) {
            CardIssuanceScreen()
        }

        dialog(
            route = KycRequirementsUnfulfilledFlowDestination.GetMoreXorDialog().destination
        ) {
            ChooseXorPurchaseMethodDialog()
        }
    }
}
