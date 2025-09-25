package be.he2b.starship

class GameConsole {

    fun startGame() {

    }

    fun displayStatus() {
        println("===== Status =====")
        println("Location : ${GameManager.spaceship?.location?.name} | " +
                "fuel : ${GameManager.spaceship?.fuel}) | " +
                "metal cargo : " + GameManager.spaceship?.metalCargo)
        println("==================")
    }

}