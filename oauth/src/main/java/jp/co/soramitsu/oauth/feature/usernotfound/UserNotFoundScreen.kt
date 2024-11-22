package jp.co.soramitsu.oauth.feature.usernotfound

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import jp.co.soramitsu.androidfoundation.format.TextValue
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.feature.YourPhoneNumberText
import jp.co.soramitsu.oauth.uiscreens.compose.Screen
import jp.co.soramitsu.oauth.uiscreens.styledui.FilledLargePrimaryButton
import jp.co.soramitsu.oauth.uiscreens.styledui.LargeTonalButton
import jp.co.soramitsu.oauth.uiscreens.theme.AuthSdkTheme
import jp.co.soramitsu.ui_core.resources.Dimens
import jp.co.soramitsu.ui_core.theme.customColors
import jp.co.soramitsu.ui_core.theme.customTypography

@Composable
fun UserNotFoundScreen(viewModel: UserNotFoundViewModel = hiltViewModel()) {
    BackHandler {
        viewModel.onToolbarNavigation()
    }
    Screen(viewModel = viewModel) { _ ->
        UserNotFoundScreenInternal(
            phone = viewModel.state.collectAsStateWithLifecycle().value,
            onTryAnotherNumber = viewModel::onTryAnotherNumber,
            onRegisterNewAccount = viewModel::onRegisterNewAccount,
        )
    }
}

@Composable
private fun UserNotFoundScreenInternal(
    phone: String,
    onTryAnotherNumber: () -> Unit,
    onRegisterNewAccount: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.customColors.bgSurface)
            .padding(start = Dimens.x3, end = Dimens.x3, bottom = Dimens.x5),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.x3),
                text = stringResource(R.string.user_not_found),
                style = MaterialTheme.customTypography.headline1,
                color = MaterialTheme.customColors.fgPrimary,
                maxLines = 1,
            )
            YourPhoneNumberText(phone = phone, topPadding = Dimens.x2)
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.x3),
                text = stringResource(R.string.no_number_in_database),
                style = MaterialTheme.customTypography.paragraphM,
                color = MaterialTheme.customColors.fgPrimary,
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LargeTonalButton(
                modifier = Modifier.fillMaxWidth(),
                text = TextValue.StringRes(id = R.string.try_another_number),
                enabled = true,
                onClick = onTryAnotherNumber,
            )
            FilledLargePrimaryButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.x2),
                text = TextValue.StringRes(id = R.string.register_new_account),
                onClick = onRegisterNewAccount,
                enabled = true,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewUserNotFoundScreenInternal() {
    AuthSdkTheme {
        UserNotFoundScreenInternal(
            phone = "+9886438537658377644646",
            onRegisterNewAccount = {},
            onTryAnotherNumber = {},
        )
    }
}
