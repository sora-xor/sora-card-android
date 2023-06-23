package jp.co.soramitsu.oauth.common.navigation.router.api

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import jp.co.soramitsu.oauth.base.navigation.Argument
import jp.co.soramitsu.oauth.base.navigation.Destination
import jp.co.soramitsu.oauth.base.navigation.animatedComposable
import jp.co.soramitsu.oauth.base.navigation.path
import jp.co.soramitsu.oauth.base.navigation.requireArguments
import jp.co.soramitsu.oauth.base.navigation.requireString
import jp.co.soramitsu.oauth.feature.login.conditions.TermsAndConditionsScreen
import jp.co.soramitsu.oauth.feature.login.conditions.WebPageScreen
import jp.co.soramitsu.oauth.feature.login.conditions.phone.EnterPhoneNumberScreen
import jp.co.soramitsu.oauth.feature.login.conditions.phone.VerifyPhoneNumberScreen
import jp.co.soramitsu.oauth.feature.registration.RegisterUserScreen
import jp.co.soramitsu.oauth.feature.registration.email.EnterEmailScreen
import jp.co.soramitsu.oauth.feature.registration.email.VerifyEmailScreen
import jp.co.soramitsu.oauth.feature.verification.result.VerificationInProgressScreen
import jp.co.soramitsu.oauth.feature.verification.result.VerificationSuccessfulScreen
import jp.co.soramitsu.oauth.feature.verification.result.cardissuance.CardIssuanceScreen
import jp.co.soramitsu.oauth.feature.verification.result.getmorexor.ChooseXorPurchaseMethodDialog
import jp.co.soramitsu.oauth.feature.verification.result.prepared.GetPreparedScreen
import jp.co.soramitsu.oauth.feature.verification.result.verificationrejected.VerificationRejectedScreen

@Composable
@OptIn(ExperimentalAnimationApi::class)
internal fun SoraCardNavGraph(
    navHostController: NavHostController,
    startDestination: SoraCardDestinations,
) {
    val isGraphVisible = remember {
        startDestination !== SoraCardDestinations.Loading
    }

    val navGraph = remember {
        movableContentOf {
            AnimatedNavHost(
                navController = navHostController,
                startDestination = startDestination.route
            ) {
                animatedComposable(
                    route = SoraCardDestinations.TermsAndConditions.route
                ) {
                    TermsAndConditionsScreen()
                }

                animatedComposable(
                    route = SoraCardDestinations.EnterPhone.route
                ) {
                    EnterPhoneNumberScreen()
                }

                animatedComposable(
                    route = SoraCardDestinations.EnterOtp.route
                        .plus(Argument.PHONE_NUMBER.path())
                        .plus(Argument.OTP_LENGTH.path()),
                    arguments = listOf(
                        navArgument(Argument.PHONE_NUMBER.arg) { type = NavType.StringType },
                        navArgument(Argument.OTP_LENGTH.arg) { type = NavType.IntType },
                    )
                ) { backStackEntry ->
                    VerifyPhoneNumberScreen(
                        phoneNumber = backStackEntry.arguments?.getString(Argument.PHONE_NUMBER.arg),
                        otpLength = backStackEntry.arguments?.getInt(Argument.OTP_LENGTH.arg),
                    )
                }

                animatedComposable(
                    route = SoraCardDestinations.EnterFirstAndLastName.route
                ) {
                    RegisterUserScreen()
                }

                animatedComposable(
                    route = SoraCardDestinations.EnterEmail.route
                        .plus(Argument.FIRST_NAME.path())
                        .plus(Argument.LAST_NAME.path())
                ) { backStackEntry ->
                    EnterEmailScreen(
                        firstName = backStackEntry.arguments?.getString(Argument.FIRST_NAME.arg),
                        lastName = backStackEntry.arguments?.getString(Argument.LAST_NAME.arg)
                    )
                }

                animatedComposable(
                    route = SoraCardDestinations.SendVerificationEmail.route
                        .plus(Argument.EMAIL.path())
                        .plus(Argument.AUTO_EMAIL_SENT.path()),
                    arguments = listOf(
                        navArgument(Argument.EMAIL.arg) { type = NavType.StringType },
                        navArgument(Argument.AUTO_EMAIL_SENT.arg) { type = NavType.BoolType },
                    )
                ) { backStackEntry ->
                    VerifyEmailScreen(
                        email = backStackEntry.requireArguments()
                            .requireString(Argument.EMAIL.arg),
                        autoEmailSent = backStackEntry.requireArguments()
                            .getBoolean(Argument.AUTO_EMAIL_SENT.arg)
                    )
                }

                animatedComposable(
                    route = SoraCardDestinations.GetPrepared.route
                ) {
                    GetPreparedScreen()
                }

                animatedComposable(
                    route = Destination.WEB_PAGE.route
                        .plus(Argument.TITLE.path())
                        .plus(Argument.URL.path())
                ) { backStackEntry ->
                    WebPageScreen(
                        title = backStackEntry.requireArguments()
                            .requireString(Argument.TITLE.arg),
                        webUrl = backStackEntry.requireArguments()
                            .requireString(Argument.URL.arg)
                    )
                }

                animatedComposable(
                    route = SoraCardDestinations.VerificationRejected.route
                ) {backStackEntry ->
                    VerificationRejectedScreen(
                        additionalDescription = backStackEntry.arguments
                            ?.getString(Argument.ADDITIONAL_DESCRIPTION.arg),
                    )
                }

                animatedComposable(
                    route = SoraCardDestinations.VerificationInProgress.route
                ) {
                    VerificationInProgressScreen()
                }

                animatedComposable(
                    route = SoraCardDestinations.VerificationSuccessful.route
                ) {
                    VerificationSuccessfulScreen()
                }

                animatedComposable(
                    route = SoraCardDestinations.NotEnoughXor.route
                ) {
                    CardIssuanceScreen()
                }

                dialog(
                    route = SoraCardDestinations.GetMoreXor.route
                ) {
                    ChooseXorPurchaseMethodDialog()
                }
            }
        }
    }

    if (isGraphVisible) {
        navGraph.invoke()
    }
}


//        animatedComposable(Destination.VERIFICATION_FAILED.route) {
//            VerificationFailedScreen(
//                additionalDescription = navHostController.previousBackStackEntry
//                    ?.requireArguments()
//                    ?.getString(Argument.ADDITIONAL_DESCRIPTION.arg),
//                kycCallback = kycCallback
//            )
//        }

//
//        animatedComposable(
//            route = SoraCardDestinations) {
//            ChangeEmailScreen()
//        }