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
import jp.co.soramitsu.oauth.base.sdk.SoraCardKycCredentials
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardContract
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardContractData
import jp.co.soramitsu.oauth.common.model.KycStatus
import jp.co.soramitsu.oauth.theme.AuthSdkTheme
import jp.co.soramitsu.ui_core.component.button.FilledButton
import jp.co.soramitsu.ui_core.component.button.properties.Order
import jp.co.soramitsu.ui_core.component.button.properties.Size
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val registrationLauncher = registerForActivityResult(
        SoraCardContract()
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
                client = buildClient(),
                userAvailableXorAmount = 19999.9,
                isEnoughXorAvailable = true,
                areAttemptsPaidSuccessfully = true,
                isIssuancePaid = false,
                soraBackEndUrl = BuildConfig.SORA_API_BASE_URL,
            )
        )
    }

    private fun buildClient(): String =
        "${BuildConfig.APPLICATION_ID}/${BuildConfig.BUILD_TYPE}/${BuildConfig.VERSION_CODE}/${BuildConfig.VERSION_NAME}/"
}
