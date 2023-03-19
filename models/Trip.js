import mongoose  from "mongoose"

const TripSchema = new mongoose.Schema({
    userId: { type: String},
    vehicle: { type: Object}, 
    driver: { type:  Object}, 
    startTime: { type: String, required: true }, 
    endTime: { type: String, required: true }, 
    route: { type: Object, required: true}
})

const Trip = mongoose.model('Trip', TripSchema)

export default Trip