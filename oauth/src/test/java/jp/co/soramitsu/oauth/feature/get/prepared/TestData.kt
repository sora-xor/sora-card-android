package jp.co.soramitsu.oauth.feature.get.prepared

import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.feature.verification.result.prepared.model.Step

object TestData {

    val STEPS = listOf(
        Step(
            index = 1,
            title = R.string.get_prepared_submit_id_photo_title,
            description = R.string.get_prepared_submit_id_photo_description
        ),
        Step(
            index = 2,
            title = R.string.get_prepared_take_selfie_title,
            description = R.string.get_prepared_take_selfie_description
        ),
        Step(
            index = 3,
            title = R.string.get_prepared_proof_address_title,
            description = R.string.get_prepared_proof_address_description
        ),
        Step(
            index = 4,
            title = R.string.get_prepared_personal_info_title,
            description = R.string.get_prepared_personal_info_description
        )
    )
}
