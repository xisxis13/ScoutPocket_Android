package be.he2b.starship

fun main() {

//    print("What's your name ? : ")
//    val name = readln()
//
//    displayWelcomeMessage(name)
//
//    val planetList : List<Planet> = listOf(
//        Planet("Earth", Coordinates(0.0, 0.0, 0.0),
//            10, 1000),
//        Planet("Mars", Coordinates(-100.0, -100.0, 10.0),
//            5, 500),
//        Planet("Jupiter", Coordinates(400.0, 400.0, 200.0),
//            8, 800)
//    )
//
//    val spaceship = Spaceship(planetList.get(0))
//    spaceship.move(planetList.get(1))
//    planetList.get(1).mine()
//    spaceship.move(planetList.get(2))

    val planet0 = Planet("Earth", Coordinates(0.0, 0.0, 0.0),
        10, 1000)
    val planet1 = Planet("Mars", Coordinates(-100.0, -100.0, 10.0),
        5, 500)
    val station = Station("X5445", Coordinates(-100.0, -100.0, 10.0),
        5.0,)
    val planet2 = Planet("Jupiter", Coordinates(400.0, 400.0, 200.0),
        8, 800)

    val spaceship = Spaceship(planet0)

    try {
        // move to Mars
        spaceship.move(planet1)
        // extract some metal from Metal
        var minedMetal = planet1.mine()
        spaceship.metalCargo += minedMetal
        minedMetal = planet1.mine()
        spaceship.metalCargo += minedMetal
        // move to the space station
        spaceship.move(station)
        // refuel
        val fuel = station.exchangeMetalForFuel(spaceship.metalCargo)
        spaceship.refuel(fuel)

        // move to Jupiter
        spaceship.move(planet2)
    } catch (e: Exception) {
        println(e.message)
    }
}

fun displayWelcomeMessage(name: String?) {
    val upperCaseName = name?.uppercase()

    if (!upperCaseName.isNullOrBlank()) {
        println("$upperCaseName, welcome to Starship")
    } else {
        println("Welcome to Starship")
    }
}