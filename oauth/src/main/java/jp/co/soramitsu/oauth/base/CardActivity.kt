package jp.co.soramitsu.oauth.base

import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.extension.getParcelableCompat
import jp.co.soramitsu.oauth.base.resources.ContextManager
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.base.sdk.SoraCardConstants.BUNDLE_EXTRA_SORA_CARD_CONTRACT_DATA
import jp.co.soramitsu.oauth.base.sdk.SoraCardConstants.EXTRA_SORA_CARD_CONTRACT_DATA
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardContractData
import jp.co.soramitsu.oauth.common.domain.CurrentActivityRetriever
import jp.co.soramitsu.oauth.common.domain.PWOAuthClientProxy
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

    @Inject
    lateinit var currentActivityRetriever: CurrentActivityRetriever

    @Inject
    lateinit var pwoAuthClientProxy: PWOAuthClientProxy

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(ContextManager.setBaseContext(base))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentActivityRetriever.setActivity(this)

        intent.getBundleExtra(BUNDLE_EXTRA_SORA_CARD_CONTRACT_DATA)
            ?.let(::setUpRegistrationFlow)

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
            vm.inMemoryRepo.environment = data.environment
            vm.inMemoryRepo.client = data.client
            vm.inMemoryRepo.userAvailableXorAmount = data.userAvailableXorAmount
            vm.inMemoryRepo.areAttemptsPaidSuccessfully = data.areAttemptsPaidSuccessfully
            vm.inMemoryRepo.isEnoughXorAvailable = data.isEnoughXorAvailable
            vm.inMemoryRepo.isIssuancePaid = data.isIssuancePaid

            pwoAuthClientProxy.init(
                applicationContext,
                data.environment,
                data.apiKey,
                data.domain,
            )
        }
    }
}
