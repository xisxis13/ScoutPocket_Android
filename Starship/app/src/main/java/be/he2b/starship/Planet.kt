package be.he2b.starship

import kotlin.random.Random

class Planet(name: String, location: Coordinates,
             val exploitationRate: Int, var metalResources: Int): SpaceObject(location, name) {

    fun mine() : Int {
        var amountMined : Int

        if (metalResources >= exploitationRate) {
            amountMined = exploitationRate
        } else {
            amountMined = metalResources
        }

        metalResources -= amountMined

        return amountMined
    }

    companion object {
        val syllabeList = listOf<String>(
            "ju", "pi", "ter", "ma", "rs", "sa", "turn", "ve", "nus", "nep", "tu", "ne", "plu",
            "to", "er", "ra", "mi", "ro", "na", "li", "sa", "ta", "li", "on", "ce", "de", "xe",
            "bi", "zo", "ka", "lu", "xi"
        )

        fun createRandomPlanet() : Planet {
            val randomXCoordinate = Random.nextDouble(-200.0, 200.0)
            val randomYCoordinate = Random.nextDouble(-200.0, 200.0)
            val randomZCoordinate = Random.nextDouble(-200.0, 200.0)
            val randomExploitationRate = Random.nextInt(10, 50)
            val randomMetalResources = Random.nextInt(10, 30)

            var randomPlanetName = ""

            for (i in 0..2) {
                randomPlanetName += syllabeList.get(Random.nextInt(0, syllabeList.size))
            }

            return Planet(randomPlanetName, Coordinates(randomXCoordinate,
                randomYCoordinate, randomZCoordinate), randomExploitationRate,
                randomMetalResources)
        }
    }

}