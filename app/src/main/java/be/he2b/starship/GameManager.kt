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
}