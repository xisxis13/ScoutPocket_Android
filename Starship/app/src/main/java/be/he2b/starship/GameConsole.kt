package be.he2b.starship

class GameConsole {

    fun startGame() {
        GameManager.startGame() {
            displayStatus()
            askQuestion()
        }
    }

    fun displayStatus() {
        println("===== Status =====")
        println("Location : ${GameManager.spaceship?.location?.name} | " +
                "fuel : ${GameManager.spaceship?.fuel} | " +
                "metal cargo : " + GameManager.spaceship?.metalCargo)
        println("==================")
    }

    fun askQuestion() {
        println("Command: (t)ravel, (m)ine, (r)efuel, (q)uit")
        val input = readln()

        when (input) {
            "t" -> travelAction()
            else -> println("Unknown command. Please try again.")
        }
    }

    fun travelAction() {
        val listClosestDestination = GameManager.listClosestDestinations()
        val input = readln()
        val choice = input.toIntOrNull()
        var newPosition : SpaceObject? = null

        if (choice != null && choice > 0 && choice < 6) {
            newPosition = listClosestDestination.get(choice-1)
            GameManager.moveToDestination(newPosition) {
                println("Successfully moved to ${newPosition.name}.")
            }
        } else {
            println("Unknown command. Please try again.")
        }
    }
}