package jp.co.soramitsu.oauth.feature.terms.and.conditions

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.compose.Screen
import jp.co.soramitsu.oauth.base.extension.testTagAsId
import jp.co.soramitsu.oauth.feature.KycCallback
import jp.co.soramitsu.ui_core.component.button.FilledButton
import jp.co.soramitsu.ui_core.component.button.properties.Order
import jp.co.soramitsu.ui_core.component.button.properties.Size
import jp.co.soramitsu.ui_core.component.card.ContentCard
import jp.co.soramitsu.ui_core.component.item.MenuItem
import jp.co.soramitsu.ui_core.resources.Dimens
import jp.co.soramitsu.ui_core.theme.borderRadius
import jp.co.soramitsu.ui_core.theme.customColors
import jp.co.soramitsu.ui_core.theme.customTypography

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun TermsAndConditionsScreen(
    kycCallback: KycCallback,
    viewModel: TermsAndConditionsViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.setArgs(kycCallback)
    }

    Screen(
        viewModel = viewModel
    ) { scrollState ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(top = Dimens.x1, start = Dimens.x3, end = Dimens.x3, bottom = Dimens.x5)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Dimens.x2),
                text = stringResource(R.string.terms_and_conditions_description),
                style = MaterialTheme.customTypography.paragraphM,
                color = MaterialTheme.customColors.fgPrimary
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(MaterialTheme.borderRadius.s))
                    .background(MaterialTheme.customColors.accentTertiaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.x2),
                    text = AnnotatedString(
                        stringResource(R.string.terms_and_conditions_sora_community_alert),
                        spanStyles = listOf(
                            AnnotatedString.Range(SpanStyle(fontWeight = FontWeight.Bold), 0, 57)
                        )
                    ), style = MaterialTheme.customTypography.paragraphM,
                    color = MaterialTheme.customColors.accentTertiary
                )
            }

            TermsAndConditionsMenu(
                modifier = Modifier.padding(vertical = Dimens.x3),
                onGeneralTermsClick = viewModel::onGeneralTermsClick,
                onPrivacyPolicy = viewModel::onPrivacyPolicy,
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Dimens.x2),
                text = stringResource(R.string.terms_and_conditions_confirm_description),
                style = MaterialTheme.customTypography.textS,
                color = MaterialTheme.customColors.fgSecondary,
                textAlign = TextAlign.Center
            )

            FilledButton(
                modifier = Modifier
                    .testTagAsId("AcceptAndContinue")
                    .fillMaxWidth(),
                text = stringResource(R.string.terms_and_conditions_accept_and_continue),
                order = Order.SECONDARY,
                size = Size.Large,
                onClick = viewModel::onConfirm
            )
        }
    }
}

@Composable
private fun TermsAndConditionsMenu(
    modifier: Modifier = Modifier,
    onGeneralTermsClick: () -> Unit,
    onPrivacyPolicy: () -> Unit,
) {
    ContentCard(
        modifier = modifier.fillMaxWidth(),
        cornerRadius = MaterialTheme.borderRadius.s
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(MaterialTheme.borderRadius.s))
                .background(MaterialTheme.customColors.bgSurface)
        ) {
            MenuItem(
                label = stringResource(R.string.terms_and_conditions_general_terms),
                onClick = onGeneralTermsClick
            )
            MenuItem(
                label = stringResource(R.string.terms_and_conditions_privacy_policy),
                onClick = onPrivacyPolicy
            )
        }
    }
}
