package be.he2b.starship

fun main() {

    print("What's your name ? : ")
    val name = readln()

    displayWelcomeMessage(name)

    val planetList : List<Planet> = listOf(
        Planet("Earth", Coordinates(0.0, 0.0, 0.0),
            10, 1000),
        Planet("Mars", Coordinates(-100.0, -100.0, 10.0),
            5, 500),
        Planet("Jupiter", Coordinates(400.0, 400.0, 200.0),
            8, 800)
    )

    val spaceship = Spaceship(planetList.get(0))
    spaceship.move(planetList.get(1))
    spaceship.move(planetList.get(2))

}

fun displayWelcomeMessage(name: String?) {
    val upperCaseName = name?.uppercase()

    if (!upperCaseName.isNullOrBlank()) {
        println("$upperCaseName, welcome to Starship")
    } else {
        println("Welcome to Starship")
    }
}