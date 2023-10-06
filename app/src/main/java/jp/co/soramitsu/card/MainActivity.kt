package jp.co.soramitsu.card

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import jp.co.soramitsu.oauth.base.sdk.SoraCardEnvironmentType
import jp.co.soramitsu.oauth.base.sdk.SoraCardKycCredentials
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardBasicContractData
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardContract
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardContractData
import jp.co.soramitsu.oauth.clients.ClientsFacade
import jp.co.soramitsu.oauth.theme.AuthSdkTheme
import jp.co.soramitsu.ui_core.component.button.FilledButton
import jp.co.soramitsu.ui_core.component.button.properties.Order
import jp.co.soramitsu.ui_core.component.button.properties.Size
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val registrationLauncher = registerForActivityResult(
        SoraCardContract()
    ) {}

    @Inject
    lateinit var facade: ClientsFacade

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AuthSdkTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Button(onClick = { startRegistrationFlow() }) {
                            Text("registration")
                        }

                        FilledButton(size = Size.Large, order = Order.SECONDARY, text = "text") {
                            facade.init(basic(), applicationContext, BuildConfig.SORA_API_BASE_URL)
                            MainScope().launch {
                                facade.getKycStatus()
                                    .onFailure {
                                        Log.e("srms", "error ${it.localizedMessage}")
                                    }
                                    .onSuccess {
                                        Log.e("srms", "success $it")
                                    }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun startRegistrationFlow() {
        registrationLauncher.launch(
            SoraCardContractData(
                basic = basic(),
                locale = Locale.ENGLISH,
                kycCredentials = SoraCardKycCredentials(
                    endpointUrl = BuildConfig.SORA_CARD_KYC_ENDPOINT_URL,
                    username = BuildConfig.SORA_CARD_KYC_USERNAME,
                    password = BuildConfig.SORA_CARD_KYC_PASSWORD,
                ),
                client = buildClient(),
//                userAvailableXorAmount = 19.9,
//                isEnoughXorAvailable = false,
                userAvailableXorAmount = 19999.9,
                isEnoughXorAvailable = true,
                areAttemptsPaidSuccessfully = true,
                isIssuancePaid = false,
                soraBackEndUrl = BuildConfig.SORA_API_BASE_URL,
            )
        )
    }

    private fun basic() = SoraCardBasicContractData(
        apiKey = BuildConfig.SORA_CARD_API_KEY,
        domain = BuildConfig.SORA_CARD_DOMAIN,
        environment = SoraCardEnvironmentType.TEST,
    )

    private fun buildClient(): String =
        "${BuildConfig.APPLICATION_ID}/${BuildConfig.BUILD_TYPE}/${BuildConfig.VERSION_CODE}/${BuildConfig.VERSION_NAME}/"
}
