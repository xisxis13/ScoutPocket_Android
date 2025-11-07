package be.he2b.starship

import kotlin.math.pow
import kotlin.math.sqrt

class Coordinates(val x: Double, val y: Double, val z: Double) {

    fun distance(other : Coordinates): Double {
        return sqrt((this.x - other.x).pow(2) +
                (this.y - other.y).pow(2) +
                (this.z - other.z).pow(2));
    }

}