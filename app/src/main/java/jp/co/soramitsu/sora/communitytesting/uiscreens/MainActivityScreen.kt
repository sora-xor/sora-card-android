package jp.co.soramitsu.sora.communitytesting.uiscreens

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import java.util.Locale
import javax.inject.Inject
import jp.co.soramitsu.androidfoundation.format.TextValue
import jp.co.soramitsu.oauth.base.sdk.SoraCardEnvironmentType
import jp.co.soramitsu.oauth.base.sdk.SoraCardKycCredentials
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardBasicContractData
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardContract
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardContractData
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardFlow
import jp.co.soramitsu.oauth.clients.ClientsFacade
import jp.co.soramitsu.oauth.feature.flagEmoji
import jp.co.soramitsu.oauth.uiscreens.clientsui.UiStyle
import jp.co.soramitsu.oauth.uiscreens.styledui.TextLargePrimaryButton
import jp.co.soramitsu.oauth.uiscreens.theme.AuthSdkTheme
import jp.co.soramitsu.oauth.uiscreens.theme.darkScrim
import jp.co.soramitsu.oauth.uiscreens.theme.lightScrim
import jp.co.soramitsu.sora.communitytesting.BuildConfig
import jp.co.soramitsu.ui_core.component.button.FilledButton
import jp.co.soramitsu.ui_core.component.button.properties.Order
import jp.co.soramitsu.ui_core.component.button.properties.Size
import jp.co.soramitsu.ui_core.theme.customColors
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@HiltAndroidApp
open class Application : Application()

@AndroidEntryPoint
class MainActivityScreen : ComponentActivity() {

    private val registrationLauncher = registerForActivityResult(
        SoraCardContract(),
    ) {
        Log.e("srms", "contract result $it")
    }

    private val dark: Boolean = true

    @Inject
    lateinit var facade: ClientsFacade

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent {
            DisposableEffect(key1 = dark) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT,
                    ) { dark },
                    navigationBarStyle = SystemBarStyle.auto(
                        lightScrim,
                        darkScrim,
                    ) { dark },
                )
                onDispose { }
            }
            AuthSdkTheme(
                darkTheme = dark,
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.customColors.bgPage,
                ) {
                    Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
                        Button(onClick = { startRegistrationFlow() }) {
                            Text("registration")
                        }
                        Button(onClick = { startGateHub() }) {
                            Text("exchange")
                        }
                        TextLargePrimaryButton(
                            text = TextValue.SimpleText("Foo text"),
                            onClick = {},
                            enabled = true,
                        )

                        Text(text = Locale.getISOCountries().joinToString(";"))
                        Text(text = Locale("", "gb").displayCountry + "US".flagEmoji())

                        FilledButton(
                            size = Size.Large,
                            order = Order.SECONDARY,
                            text = "text",
                        ) {
                            lifecycleScope.launch {
                                facade.init(
                                    basic(),
                                    applicationContext,
                                    BuildConfig.SORA_API_BASE_URL,
                                )
                            }
                            MainScope().launch {
                                facade.getKycStatus()
                                    .onFailure {
                                        Log.e("srms", "error ${it.localizedMessage}")
                                    }
                                    .onSuccess {
                                        Log.e("srms", "success $it")
                                    }
                            }
//                            MainScope().launch {
//                                facade.getSoraSupportVersion()
//                                    .also {
//                                        Log.e("srms", "res= $it")
//                                    }
//                            }
                        }
                    }
                }
            }
        }
    }

    private fun startGateHub() {
        registrationLauncher.launch(
            SoraCardContractData(
                basic = basic(),
                locale = Locale.getDefault(),
                client = buildClient(),
                soraBackEndUrl = BuildConfig.SORA_API_BASE_URL,
                clientDark = dark,
                clientCase = UiStyle.SW,
                flow = SoraCardFlow.SoraCardGateHubFlow,
            ),
        )
    }

    private fun startRegistrationFlow() {
        registrationLauncher.launch(
            SoraCardContractData(
                basic = basic(),
                locale = Locale.getDefault(),
                client = buildClient(),
                soraBackEndUrl = BuildConfig.SORA_API_BASE_URL,
                clientDark = dark,
                clientCase = UiStyle.SW,
                flow = SoraCardFlow.SoraCardKycFlow(
                    kycCredentials = SoraCardKycCredentials(
                        endpointUrl = BuildConfig.SORA_CARD_KYC_ENDPOINT_URL,
                        username = BuildConfig.SORA_CARD_KYC_USERNAME,
                        password = BuildConfig.SORA_CARD_KYC_PASSWORD,
                    ),
//                userAvailableXorAmount = 19.9,
//                isEnoughXorAvailable = false,
                    userAvailableXorAmount = 19999.9,
                    isEnoughXorAvailable = true,
                    areAttemptsPaidSuccessfully = true,
                    isIssuancePaid = false,
                    logIn = true,
                ),
            ),
        )
    }

    private fun basic() = SoraCardBasicContractData(
        apiKey = BuildConfig.SORA_CARD_API_KEY,
        domain = BuildConfig.SORA_CARD_DOMAIN,
        environment = SoraCardEnvironmentType.TEST,
        platform = BuildConfig.PLATFORM_ID,
        recaptcha = BuildConfig.RECAPTCHA_KEY,
    )

    private fun buildClient(): String =
        "${BuildConfig.APPLICATION_ID}/${BuildConfig.BUILD_TYPE}/${BuildConfig.VERSION_CODE}/${BuildConfig.VERSION_NAME}/"
}
