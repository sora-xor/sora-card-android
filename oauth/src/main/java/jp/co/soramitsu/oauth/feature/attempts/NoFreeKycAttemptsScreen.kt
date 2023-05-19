package jp.co.soramitsu.oauth.feature.attempts

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.compose.Screen
import jp.co.soramitsu.oauth.feature.KycCallback
import jp.co.soramitsu.ui_core.component.button.TonalButton
import jp.co.soramitsu.ui_core.component.button.properties.Order
import jp.co.soramitsu.ui_core.component.button.properties.Size
import jp.co.soramitsu.ui_core.resources.Dimens
import jp.co.soramitsu.ui_core.theme.customColors
import jp.co.soramitsu.ui_core.theme.customTypography

@Composable
fun NoFreeKycAttemptsScreen(
    kycCallback: KycCallback,
    viewModel: NoFreeKycAttemptsViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.setArgs(kycCallback)
    }

    BackHandler {
        viewModel.onClose()
    }

    Screen(
        viewModel = viewModel
    ) { scrollState ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(top = Dimens.x3, start = Dimens.x3, end = Dimens.x3, bottom = Dimens.x5)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.no_free_kyc_attempts_description),
                style = MaterialTheme.customTypography.paragraphM,
                color = MaterialTheme.customColors.fgPrimary
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_verification_in_progress),
                    contentDescription = null
                )
            }

            TonalButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.x3),
                order = Order.SECONDARY,
                size = Size.Large,
                text = stringResource(R.string.common_close),
                enabled = true,
                onClick = viewModel::onClose
            )
        }
    }
}
