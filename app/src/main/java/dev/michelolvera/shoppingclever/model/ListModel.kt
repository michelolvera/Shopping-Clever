package dev.michelolvera.shoppingclever.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class ListModel(
    var title: String? = "",
    var purchaseBudget: Double = 0.0,
    var color: String? = ""
)