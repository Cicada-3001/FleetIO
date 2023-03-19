import mongoose  from "mongoose"

const VehicleSchema = new mongoose.Schema({
    userId: { type: String },
    vehicleType: { type: String, required: true}, 
    plateNumber: { type: String, required: true}, 
    make: { type: String, required: true }, 
    model: { type: String, required: true }, 
    year: { type: String, required: true},
    vin: { type:Number, required: true}, 
    fuelType: {type:Object , required: true},
    odometerReading: {type:Number, required: true},
    currentLocation: {type:String, required: true},
    availability: {type:Boolean, required: true},
    driver: {type: Object},
    route: { type: Object}, 
    fuelConsumptionRate: { type: Number}, //fuel consumption rate  litres per km 
    imageUrl: { type: String }
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

