package he2b.be.bored.network

data class BoredResponse(
    val activity: String,
    val type: String,
    val participants: Int,
    val price: Double,
)
