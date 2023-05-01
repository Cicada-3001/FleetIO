import mongoose from "mongoose";
import Vehicle from "../models/Vehicles.js";
import Driver from "../models/Driver.js"
import Fuel from "../models/Fuel.js"
import Maintenance from "../models/Maintance.js";
import Trip from "../models/Trip.js";
import User from "../models/User.js"


const connectDB = (url) =>{
    mongoose.set('strictQuery', true)
    mongoose.connect(url).then(
        () => {

           // mongoose.connection.db.collection('drivers').updateMany({}, { $unset: { ['vehicle']: 1 } });
            console.log('MongoDB connected')
        }
    ).catch((err) => console.log(err))

}

export default connectDB