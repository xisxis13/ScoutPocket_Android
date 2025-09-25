package be.he2b.starship

fun main() {

    print("What's your name ? : ")
    val name = readln()

    displayWelcomeMessage(name)

}

fun displayWelcomeMessage(name: String?) {
    val upperCaseName = name?.uppercase()

    if (!upperCaseName.isNullOrBlank()) {
        println("$upperCaseName, welcome to Starship")
    } else {
        println("Welcome to Starship")
    }
}