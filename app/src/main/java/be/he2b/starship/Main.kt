package be.he2b.starship

fun main() {

    val gameConsole = GameConsole()
    gameConsole.startGame()

}

fun displayWelcomeMessage(name: String?) {
    val upperCaseName = name?.uppercase()

    if (!upperCaseName.isNullOrBlank()) {
        println("$upperCaseName, welcome to Starship")
    } else {
        println("Welcome to Starship")
    }
}