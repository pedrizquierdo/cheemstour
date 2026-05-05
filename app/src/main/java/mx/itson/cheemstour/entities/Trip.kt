package mx.itson.cheemstour.entities

class Trip {

    var id: Int? = null
    var name: String? = null
    var city: String? = null
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    constructor(id: Int?, name: String, city: String, latitude: Double, longitude: Double) {
        this.id = id
        this.name = name
        this.city = city
        this.latitude = latitude
        this.longitude = longitude
    }
}