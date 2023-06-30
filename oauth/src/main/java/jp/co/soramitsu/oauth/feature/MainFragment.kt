package jp.co.soramitsu.oauth.feature

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import com.paywings.onboarding.kyc.android.sdk.PayWingsOnboardingKycContract
import com.paywings.onboarding.kyc.android.sdk.data.model.KycContractData
import com.paywings.onboarding.kyc.android.sdk.data.model.KycCredentials
import com.paywings.onboarding.kyc.android.sdk.data.model.KycSettings
import com.paywings.onboarding.kyc.android.sdk.util.PayWingsOnboardingKycResult
import dagger.hilt.android.AndroidEntryPoint
import jp.co.soramitsu.oauth.base.BaseFragment
import jp.co.soramitsu.oauth.base.extension.onBackPressed
import jp.co.soramitsu.oauth.base.navigation.Destination
import jp.co.soramitsu.oauth.base.navigation.SdkNavGraph
import jp.co.soramitsu.oauth.base.sdk.SoraCardConstants
import jp.co.soramitsu.oauth.base.sdk.SoraCardInfo
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardResult
import jp.co.soramitsu.oauth.feature.terms.and.conditions.ProgressDialog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.util.UUID

@AndroidEntryPoint
internal class MainFragment : BaseFragment() {

    private val viewModel: MainViewModel by viewModels()

    private val onboardingKyc = registerForActivityResult(
        PayWingsOnboardingKycContract()
    ) { payWingsOnboardingKycResult ->
        when (payWingsOnboardingKycResult) {
            is PayWingsOnboardingKycResult.Success -> {
                viewModel.checkKycStatus()
            }
            is PayWingsOnboardingKycResult.Failure -> {
                viewModel.onKycFailed(
                    statusDescription = payWingsOnboardingKycResult.statusDescription
                )
            }
        }
    }

    private var authCallback = object : OAuthCallback {
        override fun onOAuthSucceed(accessToken: String) {
            viewModel.onAuthSucceed(accessToken)
        }

        override fun onStartKyc() {
            viewModel.getUserData()
        }
    }

    private var kycCallback = object : KycCallback {
        override fun onFinish(result: SoraCardResult) {
            when (result) {
                is SoraCardResult.Success -> {
                    requireActivity().setResult(
                        Activity.RESULT_OK,
                        Intent().putExtra(SoraCardConstants.EXTRA_SORA_CARD_RESULT, result)
                    )
                }
                is SoraCardResult.Canceled,
                is SoraCardResult.Failure -> {
                    requireActivity().setResult(
                        Activity.RESULT_CANCELED,
                        Intent().putExtra(SoraCardConstants.EXTRA_SORA_CARD_RESULT, result)
                    )
                }
                else -> { /*DO NOTHING*/ }
            }
            requireActivity().finish()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onBackPressed {
            finishWithCancel()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .distinctUntilChanged()
                .collectLatest { state ->
                    if (state?.referenceNumber != null && state.kycUserData != null && state.userCredentials != null) {
                        onboardingKyc.launch(
                            KycContractData(
                                KycCredentials(
                                    endpointUrl = viewModel.inMemoryRepo.endpointUrl,
                                    username = viewModel.inMemoryRepo.username,
                                    password = viewModel.inMemoryRepo.password
                                ),
                                KycSettings(
                                    appReferenceId = UUID.randomUUID().toString(),
                                    referenceNumber = state.referenceNumber
                                ),
                                userData = state.kycUserData,
                                userCredentials = state.userCredentials
                            )
                        )
                    }
                }
        }
    }

    @Composable
    override fun NavGraph(navHostController: NavHostController) {
        Box(modifier = Modifier.fillMaxWidth()) {
            SdkNavGraph(
                navHostController = navHostController,
                startDestination = Destination.TERMS_AND_CONDITIONS,
                authCallback = authCallback,
                kycCallback = kycCallback
            )

            if (viewModel.uiState.loading) {
                ProgressDialog()
            }
        }
    }

    private fun finishWithCancel() {
        kycCallback.onFinish(SoraCardResult.Canceled)
    }
}
