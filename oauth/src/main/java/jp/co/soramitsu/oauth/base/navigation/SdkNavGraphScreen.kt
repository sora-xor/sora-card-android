package jp.co.soramitsu.oauth.base.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import jp.co.soramitsu.oauth.feature.OAuthCallback
import jp.co.soramitsu.oauth.feature.cardissuance.CardIssuanceScreen
import jp.co.soramitsu.oauth.feature.change.email.ChangeEmailScreen
import jp.co.soramitsu.oauth.feature.gatehub.onboarding.GatehubOnboardingProgressScreen
import jp.co.soramitsu.oauth.feature.gatehub.rejected.GatehubOnboardingRejectedScreen
import jp.co.soramitsu.oauth.feature.gatehub.step1.GatehubOnboardingStep1Screen
import jp.co.soramitsu.oauth.feature.gatehub.step2.GatehubOnboardingStep2Screen
import jp.co.soramitsu.oauth.feature.gatehub.step2crossbordertx.GatehubOnboardingCrossBorderTxScreen
import jp.co.soramitsu.oauth.feature.gatehub.step3.GatehubOnboardingStep3Screen
import jp.co.soramitsu.oauth.feature.gatehub.stepEmploymentStatus.GatehubOnboardingStepEmploymentStatusScreen
import jp.co.soramitsu.oauth.feature.getmorexor.ChooseXorPurchaseMethodDialog
import jp.co.soramitsu.oauth.feature.getprepared.GetPreparedScreen
import jp.co.soramitsu.oauth.feature.kyc.result.VerificationFailedScreen
import jp.co.soramitsu.oauth.feature.kyc.result.VerificationInProgressScreen
import jp.co.soramitsu.oauth.feature.kyc.result.VerificationSuccessfulScreen
import jp.co.soramitsu.oauth.feature.kyc.result.verificationrejected.VerificationRejectedScreen
import jp.co.soramitsu.oauth.feature.registration.RegisterUserScreen
import jp.co.soramitsu.oauth.feature.terms.and.conditions.InitLoadingScreen
import jp.co.soramitsu.oauth.feature.terms.and.conditions.TermsAndConditionsScreen
import jp.co.soramitsu.oauth.feature.terms.and.conditions.WebPageScreen
import jp.co.soramitsu.oauth.feature.usernotfound.UserNotFoundScreen
import jp.co.soramitsu.oauth.feature.verify.email.EnterEmailScreen
import jp.co.soramitsu.oauth.feature.verify.email.VerifyEmailScreen
import jp.co.soramitsu.oauth.feature.verify.phone.uicompose.CountryListScreen
import jp.co.soramitsu.oauth.feature.verify.phone.uicompose.EnterPhoneNumberScreen
import jp.co.soramitsu.oauth.feature.verify.phone.uicompose.VerifyPhoneNumberScreen

