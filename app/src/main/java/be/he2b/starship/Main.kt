package be.he2b.starship

fun main() {

    Planet.createRandomPlanet()
    // println(Planet.createRandomPlanet().name)

    Station.createRandomStation()
    // println(Station.createRandomStation().name)

}

fun displayWelcomeMessage(name: String?) {
    val upperCaseName = name?.uppercase()

    if (!upperCaseName.isNullOrBlank()) {
        println("$upperCaseName, welcome to Starship")
    } else {
        println("Welcome to Starship")
    }
}