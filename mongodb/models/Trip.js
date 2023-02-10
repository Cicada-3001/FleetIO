import mongoose  from "mongoose"

const TripSchema = new mongoose.Schema({
    vehicle: { type: Number, required: true}, 
    driver: { type: Number, required: true}, 
    startTime: { type: String, required: true }, 
    endTime: { type: String, required: true }, 
    startLocation: { type: String, required: true},
    endLocation: { type:String, required: true}, 
    purpose: {type:String}, 
    distanceTravelled: { type:Number, required: true }, 
    fuelUsed: { type: String, required: true},
})

const Trip = mongoose.model('Trip', TripSchema)

export default Trip