import mongoose  from "mongoose"

const OtherRevenueSchema = new mongoose.Schema({
    userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User'},
    description: { type:  String, required: true}, 
    amount: { type: Number, required: true}, 
    created: { type: String, required: true }, 
   
})

const OtherRevenue = mongoose.model('OtherRevenue', OtherRevenueSchema)

export default OtherRevenue