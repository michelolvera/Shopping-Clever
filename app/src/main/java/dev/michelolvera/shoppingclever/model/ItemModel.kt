package dev.michelolvera.shoppingclever.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class ItemModel(
    var name: String = ""
)