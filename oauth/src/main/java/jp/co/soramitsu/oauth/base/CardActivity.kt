package jp.co.soramitsu.oauth.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.paywings.onboarding.kyc.android.sdk.PayWingsOnboardingKycContract
import com.paywings.onboarding.kyc.android.sdk.data.model.KycContractData
import com.paywings.onboarding.kyc.android.sdk.data.model.KycCredentials
import com.paywings.onboarding.kyc.android.sdk.data.model.KycSettings
import com.paywings.onboarding.kyc.android.sdk.util.PayWingsOnboardingKycResult
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID
import javax.inject.Inject
import jp.co.soramitsu.oauth.base.extension.getParcelableCompat
import jp.co.soramitsu.oauth.base.extension.onActivityBackPressed
import jp.co.soramitsu.oauth.base.navigation.Destination
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.navigation.SdkNavGraph
import jp.co.soramitsu.oauth.base.resources.ContextManager
import jp.co.soramitsu.oauth.base.sdk.SoraCardConstants
import jp.co.soramitsu.oauth.base.sdk.SoraCardConstants.BUNDLE_EXTRA_SORA_CARD_CONTRACT_DATA
import jp.co.soramitsu.oauth.base.sdk.SoraCardConstants.EXTRA_SORA_CARD_CONTRACT_DATA
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardContractData
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardResult
import jp.co.soramitsu.oauth.common.domain.CurrentActivityRetriever
import jp.co.soramitsu.oauth.common.domain.PWOAuthClientProxy
import jp.co.soramitsu.oauth.feature.MainViewModel
import jp.co.soramitsu.oauth.feature.OAuthCallback
import jp.co.soramitsu.oauth.feature.terms.and.conditions.ProgressDialog
import jp.co.soramitsu.oauth.theme.AuthSdkTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CardActivity : ComponentActivity() {

    private val vm: MainViewModel by viewModels()

    @Inject
    lateinit var currentActivityRetriever: CurrentActivityRetriever

    @Inject
    lateinit var pwoAuthClientProxy: PWOAuthClientProxy

    @Inject
    lateinit var mainRouter: MainRouter

    private val onboardingKyc = registerForActivityResult(
        PayWingsOnboardingKycContract(),
    ) { payWingsOnboardingKycResult ->
        when (payWingsOnboardingKycResult) {
            is PayWingsOnboardingKycResult.Success -> {
                vm.checkKycStatus()
            }

            is PayWingsOnboardingKycResult.Failure -> {
                vm.onKycFailed(
                    statusDescription = payWingsOnboardingKycResult.statusDescription,
                )
            }
        }
    }

    private var authCallback = object : OAuthCallback {
        override fun onOAuthSucceed(accessToken: String) {
            vm.onAuthSucceed(accessToken)
        }

        override fun onStartKyc() {
            vm.getUserData()
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(ContextManager.setBaseContext(base))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentActivityRetriever.setActivity(this)

        intent.getBundleExtra(BUNDLE_EXTRA_SORA_CARD_CONTRACT_DATA)
            ?.let(::setUpRegistrationFlow)

        onActivityBackPressed {
            val result = SoraCardResult.Canceled
            setResult(
                mapSoraCardResult(result),
                Intent().putExtra(SoraCardConstants.EXTRA_SORA_CARD_RESULT, result),
            )
            finish()
        }
        vm.toast.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        }

        lifecycleScope.launch {
            vm.state
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .distinctUntilChanged()
                .collectLatest { state ->
                    if (state?.referenceNumber != null && state.kycUserData != null && state.userCredentials != null) {
                        onboardingKyc.launch(
                            KycContractData(
                                KycCredentials(
                                    endpointUrl = vm.inMemoryRepo.endpointUrl,
                                    username = vm.inMemoryRepo.username,
                                    password = vm.inMemoryRepo.password,
                                ),
                                KycSettings(
                                    appReferenceId = UUID.randomUUID().toString(),
                                    language = vm.inMemoryRepo.locale,
                                    referenceNumber = state.referenceNumber,
                                ),
                                userData = state.kycUserData,
                                userCredentials = state.userCredentials,
                            ),
                        )
                    }
                }
        }

        setContent {
            val navController = rememberNavController()
            mainRouter.attachNavController(this, navController)
            lifecycle.addObserver(mainRouter)
            MainCardScreen(navController)
        }
    }

    @Composable
    private fun MainCardScreen(navController: NavHostController) {
        AuthSdkTheme {
            Box(modifier = Modifier.fillMaxWidth()) {
                SdkNavGraph(
                    navHostController = navController,
                    startDestination = Destination.TERMS_AND_CONDITIONS,
                    authCallback = authCallback,
                )
                val loading = vm.uiState.collectAsStateWithLifecycle()
                if (loading.value.loading) {
                    ProgressDialog()
                }
            }
        }
    }

    private fun setUpRegistrationFlow(bundle: Bundle) {
        val contractData = bundle.getParcelableCompat(
            EXTRA_SORA_CARD_CONTRACT_DATA,
            SoraCardContractData::class.java,
        )

        contractData?.let { data ->
            ContextManager.setLocale(data.locale)

            vm.inMemoryRepo.locale = data.locale.country
            vm.inMemoryRepo.endpointUrl = data.kycCredentials.endpointUrl
            vm.inMemoryRepo.username = data.kycCredentials.username
            vm.inMemoryRepo.password = data.kycCredentials.password
            vm.inMemoryRepo.environment = data.basic.environment
            vm.inMemoryRepo.soraBackEndUrl = data.soraBackEndUrl
            vm.inMemoryRepo.client = data.client
            vm.inMemoryRepo.userAvailableXorAmount = data.userAvailableXorAmount
            vm.inMemoryRepo.areAttemptsPaidSuccessfully = data.areAttemptsPaidSuccessfully
            vm.inMemoryRepo.isEnoughXorAvailable = data.isEnoughXorAvailable
            vm.inMemoryRepo.isIssuancePaid = data.isIssuancePaid

            pwoAuthClientProxy.init(
                applicationContext,
                data.basic.environment,
                data.basic.apiKey,
                data.basic.domain,
            )
        }
    }
}
