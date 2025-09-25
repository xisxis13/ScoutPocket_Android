package be.he2b.starship

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

}