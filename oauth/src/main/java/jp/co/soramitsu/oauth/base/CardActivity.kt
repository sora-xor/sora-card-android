package jp.co.soramitsu.oauth.base

import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModel
import com.paywings.oauth.android.sdk.initializer.PayWingsOAuthClient
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.extension.getParcelableCompat
import jp.co.soramitsu.oauth.base.resources.ContextManager
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.base.sdk.Mode
import jp.co.soramitsu.oauth.base.sdk.SoraCardConstants.BUNDLE_EXTRA_SORA_CARD_CONTRACT_DATA
import jp.co.soramitsu.oauth.base.sdk.SoraCardConstants.EXTRA_SORA_CARD_CONTRACT_DATA
import jp.co.soramitsu.oauth.base.sdk.SoraCardConstants.SIGN_IN_BUNDLE_EXTRA
import jp.co.soramitsu.oauth.base.sdk.SoraCardConstants.SIGN_IN_DATA
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardContractData
import jp.co.soramitsu.oauth.base.sdk.toPayWingsType
import jp.co.soramitsu.oauth.feature.MainFragment
import javax.inject.Inject

@HiltViewModel
class CardViewModel @Inject constructor(
    val inMemoryRepo: InMemoryRepo,
) : ViewModel() {

}

@AndroidEntryPoint
class CardActivity : AppCompatActivity(R.layout.card_activity) {

    private val vm: CardViewModel by viewModels()

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(ContextManager.setBaseContext(base))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent.getBundleExtra(BUNDLE_EXTRA_SORA_CARD_CONTRACT_DATA)
            ?.let(::setUpRegistrationFlow)

        intent.getBundleExtra(SIGN_IN_BUNDLE_EXTRA)
            ?.let(::setUpSignInFlow)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<MainFragment>(R.id.fragment_container_view)
            }
        }
    }

    private fun setUpRegistrationFlow(bundle: Bundle) {
        val contractData = bundle.getParcelableCompat(
            EXTRA_SORA_CARD_CONTRACT_DATA,
            SoraCardContractData::class.java
        )

        contractData?.let { data ->
            ContextManager.setLocale(data.locale)

            vm.inMemoryRepo.endpointUrl = data.kycCredentials.endpointUrl
            vm.inMemoryRepo.username = data.kycCredentials.username
            vm.inMemoryRepo.password = data.kycCredentials.password
            vm.inMemoryRepo.soraCardInfo = data.soraCardInfo
            vm.inMemoryRepo.mode = Mode.REGISTRATION
            vm.inMemoryRepo.environment = data.environment
            vm.inMemoryRepo.client = data.client

            PayWingsOAuthClient.init(
                applicationContext,
                data.environment.toPayWingsType(),
                data.apiKey,
                data.domain
            )
        }
    }

    private fun setUpSignInFlow(bundle: Bundle) {
        val contractData = bundle.getParcelableCompat(
            SIGN_IN_DATA,
            SoraCardContractData::class.java
        )

        contractData?.let { data ->
            ContextManager.setLocale(data.locale)
            vm.inMemoryRepo.endpointUrl = data.kycCredentials.endpointUrl
            vm.inMemoryRepo.username = data.kycCredentials.username
            vm.inMemoryRepo.password = data.kycCredentials.password
            vm.inMemoryRepo.soraCardInfo = data.soraCardInfo
            vm.inMemoryRepo.mode = Mode.SIGN_IN
            vm.inMemoryRepo.environment = data.environment
            vm.inMemoryRepo.client = data.client

            PayWingsOAuthClient.init(
                applicationContext,
                data.environment.toPayWingsType(),
                data.apiKey,
                data.domain
            )
        }
    }
}
