package be.he2b.scoutpocket.database.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UnitMembership(
    @SerialName("user_id") val userId: String,
    @SerialName("unit_id") val unitId: String,
    val role: String,
    val status: String,
    @SerialName("first_name") val firstName: String? = null,
    @SerialName("last_name") val lastName: String? = null,
)
