import mongoose  from "mongoose"

const RouteSchema = new mongoose.Schema({
    userId: { type: String },
    startPoint : { type: String, required:true}, 
    endPoint : { type: String, required: true}, 
    estimateDistance : { type: Number, required: true}, 
    estimateTime : { type: String, required: true}, 
    estimateFareAmt: {type: Number, required: true }
})

const Route = mongoose.model('Route', RouteSchema)

export default Route 