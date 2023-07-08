package jp.co.soramitsu.oauth.core.datasources.paywings.api

import jp.co.soramitsu.oauth.theme.views.Text

sealed interface PayWingsResponse {

    object Loading: PayWingsResponse

    sealed interface Error : PayWingsResponse {

        @JvmInline
        value class OnChangeUnverifiedEmail(
            val errorText: Text
        ): Error

        @JvmInline
        value class OnCheckEmailVerification(
            val errorText: Text
        ): Error

        @JvmInline
        value class OnGetNewAccessToken(
            val errorText: Text
        ): Error

        @JvmInline
        value class OnGetUserData(
            val errorText: Text
        ): Error

        @JvmInline
        value class OnRegisterUser(
            val errorText: Text
        ): Error

        @JvmInline
        value class OnSendNewVerificationEmail(
            val errorText: Text
        ): Error

        @JvmInline
        value class OnSignInWithPhoneNumberVerifyOtp(
            val errorText: Text
        ): Error

        @JvmInline
        value class OnSignWithPhoneNumberRequestOtp(
            val errorText: Text
        ): Error

        object OnVerificationByOtpFailed: Error

    }

    sealed interface Result: PayWingsResponse {

        class ReceivedAccessTokens(
            val accessToken: String,
            val accessTokenExpirationTime: Long,
            val refreshToken: String
        ): Result

        class ReceivedNewAccessToken(
            val accessToken: String,
            val accessTokenExpirationTime: Long
        ): Result

        class ReceivedUserData(
            val userId: String,
            val firstName: String?,
            val lastName: String?,
            val email: String?,
            val emailConfirmed: Boolean,
            val phoneNumber: String?
        ): Result


        class ResendDelayedOtpRepeatedly(): Result

        class ResendDelayedVerificationEmailRepeatedly(): Result

    }

    sealed interface NavigationIncentive : PayWingsResponse {

        class OnEmailConfirmationRequiredScreen(
            val email: String,
            val autoEmailBeenSent: Boolean
        ): NavigationIncentive

        class OnUserSignInRequiredScreen(): NavigationIncentive

        class OnVerificationOtpBeenSent(
            val otpLength: Int
        ): NavigationIncentive

        class OnRegistrationRequiredScreen(): NavigationIncentive

    }
}
