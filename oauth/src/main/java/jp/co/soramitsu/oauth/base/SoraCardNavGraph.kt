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
import jp.co.soramitsu.oauth.common.navigation.flow.login.api.LoginDestination
import jp.co.soramitsu.oauth.common.navigation.flow.registration.api.RegistrationDestination
import jp.co.soramitsu.oauth.common.navigation.flow.verification.api.VerificationDestination
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
    println("This is checkpoint: SoraCardNavGraph recompose - ${startDestination.route}")

    val isGraphVisible = remember(startDestination) {
        startDestination !== SoraCardDestinations.Loading
    }

    val navGraph = remember(startDestination) {
        println("This is checkpoint: navGraph remember recompose - ${startDestination.route}")

        movableContentOf {
            println("This is checkpoint: navGraph movableContentOf recompose - ${startDestination.route}")

            AnimatedNavHost(
                navController = navHostController,
                startDestination = SoraCardDestinations.Loading.route
            ) {
                animatedComposable(
                    route = LoginDestination.TermsAndConditions.route
                ) {
                    TermsAndConditionsScreen()
                }

                animatedComposable(
                    route = LoginDestination.EnterPhone.route
                ) {
                    EnterPhoneNumberScreen()
                }

                animatedComposable(
                    route = LoginDestination.EnterOtp.route
                ) {
                    VerifyPhoneNumberScreen()
                }

                animatedComposable(
                    route = RegistrationDestination.EnterFirstAndLastName.route
                ) {
                    RegisterUserScreen()
                }

                animatedComposable(
                    route = RegistrationDestination.EnterEmail.route,
                ) {
                    EnterEmailScreen()
                }

                animatedComposable(
                    route = RegistrationDestination.EmailConfirmation.route
                ) {
                    VerifyEmailScreen()
                }

                animatedComposable(
                    route = VerificationDestination.GetPrepared.route
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
                    route = VerificationDestination.VerificationFailed.route
                ) {
                    VerificationFailedScreen()
                }

                animatedComposable(
                    route = VerificationDestination.VerificationRejected.route
                ) {
                    VerificationRejectedScreen()
                }

                animatedComposable(
                    route = VerificationDestination.VerificationInProgress.route
                ) {
                    VerificationInProgressScreen()
                }

                animatedComposable(
                    route = VerificationDestination.VerificationSuccessful.route
                ) {
                    VerificationSuccessfulScreen()
                }

                animatedComposable(
                    route = VerificationDestination.NotEnoughXor.route
                ) {
                    CardIssuanceScreen()
                }

                dialog(
                    route = VerificationDestination.GetMoreXor.route
                ) {
                    ChooseXorPurchaseMethodDialog()
                }
            }


            println("This is checkpoint: navHostController.graph - ${navHostController.graph.nodes}")
        }
    }

    navGraph.invoke()
}

@Preview
@Composable
@OptIn(ExperimentalAnimationApi::class)
private fun PreviewSoraCardNavGraph() {
    SoraCardNavGraph(
        navHostController = rememberAnimatedNavController(),
        startDestination = LoginDestination.TermsAndConditions
    )
}




//
//        animatedComposable(
//            route = SoraCardDestinations) {
//            ChangeEmailScreen()
//        }