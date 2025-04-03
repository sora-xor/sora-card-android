package jp.co.soramitsu.oauth.common.model

data class CountryDial(
    val code: String,
    val name: String,
    val dialCode: String,
)

internal val countryDialList = listOf(
    CountryDial(
        code = "DE",
        name = "Germany",
        dialCode = "+49 234",
    ),
    CountryDial(
        code = "BR",
        name = "Brazil",
        dialCode = "+55 376",
    ),
    CountryDial(
        code = "RU",
        name = "Russia",
        dialCode = "+7 777",
    ),
    CountryDial(
        code = "US",
        name = "USA",
        dialCode = "+1 666",
    ),
    CountryDial(
        code = "GB",
        name = "Britain",
        dialCode = "+44 460",
    ),
)
