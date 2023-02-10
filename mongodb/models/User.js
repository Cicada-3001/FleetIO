import mongoose  from "mongoose"

const UserSchema = new mongoose.Schema({
    fullname: { type: String, required: true}, 
    phone: { type: String, required: true}, 
    email: { type: String, required: true }, 
    password: { type: String, required: true }, 
    created: { type: String, required: true},
    lastLogin: { type:String, required: true}, 
})

const User = mongoose.model('User', DriverSchema)

export default User