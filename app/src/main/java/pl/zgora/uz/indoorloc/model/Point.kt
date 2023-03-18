package pl.zgora.uz.indoorloc.model

data class Point(var x: Double, var y: Double, var z: Double) {
    constructor(x: Double, y: Double) : this(x,y,0.0) {

    }

}

