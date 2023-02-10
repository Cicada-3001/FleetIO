import mongoose  from "mongoose"

const MaintenanceSchema = new mongoose.Schema({
    vehicle: { type: String, required: true}, 
    maintenanceType: { type: String, required: true}, 
    date: { type: String, required: true }, 
    cost: { type: Number, required: true},
    description: { type:String, required: true}, 
   
})

const Maintenance = mongoose.model('Maintenance', MaintenanceSchema)

export default Maintenance