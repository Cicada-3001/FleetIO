import mongoose  from "mongoose"

const DriverSchema = new mongoose.Schema({
    userId: {type: mongoose.Schema.Types.ObjectId, ref: 'User'},
    firstName: { type: String, required: true}, 
    lastName: { type: String, required: true}, 
    licenseNumber: { type: String, required: true }, 
    licenseExpiry: { type: String, required: true },
    dateOfBirth: { type: String, required: true }, 
    phoneNumber: { type: String, required: true},
    email: { type:String, required: true}, 
    imageUrl: { type: String },
    vehicle: { type: mongoose.Schema.Types.ObjectId, ref: 'Vehicle'}
})

const Driver = mongoose.model('Driver', DriverSchema)

export default Driver