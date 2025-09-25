package be.he2b.starship

import kotlin.times

class Spaceship(startLocation : SpaceObject) {

    var location : SpaceObject = startLocation
        private set
    var fuel = 50.0
        private set
    var metalCargo = 0

    fun move(destination : SpaceObject) {
        val distanceToDestination = location.location.distance(destination.location)
        val fuelConsume = distanceToDestination * 0.1

        if (fuel >= fuelConsume) {
            fuel -= fuelConsume
            location = destination
        } else {
            throw IllegalStateException("Not enough fuel left to go to ${destination.name}. " +
                    "Fuel remaining : $fuel")
        }
    }

    fun refuel(quantity : Double) {
        if (quantity > 0) {
            fuel += quantity
        }
    }

}