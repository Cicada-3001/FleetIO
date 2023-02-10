import mongoose  from "mongoose"

const VehicleSchema = new mongoose.Schema({
    vehicleType: { type: String, required: true}, 
    plateNumber: { type: String, required: true}, 
    make: { type: String, required: true }, 
    model: { type: String, required: true }, 
    year: { type: String, required: true},
    vin: { type:Number, required: true}, 
    fuelType: {type:String, required: true},
    odometerReading: {type:Number, required: true},
    currentLocation: {type:String, required: true},
    availability: {type:Boolean, required: true},
  
})

const Vehicle = mongoose.model('Vehicle', VehicleSchema)

export default Vehicle 