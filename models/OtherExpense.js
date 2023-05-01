import mongoose  from "mongoose"

const OtherExpenseSchema = new mongoose.Schema({
    userId: {type: mongoose.Schema.Types.ObjectId, ref: 'User'},
    description: { type:  String, required: true}, 
    amount: { type: Number, required: true}, 
    created: { type: String, required: true }, 
   
})

const OtherExpense = mongoose.model('OtherExpense', OtherExpenseSchema)

export default OtherExpense