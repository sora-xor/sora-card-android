package jp.co.soramitsu.oauth.base

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.paywings.oauth.android.sdk.initializer.PayWingsOAuthClient
import com.paywings.onboarding.kyc.android.sdk.PayWingsOnboardingKycContract
import dagger.hilt.android.AndroidEntryPoint
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.extension.getParcelableCompat
import jp.co.soramitsu.oauth.base.resources.ContextManager
import jp.co.soramitsu.oauth.base.sdk.Mode
import jp.co.soramitsu.oauth.base.sdk.SoraCardConstants.BUNDLE_EXTRA_SORA_CARD_CONTRACT_DATA
import jp.co.soramitsu.oauth.base.sdk.SoraCardConstants.EXTRA_SORA_CARD_CONTRACT_DATA
import jp.co.soramitsu.oauth.base.sdk.SoraCardConstants.SIGN_IN_BUNDLE_EXTRA
import jp.co.soramitsu.oauth.base.sdk.SoraCardConstants.SIGN_IN_DATA
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardContractData
import jp.co.soramitsu.oauth.base.sdk.toPayWingsType
import jp.co.soramitsu.oauth.common.navigation.coordinator.api.NavigationCoordinator
import jp.co.soramitsu.oauth.core.engines.activityresult.api.ActivityResult
import jp.co.soramitsu.oauth.core.engines.router.api.ComposeRouter
import jp.co.soramitsu.oauth.core.engines.router.api.SoraCardDestinations
import jp.co.soramitsu.oauth.theme.AuthSdkTheme
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CardActivity : AppCompatActivity(R.layout.card_activity) {

    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var coordinator: NavigationCoordinator

    @Inject
    lateinit var composeRouter: ComposeRouter

    @Inject
    lateinit var activityResult: ActivityResult

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(ContextManager.setBaseContext(base))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AuthSdkTheme {
                Box(modifier = Modifier.fillMaxWidth()) {
                    val isLoading = remember {
                        derivedStateOf {
                            composeRouter.startDestination.value === SoraCardDestinations.Loading
                        }
                    }

                    if(isLoading.value) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .wrapContentSize()
                                .align(Alignment.Center)
                        )
                    }

                    SoraCardNavGraph(
                        navHostController = composeRouter.navController,
                        startDestination = composeRouter.startDestination.value,
                    )
                }
            }
        }

        with(activityResult) {
            setActivity(this@CardActivity)

            registerForActivityResult(PayWingsOnboardingKycContract()) {
                viewModel.setPayWingsKycResult(it)
            }.run { activityResult.setKycContract(this) }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.RESUMED) {
                coordinator.start(this)
            }
        }

        intent.getBundleExtra(BUNDLE_EXTRA_SORA_CARD_CONTRACT_DATA)
            ?.let(::setUpRegistrationFlow)

        intent.getBundleExtra(SIGN_IN_BUNDLE_EXTRA)
            ?.let(::setUpSignInFlow)
    }

    private fun setUpRegistrationFlow(bundle: Bundle) {
        val contractData = bundle.getParcelableCompat(
            EXTRA_SORA_CARD_CONTRACT_DATA,
            SoraCardContractData::class.java
        )

        contractData?.let { data ->
            ContextManager.setLocale(data.locale)

            viewModel.inMemoryRepo.endpointUrl = data.kycCredentials.endpointUrl
            viewModel.inMemoryRepo.username = data.kycCredentials.username
            viewModel.inMemoryRepo.password = data.kycCredentials.password
            viewModel.inMemoryRepo.soraCardInfo = data.soraCardInfo
            viewModel.inMemoryRepo.mode = Mode.REGISTRATION
            viewModel.inMemoryRepo.environment = data.environment
            viewModel.inMemoryRepo.client = data.client

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
            viewModel.inMemoryRepo.endpointUrl = data.kycCredentials.endpointUrl
            viewModel.inMemoryRepo.username = data.kycCredentials.username
            viewModel.inMemoryRepo.password = data.kycCredentials.password
            viewModel.inMemoryRepo.soraCardInfo = data.soraCardInfo
            viewModel.inMemoryRepo.mode = Mode.SIGN_IN
            viewModel.inMemoryRepo.environment = data.environment
            viewModel.inMemoryRepo.client = data.client
            viewModel.inMemoryRepo.userAvailableXorAmount = data.userAvailableXorAmount

            PayWingsOAuthClient.init(
                applicationContext,
                data.environment.toPayWingsType(),
                data.apiKey,
                data.domain
            )
        }
    }
}
