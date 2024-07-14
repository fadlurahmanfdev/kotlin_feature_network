package co.id.fadlurahmanfdev.kotlin_feature_rtc.kotlin_feature_network.example.data.dto.model

import androidx.annotation.DrawableRes

data class FeatureModel(
    @DrawableRes val featureIcon: Int,
    val enum: String,
    val title: String,
    val desc: String? = null,
)
