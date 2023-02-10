import mongoose  from "mongoose"

const DriverSchema = new mongoose.Schema({
    firstName: { type: String, required: true}, 
    lastName: { type: String, required: true}, 
    licenseNumber: { type: String, required: true }, 
    dateOfBirth: { type: String, required: true }, 
    phoneNumber: { type: String, required: true},
    email: { type:String, required: true}, 
    vehicle: {type:String}
})

const Driver = mongoose.model('Driver', DriverSchema)

export default Driver