package be.he2b.starship

abstract class SpaceObject(val location : Coordinates, private val _name: String) {

    val name: String
        get() {
            if (_name.isEmpty()) {
                return "Obect unknown"
            } else {
                return _name
            }
        }

}