import mongoose  from "mongoose"

const VehicleSchema = new mongoose.Schema({
    userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
    vehicleType: { type: String, required: true}, 
    plateNumber: { type: String, required: true}, 
    make: { type: String, required: true }, 
    model: { type: String, required: true }, 
    year: { type: String, required: true},
    vin: { type:String, required: true}, 
    fuelType: {type: mongoose.Schema.Types.ObjectId, ref: 'FuelType' , required: true},
    odometerReading: {type:Number, required: true},
    currentLocation: {type:String, required: true},
    availability: { type: String, required: true},
    driver: {type: mongoose.Schema.Types.ObjectId, ref: 'Driver'},
    route: {type: mongoose.Schema.Types.ObjectId, ref: 'Route'}, 
    fuelConsumptionRate: { type: Number}, //fuel consumption rate  litres per km 
    imageUrl: { type: String }, 
    seatCapacity: { type: Number, required: true},
    operationArea:  { type: String },
    geofenceRadius: { type: Number }
})

const Vehicle = mongoose.model('Vehicle', VehicleSchema)

export default Vehicle 


/*
    User
    Vehicle
    Driver
    Fuel
    Maintenance
    Trip

 */

