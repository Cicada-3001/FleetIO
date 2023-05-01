import mongoose  from "mongoose"

const TripSchema = new mongoose.Schema({
    userId: {type: mongoose.Schema.Types.ObjectId, ref: 'User'},
    vehicle: {type: mongoose.Schema.Types.ObjectId, ref: 'Vehicle'}, 
    startTime: { type: String, required: true }, 
    endTime: { type: String, required: true }, 
})

const Trip = mongoose.model('Trip', TripSchema)

export default Trip