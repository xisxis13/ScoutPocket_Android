package be.he2b.scoutpocket.database.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Unit(
    val id: String,
    val name: String,
    @SerialName("created_at") val createdAt: String? = null,
)
