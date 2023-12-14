package jp.co.soramitsu.oauth.base.navigation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.annotation.StringRes
import androidx.navigation.NavHostController
import jp.co.soramitsu.oauth.base.extension.isAppAvailableCompat
import jp.co.soramitsu.oauth.feature.terms.and.conditions.model.WebUrl

interface MainRouter {

    fun attachNavController(activity: Activity, navHostController: NavHostController)

    fun detachNavController(activity: Activity, navHostController: NavHostController)

    fun back()

    fun openGetPrepared()

    fun openEnterPhoneNumber(clearStack: Boolean = false)

    fun openCountryList()
    fun backWithCountry(code: String)

    fun openVerifyPhoneNumber(phoneNumber: String, otpLength: Int)

    fun openRegisterUser()

    fun openEnterEmail(firstName: String, lastName: String)

    fun openVerifyEmail(email: String, autoEmailSent: Boolean, clearStack: Boolean = false)

    fun openWebPage(@StringRes titleRes: Int, url: WebUrl)

    fun openChangeEmail()

    fun openVerificationSuccessful()

    fun openVerificationInProgress()

    fun openVerificationFailed(additionalDescription: String?)

    fun openVerificationRejected()

    fun openSupportChat()

    fun navigate(destinationRoute: String)

    fun popUpToAndNavigate(popUpRoute: String, destinationRoute: String)

    fun popUpTo(destinationRoute: String)
}

class MainRouterImpl : MainRouter {

    private companion object {
        const val APP_TELEGRAM = "org.telegram.messenger"
        const val APP_TELEGRAM_X = "org.thunderdog.challegram"

        const val SUPPORT_CHAT_ID = "soracardofficial"
        const val SUPPORT_CHAT_LINK = "https://t.me/$SUPPORT_CHAT_ID"
    }

    private var navHostController: NavHostController? = null
    private var activity: Activity? = null

    override fun attachNavController(activity: Activity, navHostController: NavHostController) {
        this.navHostController = navHostController
        this.activity = activity
    }

    override fun detachNavController(activity: Activity, navHostController: NavHostController) {
        if (this.activity == activity && this.navHostController == navHostController) {
            this.navHostController = null
            this.activity = null
        }
    }

    override fun back() {
        navHostController?.popBackStack()
    }

    override fun openGetPrepared() {
        navHostController?.navigate(Destination.GET_PREPARED.route) {
            popUpTo(Destination.ENTER_PHONE_NUMBER.route)
        }
    }

    override fun openCountryList() {
        navHostController?.navigate(Destination.SELECT_COUNTRY.route)
    }

    override fun backWithCountry(code: String) {
        navHostController?.previousBackStackEntry?.savedStateHandle?.set(COUNTRY_CODE, code)
        navHostController?.popBackStack()
    }

    override fun openEnterPhoneNumber(clearStack: Boolean) {
        navHostController?.navigate(Destination.ENTER_PHONE_NUMBER.route) {
            if (clearStack) {
                popUpTo(Destination.ENTER_PHONE_NUMBER.route)
            }
        }
    }

    override fun openVerifyPhoneNumber(phoneNumber: String, otpLength: Int) {
        navHostController?.navigate(
            Destination.VERIFY_PHONE_NUMBER.route + phoneNumber.asArgument() + otpLength.asArgument(),
        )
    }

    override fun openRegisterUser() {
        navHostController?.navigate(Destination.REGISTER_USER.route)
    }

    override fun openEnterEmail(firstName: String, lastName: String) {
        navHostController?.navigate(
            Destination.ENTER_EMAIL.route + firstName.asArgument() + lastName.asArgument(),
        )
    }

    override fun openVerifyEmail(email: String, autoEmailSent: Boolean, clearStack: Boolean) {
        navHostController?.navigate(
            Destination.VERIFY_EMAIL.route + email.asArgument() + autoEmailSent.asArgument(),
        ) {
            if (clearStack) {
                popUpTo(Destination.ENTER_PHONE_NUMBER.route)
            }
        }
    }

    override fun openWebPage(@StringRes titleRes: Int, url: WebUrl) {
        val title = navHostController?.context?.getString(titleRes).orEmpty()
        navHostController?.navigate(
            Destination.WEB_PAGE.route + title.asArgument() + url.asArgument(),
        )
    }

    override fun openChangeEmail() {
        navHostController?.navigate(Destination.CHANGE_EMAIL.route)
    }

    override fun openVerificationSuccessful() {
        navHostController?.popBackStack()
        navHostController?.navigate(Destination.VERIFICATION_SUCCESSFUL.route)
    }

    override fun openVerificationInProgress() {
        navHostController?.popBackStack()
        navHostController?.navigate(Destination.VERIFICATION_IN_PROGRESS.route)
    }

    override fun openVerificationFailed(additionalDescription: String?) {
        navHostController?.popBackStack()
        navHostController?.apply {
            currentBackStackEntry?.arguments?.putString(
                Argument.ADDITIONAL_DESCRIPTION.arg,
                additionalDescription,
            )
            navigate(Destination.VERIFICATION_FAILED.route)
        }
    }

    override fun openVerificationRejected() {
        navHostController?.popBackStack()
        navHostController?.apply {
            navigate(Destination.VERIFICATION_REJECTED.route)
        }
    }

    override fun openSupportChat() {
        val appAvailable = activity?.let {
            it.isAppAvailableCompat(APP_TELEGRAM) || it.isAppAvailableCompat(APP_TELEGRAM_X)
        } ?: false

        val intent = if (appAvailable) {
            Intent(Intent.ACTION_VIEW, Uri.parse("tg://resolve?domain=${SUPPORT_CHAT_ID}"))
        } else {
            Intent(Intent.ACTION_VIEW, Uri.parse(SUPPORT_CHAT_LINK))
        }

        activity?.startActivity(intent)
    }

    override fun navigate(destinationRoute: String) {
        navHostController?.navigate(destinationRoute)
    }

    override fun popUpToAndNavigate(popUpRoute: String, destinationRoute: String) {
        navHostController?.navigate(destinationRoute) {
            popUpTo(popUpRoute)
        }
    }

    override fun popUpTo(destinationRoute: String) {
        if (navHostController?.currentBackStackEntry?.destination?.route?.contains(destinationRoute) == true) {
            navHostController?.popBackStack(destinationRoute, inclusive = false)
        } else {
            navHostController?.popBackStack(
                Destination.TERMS_AND_CONDITIONS.route,
                inclusive = false,
            )
        }
    }
}
