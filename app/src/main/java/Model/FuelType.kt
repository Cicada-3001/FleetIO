package Model

import java.io.Serializable


class FuelType(val _id: String?, val userId: String?, val name: String?, val price: Double):
    Serializable {
    override fun toString(): String {
        return name!!
    }
}