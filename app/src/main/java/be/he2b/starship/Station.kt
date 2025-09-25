package be.he2b.starship

import kotlin.random.Random

class Station(
    name: String,
    location: Coordinates,
    private val metalPrice: Double
): SpaceObject(location, name) {

    fun exchangeMetalForFuel(metalQuantity: Int) : Double {
        if (metalQuantity <= 0) {
            throw Exception("Metal quantity must be greater than zero.")
        }

        val fuelQuantity = metalQuantity * metalPrice

        return fuelQuantity
    }

    companion object {
        fun createRandomStation() : Station {
            val randomXCoordinate = Random.nextDouble(-200.0, 200.0)
            val randomYCoordinate = Random.nextDouble(-200.0, 200.0)
            val randomZCoordinate = Random.nextDouble(-200.0, 200.0)
            val randomMetalPrice = Random.nextDouble(0.5, 2.0)
            val randomStationName = "Station_" + Random.nextInt(1, 1000)

            return Station(randomStationName, Coordinates(randomXCoordinate,
                randomYCoordinate, randomZCoordinate), randomMetalPrice)
        }
    }

}