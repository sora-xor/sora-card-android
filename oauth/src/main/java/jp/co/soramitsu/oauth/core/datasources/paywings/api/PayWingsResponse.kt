package jp.co.soramitsu.oauth.core.datasources.paywings.api

sealed interface PayWingsResponse {

    sealed interface Error : PayWingsResponse {

        class OnChangeUnverifiedEmail(
            val errorMessage: String
        ): Error

        class OnCheckEmailVerification(
            val errorMessage: String
        ): Error

        class OnGetNewAccessToken(
            val errorMessage: String
        ): Error

        class OnGetUserData(
            val errorMessage: String
        ): Error

        class OnRegisterUser(
            val errorMessage: String
        ): Error

        class OnSendNewVerificationEmail(
            val errorMessage: String
        ): Error

        class OnSignInWithPhoneNumberVerifyOtp(
            val errorMessage: String
        ): Error

        class OnSignWithPhoneNumberRequestOtp(
            val errorMessage: String
        ): Error

        class OnVerificationByOtpFailed: Error

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
