package jp.co.soramitsu.oauth.feature.get.prepared

import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.feature.getprepared.Step

object TestData {

    val STEPS = listOf(
        Step(
            index = 1,
            title = R.string.get_prepared_submit_id_photo_title,
            description = listOf(R.string.get_prepared_submit_id_photo_description),
        ),
        Step(
            index = 2,
            title = R.string.get_prepared_take_selfie_title,
            description = listOf(R.string.get_prepared_take_selfie_description),
        ),
        Step(
            index = 3,
            title = R.string.get_prepared_proof_address_title,
            description = listOf(
                R.string.get_prepared_proof_address_description,
                R.string.get_prepared_proof_address_note,
            ),
        ),
        Step(
            index = 4,
            title = R.string.get_prepared_personal_info_title,
            description = listOf(R.string.get_prepared_personal_info_description),
        ),
    )
}
