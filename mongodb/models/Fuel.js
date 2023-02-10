import mongoose  from "mongoose"

const FuelSchema = new mongoose.Schema({
    vehicle: { type: String, required: true}, 
    fuelType: { type: String, required: true}, 
    Location: { type: String, required: true }, 
    cost: { type: Number, required: true }, 
    gallons: { type: Number, required: true},
})

const Fuel = mongoose.model('Fuel', FuelSchema)

export default Fuel