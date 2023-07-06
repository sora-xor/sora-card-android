package jp.co.soramitsu.oauth.base

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import jp.co.soramitsu.oauth.base.navigation.Argument
import jp.co.soramitsu.oauth.base.navigation.Destination
import jp.co.soramitsu.oauth.base.navigation.animatedComposable
import jp.co.soramitsu.oauth.base.navigation.path
import jp.co.soramitsu.oauth.base.navigation.requireArguments
import jp.co.soramitsu.oauth.base.navigation.requireString
import jp.co.soramitsu.oauth.core.engines.router.api.SoraCardDestinations
import jp.co.soramitsu.oauth.feature.login.terms.TermsAndConditionsScreen
import jp.co.soramitsu.oauth.feature.login.web.WebPageScreen
import jp.co.soramitsu.oauth.feature.login.enterphone.EnterPhoneNumberScreen
import jp.co.soramitsu.oauth.feature.login.enterotp.VerifyPhoneNumberScreen
import jp.co.soramitsu.oauth.feature.registration.enternames.RegisterUserScreen
import jp.co.soramitsu.oauth.feature.registration.enteremail.EnterEmailScreen
import jp.co.soramitsu.oauth.feature.registration.sendverificationemail.VerifyEmailScreen
import jp.co.soramitsu.oauth.feature.verification.inprogress.VerificationInProgressScreen
import jp.co.soramitsu.oauth.feature.verification.successful.VerificationSuccessfulScreen
import jp.co.soramitsu.oauth.feature.verification.cardissuance.CardIssuanceScreen
import jp.co.soramitsu.oauth.feature.verification.failed.VerificationFailedScreen
import jp.co.soramitsu.oauth.feature.verification.getmorexor.ChooseXorPurchaseMethodDialog
import jp.co.soramitsu.oauth.feature.verification.getprepared.GetPreparedScreen
import jp.co.soramitsu.oauth.feature.verification.rejected.VerificationRejectedScreen

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
                    route = SoraCardDestinations.EnterOtp.template,
                    arguments = listOf(
                        navArgument(
                            Argument.PHONE_NUMBER.arg
                        ) {
                            type = NavType.StringType
                            defaultValue = "+1234567890"
                        },
                        navArgument(
                            name = "/{otpLength}"
                        ) {
                            type = NavType.IntType
                            defaultValue = 6
                        }
                    )
                ) { backStackEntry ->
                    VerifyPhoneNumberScreen(
                        phoneNumber = backStackEntry.arguments
                            ?.getString("/{phoneNumber}"),
                        otpLength = backStackEntry.arguments
                            ?.getInt("/{otpLength}"),
                    )
                }

                animatedComposable(
                    route = SoraCardDestinations.EnterFirstAndLastName.route
                ) {
                    RegisterUserScreen()
                }

                animatedComposable(
                    route = SoraCardDestinations.EnterEmail.template,
                    arguments = listOf(
                        navArgument(
                            name = "/{firstName}"
                        ) {
                            type = NavType.StringType
                            defaultValue = "John"
                        },
                        navArgument(
                            name = "/{lastName}"
                        ) {
                            type = NavType.StringType
                            defaultValue = "Doe"
                        }
                    )
                ) { backStackEntry ->
                    EnterEmailScreen(
                        firstName = backStackEntry.requireArguments()
                            .getString("/{firstName}"),
                        lastName = backStackEntry.requireArguments()
                            .getString("/{lastName}")
                    )
                }

                animatedComposable(
                    route = SoraCardDestinations.SendVerificationEmail.template,
                    arguments = listOf(
                        navArgument(
                            name = "/{email}"
                        ) {
                            type = NavType.StringType
                            defaultValue = "johndoe@email.com"
                        },
                        navArgument(
                            name = "/{autoEmailBeenSent}"
                        ) {
                            type = NavType.BoolType
                            defaultValue = false
                        },
                    )
                ) { backStackEntry ->
                    VerifyEmailScreen(
                        email = backStackEntry.requireArguments()
                            .requireString("/{email}"),
                        autoEmailSent = backStackEntry.requireArguments()
                            .getBoolean("/{autoEmailBeenSent}")
                    )
                }

                animatedComposable(
                    route = SoraCardDestinations.GetPrepared.route
                ) {
                    GetPreparedScreen()
                }

//                animatedComposable(
//                    route = Destination.WEB_PAGE.route
//                        .plus(Argument.TITLE.path())
//                        .plus(Argument.URL.path())
//                ) { backStackEntry ->
//                    WebPageScreen(
//                        title = backStackEntry.requireArguments()
//                            .requireString(Argument.TITLE.arg),
//                        webUrl = backStackEntry.requireArguments()
//                            .requireString(Argument.URL.arg)
//                    )
//                }

                animatedComposable(
                    route = SoraCardDestinations.VerificationFailed.template,
                    arguments = listOf(
                        navArgument(
                            name = "/{additionalInfo}"
                        ) {
                            type = NavType.StringType
                            defaultValue = null
                        },
                    )
                ) {backStackEntry ->
                    VerificationFailedScreen(
                        additionalDescription = backStackEntry.requireArguments()
                            .getString("/{additionalInfo}")
                    )
                }

                animatedComposable(
                    route = SoraCardDestinations.VerificationRejected.template,
                    arguments = listOf(
                        navArgument(
                            name = "/{additionalInfo}"
                        ) {
                            type = NavType.StringType
                            defaultValue = null
                        },
                    )
                ) {backStackEntry ->
                    VerificationRejectedScreen(
                        additionalDescription = backStackEntry.requireArguments()
                            .getString("/{additionalInfo}")
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

@Preview
@Composable
@OptIn(ExperimentalAnimationApi::class)
private fun PreviewSoraCardNavGraph() {
    SoraCardNavGraph(
        navHostController = rememberAnimatedNavController(),
        startDestination = SoraCardDestinations.TermsAndConditions
    )
}




//
//        animatedComposable(
//            route = SoraCardDestinations) {
//            ChangeEmailScreen()
//        }