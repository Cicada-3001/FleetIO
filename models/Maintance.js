import mongoose  from "mongoose"

const MaintenanceSchema = new mongoose.Schema({
    userId: { type: String },
    vehicle: { type: Object}, 
    maintenanceType: { type: String, required: true}, 
    date: { type: String, required: true }, 
    cost: { type: Number, required: true},
    description: { type:String, required: true}, 
    maintained: { type: Boolean, required:true}
})

const Maintenance = mongoose.model('Maintenance', MaintenanceSchema)

export default Maintenance