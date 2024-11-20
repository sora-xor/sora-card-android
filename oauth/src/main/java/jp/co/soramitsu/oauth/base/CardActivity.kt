package jp.co.soramitsu.oauth.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import jp.co.soramitsu.androidfoundation.format.TextValue
import jp.co.soramitsu.androidfoundation.fragment.onActivityBackPressed
import jp.co.soramitsu.androidfoundation.intent.getParcelableCompat
import jp.co.soramitsu.oauth.base.navigation.Destination
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.navigation.SdkNavGraph
import jp.co.soramitsu.oauth.base.resources.ContextManager
import jp.co.soramitsu.oauth.base.sdk.SoraCardConstants
import jp.co.soramitsu.oauth.base.sdk.SoraCardConstants.BUNDLE_EXTRA_SORA_CARD_CONTRACT_DATA
import jp.co.soramitsu.oauth.base.sdk.SoraCardConstants.EXTRA_SORA_CARD_CONTRACT_DATA
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardContractData
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardResult
import jp.co.soramitsu.oauth.clients.ui.localCompositionUiStyle
import jp.co.soramitsu.oauth.common.domain.CurrentActivityRetriever
import jp.co.soramitsu.oauth.common.domain.PWOAuthClientProxy
import jp.co.soramitsu.oauth.feature.MainViewModel
import jp.co.soramitsu.oauth.feature.OAuthCallback
import jp.co.soramitsu.oauth.feature.terms.and.conditions.ProgressDialog
import jp.co.soramitsu.oauth.styledui.TextLargePrimaryButton
import jp.co.soramitsu.oauth.theme.AuthSdkTheme
import jp.co.soramitsu.oauth.theme.darkScrim
import jp.co.soramitsu.oauth.theme.lightScrim
import jp.co.soramitsu.ui_core.resources.Dimens
import jp.co.soramitsu.ui_core.theme.customColors
import jp.co.soramitsu.ui_core.theme.customTypography

@AndroidEntryPoint
class CardActivity : ComponentActivity() {

    @Inject
    lateinit var currentActivityRetriever: CurrentActivityRetriever

    private val vm: MainViewModel by viewModels()

    @Inject
    lateinit var pwoAuthClientProxy: PWOAuthClientProxy

    @Inject
    lateinit var mainRouter: MainRouter

    private val authCallback = object : OAuthCallback {
        override fun onOAuthSucceed() {
            vm.onAuthSucceed()
        }

        override fun onStartKyc() {
            vm.startKycProcess(this@CardActivity)
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(ContextManager.setBaseContext(base))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val bundle = intent.getBundleExtra(BUNDLE_EXTRA_SORA_CARD_CONTRACT_DATA)!!
        val contractData = bundle.getParcelableCompat(
            EXTRA_SORA_CARD_CONTRACT_DATA,
            SoraCardContractData::class.java,
        )!!
        ContextManager.setLocale(contractData.locale)
        currentActivityRetriever.setActivity(this)

        onActivityBackPressed {
            val result = SoraCardResult.Canceled
            setResult(
                mapSoraCardResult(result),
                Intent().putExtra(SoraCardConstants.EXTRA_SORA_CARD_RESULT, result),
            )
            finish()
        }

        setContent {
            DisposableEffect(key1 = contractData.clientDark) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT,
                    ) { contractData.clientDark },
                    navigationBarStyle = SystemBarStyle.auto(
                        lightScrim,
                        darkScrim,
                    ) { contractData.clientDark },
                )
                onDispose { }
            }
            LaunchedEffect(key1 = Unit) {
                vm.launch(contractData, this@CardActivity)
            }
            val navController = rememberNavController()
            mainRouter.attachNavController(this, navController)
            lifecycle.addObserver(mainRouter)
            CompositionLocalProvider(
                localCompositionUiStyle provides contractData.clientCase,
            ) {
                MainCardScreen(navController, contractData.clientDark)
            }
        }
    }

    @Composable
    private fun MainCardScreen(navController: NavHostController, dark: Boolean) {
        AuthSdkTheme(darkTheme = dark) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.customColors.bgSurface),
            ) {
                SdkNavGraph(
                    navHostController = navController,
                    startDestination = Destination.INIT_LOADING,
                    authCallback = authCallback,
                )
                val state = vm.uiState.collectAsStateWithLifecycle().value
                if (state.loading) {
                    ProgressDialog()
                }
                state.error?.let {
                    AlertDialog(
                        backgroundColor = MaterialTheme.customColors.bgSurface,
                        onDismissRequest = vm::onHideErrorDialog,
                        confirmButton = {
                            TextLargePrimaryButton(
                                modifier = Modifier
                                    .padding(vertical = Dimens.x1),
                                text = TextValue.StringRes(id = android.R.string.ok),
                                onClick = vm::onHideErrorDialog,
                                enabled = true,
                            )
                        },
                        title = {
                            Text(text = it, style = MaterialTheme.customTypography.headline3)
                        },
                    )
                }
            }
        }
    }
}
