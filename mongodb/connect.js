import mongoose from "mongoose";
import { vehicles } from "../data";
import Vehicle from "./models/Vehicles";

const connectDB = (url) =>{
    mongoose.set('strictQuery', true)
    mongoose.connect(url).then(
        () => {
            Vehicle.insertMany(vehicles)
            Maintenance.insertMany(maintenances)
            Driver.insertMany(vehicles)
            Trip.insertMany(vehicles)
            User.insertMany(vehicles)
            Fuel.insertMany(vehicles)




            console.log('MongoDB connected')
        }
    ).catch((err) => console.log(err))

}

export default connectDB