@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun SdkNavGraph(
    navHostController: NavHostController,
    startDestination: Destination,
    authCallback: OAuthCallback,
) {
    NavHost(
        modifier = Modifier.systemBarsPadding(),
        navController = navHostController,
        startDestination = startDestination.route,
    ) {
        animatedComposable(Destination.INIT_LOADING.route) {
            InitLoadingScreen()
        }

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

        animatedComposable(
            route = Destination.SELECT_COUNTRY.route + Argument.ADDITIONAL_DESCRIPTION.path(),
            arguments = listOf(
                navArgument(Argument.ADDITIONAL_DESCRIPTION.arg) { type = NavType.BoolType },
            ),
        ) {
            CountryListScreen()
        }

        animatedComposable(Destination.GATEHUB_ONBOARDING_STEP_EMPLOYMENT.route) {
            GatehubOnboardingStepEmploymentStatusScreen()
        }

        animatedComposable(Destination.GATEHUB_ONBOARDING_STEP_1.route) {
            GatehubOnboardingStep1Screen()
        }

        animatedComposable(
            route = Destination.GATEHUB_ONBOARDING_STEP_CROSS_BORDER_TX.route + Argument.ADDITIONAL_DESCRIPTION.path(),
            arguments = listOf(
                navArgument(Argument.ADDITIONAL_DESCRIPTION.arg) { type = NavType.BoolType },
            ),
        ) {
            val codes = it.savedStateHandle.getLiveData<List<String>>(COUNTRY_CODE).observeAsState()
            GatehubOnboardingCrossBorderTxScreen(
                countries = codes.value,
            )
        }

        animatedComposable(Destination.GATEHUB_ONBOARDING_STEP_2.route) {
            GatehubOnboardingStep2Screen()
        }

        animatedComposable(Destination.GATEHUB_ONBOARDING_STEP_3.route) {
            GatehubOnboardingStep3Screen()
        }

        animatedComposable(Destination.GATEHUB_ONBOARDING_PROGRESS.route) {
            GatehubOnboardingProgressScreen()
        }

        animatedComposable(
            route = Destination.GATEHUB_ONBOARDING_REJECTED.route + Argument.ADDITIONAL_DESCRIPTION.path(),
            arguments = listOf(
                navArgument(Argument.ADDITIONAL_DESCRIPTION.arg) { type = NavType.StringType },
            ),
        ) { entry ->
            GatehubOnboardingRejectedScreen(
                reason = entry.arguments?.getString(Argument.ADDITIONAL_DESCRIPTION.arg).orEmpty(),
            )
        }

        animatedComposable(
            route = Destination.VERIFY_PHONE_NUMBER.route + Argument.COUNTRY_CODE.path() + Argument.PHONE_NUMBER.path() + Argument.OTP_LENGTH.path(),
            arguments = listOf(
                navArgument(Argument.COUNTRY_CODE.arg) { type = NavType.StringType },
                navArgument(Argument.PHONE_NUMBER.arg) { type = NavType.StringType },
                navArgument(Argument.OTP_LENGTH.arg) { type = NavType.IntType },
            ),
        ) { backStackEntry ->
            VerifyPhoneNumberScreen(
                countryCode = backStackEntry.arguments?.getString(Argument.COUNTRY_CODE.arg),
                phoneNumber = backStackEntry.arguments?.getString(Argument.PHONE_NUMBER.arg),
                otpLength = backStackEntry.arguments?.getInt(Argument.OTP_LENGTH.arg),
                authCallback = authCallback,
            )
        }

        animatedComposable(Destination.REGISTER_USER.route) {
            RegisterUserScreen()
        }

        animatedComposable(
            Destination.ENTER_EMAIL.route + Argument.FIRST_NAME.path() + Argument.LAST_NAME.path(),
        ) { backStackEntry ->
            EnterEmailScreen(
                firstName = backStackEntry.arguments?.getString(Argument.FIRST_NAME.arg),
                lastName = backStackEntry.arguments?.getString(Argument.LAST_NAME.arg),
                authCallback = authCallback,
            )
        }

        animatedComposable(
            Destination.VERIFY_EMAIL.route + Argument.EMAIL.path() + Argument.AUTO_EMAIL_SENT.path(),
            arguments = listOf(
                navArgument(Argument.EMAIL.arg) { type = NavType.StringType },
                navArgument(Argument.AUTO_EMAIL_SENT.arg) { type = NavType.BoolType },
            ),
        ) { backStackEntry ->
            VerifyEmailScreen(
                email = backStackEntry.requireArguments().requireString(Argument.EMAIL.arg),
                autoEmailSent = backStackEntry.requireArguments()
                    .getBoolean(Argument.AUTO_EMAIL_SENT.arg),
                authCallback = authCallback,
            )
        }

        animatedComposable(
            Destination.WEB_PAGE.route + Argument.TITLE.path() + Argument.URL.path() + Argument.FLAG.path(),
        ) { backStackEntry ->
            WebPageScreen(
                title = backStackEntry.requireArguments().requireString(Argument.TITLE.arg),
                webUrl = backStackEntry.requireArguments().requireString(Argument.URL.arg),
                lastPage = backStackEntry.requireArguments().requireString(
                    Argument.FLAG.arg,
                ).toBoolean(),
            )
        }

        animatedComposable(Destination.CHANGE_EMAIL.route) {
            ChangeEmailScreen()
        }

        animatedComposable(
            Destination.VERIFICATION_FAILED.route + Argument.ADDITIONAL_DESCRIPTION.path(),
            listOf(navArgument(Argument.ADDITIONAL_DESCRIPTION.arg) { type = NavType.StringType }),
        ) {
            val desc = it.arguments?.getString(Argument.ADDITIONAL_DESCRIPTION.arg)
            VerificationFailedScreen(additionalDescription = desc)
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
            route = Destination.CARD_ISSUANCE_OPTIONS.route,
        ) {
            CardIssuanceScreen()
        }

        animatedComposable(Destination.USER_NOT_FOUND.route) {
            UserNotFoundScreen()
        }

        dialog(
            route = Destination.GET_MORE_XOR_DIALOG.route,
        ) {
            ChooseXorPurchaseMethodDialog()
        }
    }
}
