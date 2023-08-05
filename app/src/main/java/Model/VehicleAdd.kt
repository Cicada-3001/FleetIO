package Model

data class VehicleAdd(
    val userId: String?,
    val vehicleType: String?,
    val plateNumber: String?,
    val make: String?,
    val model: String?,
    val year: String?,
    val vin: String?,
    val fuelType: String?,
    val odometerReading: Double,
    val currentLocation: String?,
    val availability: String,
    val driver: String?,
    val route: String?,
    val fuelConsumptionRate: Double,
    val imageUrl: String?,
    val seatCapacity:Int
)
