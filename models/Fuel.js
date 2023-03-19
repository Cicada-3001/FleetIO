import mongoose  from "mongoose"

const FuelSchema = new mongoose.Schema({
    vehicle: { type:  Object}, 
    fuelType: { type: String, required: true}, 
    location: { type: String, required: true }, 
    cost: { type: Number, required: true }, 
    gallons: { type: Number, required: true},
})

const Fuel = mongoose.model('Fuel', FuelSchema)

export default Fuel