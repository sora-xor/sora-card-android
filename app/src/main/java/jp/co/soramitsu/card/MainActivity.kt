package jp.co.soramitsu.card

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import jp.co.soramitsu.oauth.base.sdk.SoraCardEnvironmentType
import jp.co.soramitsu.oauth.base.sdk.SoraCardInfo
import jp.co.soramitsu.oauth.base.sdk.SoraCardKycCredentials
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardContract
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardContractData
import jp.co.soramitsu.oauth.base.sdk.signin.SoraCardSignInContract
import jp.co.soramitsu.oauth.core.datasources.tachi.api.models.KycStatus
import jp.co.soramitsu.oauth.theme.AuthSdkTheme
import jp.co.soramitsu.ui_core.component.button.FilledButton
import jp.co.soramitsu.ui_core.component.button.properties.Order
import jp.co.soramitsu.ui_core.component.button.properties.Size
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val registrationLauncher = registerForActivityResult(
        SoraCardContract()
    ) {}

    private var signInLauncher = registerForActivityResult(
        SoraCardSignInContract()
    ) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AuthSdkTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Button(onClick = { startRegistrationFlow(kycStatus = null) }) {
                            Text("registration")
                        }
                        Button(onClick = { startSignInFlow() }) {
                            Text("login")
                        }

                        FilledButton(size = Size.Large, order = Order.SECONDARY, text = "text") {

                        }
                    }
                }
            }
        }
    }

    private fun startRegistrationFlow(kycStatus: KycStatus? = null) {
        registrationLauncher.launch(
            SoraCardContractData(
                locale = Locale.ENGLISH,
                apiKey = BuildConfig.SORA_CARD_API_KEY,
                domain = BuildConfig.SORA_CARD_DOMAIN,
                kycCredentials = SoraCardKycCredentials(
                    endpointUrl = BuildConfig.SORA_CARD_KYC_ENDPOINT_URL,
                    username = BuildConfig.SORA_CARD_KYC_USERNAME,
                    password = BuildConfig.SORA_CARD_KYC_PASSWORD,
                ),
                environment = SoraCardEnvironmentType.TEST,
                soraCardInfo = kycStatus?.let {
                    SoraCardInfo(
                        accessToken = "",
                        refreshToken = "",
                        accessTokenExpirationTime = 0L,
                    )
                },
                client = buildClient(),
                userAvailableXorAmount = 120.0
            )
        )
    }

    private fun startSignInFlow() {
        signInLauncher.launch(
            SoraCardContractData(
                locale = Locale.ENGLISH,
                apiKey = BuildConfig.SORA_CARD_API_KEY,
                domain = BuildConfig.SORA_CARD_DOMAIN,
                environment = SoraCardEnvironmentType.TEST,
                soraCardInfo = null,
                kycCredentials = SoraCardKycCredentials("", "", ""),
                client = buildClient(),
                userAvailableXorAmount = 120.0
            )
        )
    }

    private fun buildClient(): String =
        "${BuildConfig.APPLICATION_ID}/${BuildConfig.BUILD_TYPE}/${BuildConfig.VERSION_CODE}/${BuildConfig.VERSION_NAME}/"
}
