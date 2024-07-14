package co.id.fadlurahmanfdev.kotlin_feature_rtc.kotlin_feature_network.example.data.dto.response

data class BaseIdentityBankMasResponse<T>(
    val message: String,
    val data: T,
)
