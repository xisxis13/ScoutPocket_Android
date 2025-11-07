package be.he2b.starship

enum class GameState {
    IN_PROGRESS,
    GAME_OVER_WON,
    GAME_OVER_LOST,
}

object GameManager {
    var spaceship : Spaceship? = null
    var destinations = mutableListOf<SpaceObject>()
    var gameState : GameState = GameState.GAME_OVER_LOST

    private fun createUniverse() {
        for (i in 1..50) {
            destinations.add(Planet.createRandomPlanet())
        }

        for (i in 1..10) {
            destinations.add(Station.createRandomStation())
        }
    }

    fun startGame(displayGameRound: () -> Unit) {
        createUniverse()
        spaceship = Spaceship(destinations[0])
        gameState = GameState.IN_PROGRESS

        while (gameState == GameState.IN_PROGRESS) {
            displayGameRound()
        }
    }

    fun listClosestDestinations() : List<SpaceObject> {
        var list : List<SpaceObject> = destinations
            .sortedBy { spaceship?.location?.location?.distance(it.location) }
            .take(6)

        list = list.drop(1)

        println("Closest destinations: ")

        for (i in 0..4) {
            println("${i+1}. ${list.get(i).name} (distance from current position : ${spaceship?.location?.location?.distance(list.get(i).location)})")
        }

        return list
    }

    fun moveToDestination(destination : SpaceObject, success : () -> Unit) {
        spaceship?.move(destination)

        var moveSuccessful = false

        if (spaceship?.location == destination) {
            moveSuccessful = true
        }

        if (moveSuccessful) {
            success()
        } else {
            println("Failed to move to ${destination.name}.")
        }

    }

}