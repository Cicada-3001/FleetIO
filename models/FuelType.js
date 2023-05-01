import mongoose  from "mongoose"

const FuelTypeSchema = new mongoose.Schema({
    userId: {type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true},
    name: { type:String,  required: true}, 
    price: { type:Number, required: true}, 
})




const FuelType = mongoose.model('FuelType', FuelTypeSchema)

export default FuelType