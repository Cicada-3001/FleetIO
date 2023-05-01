import express from "express"
import * as dotenv  from 'dotenv'
import cors from "cors"
import connectDB from "./mongodb/connect.js"
import multer from "multer"
import helmet  from "helmet"
import morgan from "morgan"
import path from "path"
import bodyParser from "body-parser"
import  authRoutes from "./routes/auth.js"
import driverRoutes from "./routes/driverRoutes.js"
import  maintenanceRoutes from "./routes/maintenanceRoutes.js"
import tripRoutes from "./routes/tripRoutes.js"
import  vehicleRoutes from "./routes/vehicleRoutes.js"
import fuelRoutes  from "./routes/fuelRoutes.js"
import userRoutes from "./routes/userRoutes.js"
import routeRoutes from "./routes/routeRoutes.js"
import analyticsRoutes from "./routes/analyticsRoutes.js"
import fuelTypeRoutes from "./routes/fuelTypeRoutes.js"
import revenueRoutes from "./routes/revenueRoutes.js"
import expenseRoutes from "./routes/expenseRoutes.js"

import { getVehicle1Coordinates } from "./data/index.js";
import Vehicle from "./models/Vehicles.js"
import { fileURLToPath } from "url"
import { getCounts } from "./controllers/analytics.js"

const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)
dotenv.config();
const app = express()

app.use(cors())
app.use(express.json())
app.use(express.urlencoded({ extended: true }));
app.use(helmet())
app.use(helmet.crossOriginResourcePolicy({ policy: "cross-origin"})); 
app.use(morgan("common"))


/* routes */
app.use("/assets", express.static(path.join(__dirname,'public/assets')))
app.use('/api/v1/drivers',driverRoutes)
app.use('/api/v1/maintenances',maintenanceRoutes)
app.use('/api/v1/trips',tripRoutes)
app.use('/api/v1/vehicles',vehicleRoutes)
app.use('/api/v1/fuels',fuelRoutes)
app.use('/api/v1/users',userRoutes)
app.use('/api/v1/routes',routeRoutes)
app.use('/api/v1/analytics',analyticsRoutes)
app.use('/api/v1/fueltypes',fuelTypeRoutes)
app.use('/api/v1/otherrevenues',revenueRoutes)
app.use('/api/v1/otherexpenses',expenseRoutes)

/*  file storage */ 
const storage  = multer.diskStorage({
    destination: function (req, file, cb){
        cb(null, "public/assets")
    },
    filename: function (req, file, cb){
        cb(null, file.originalname)
    }
})

const upload= multer({ storage })
var  serverStarted=false





function  updateVehicle1 ()  {
    console.log("vehicle 1 has been called")
    let vehicle= Vehicle.find()[0]
    console.log(vehicle)
    if(vehicle){
        setVehicleLocation(vehicle)
    }
        

}
  
const updateVehicle2 = async () => {
    let vehicle= Vehicle.find()[1]
    if(vehicle)
        setVehicleLocation(vehicle)
   
}
   

const updateVehicle3 = async () => {
    let vehicle= Vehicle.find()[2]
    if(vehicle)
        setVehicleLocation(vehicle)
}


const  setVehicleLocation =  async (vehicle) =>{
    setInterval(updateVehicleLocation(vehicle),1000)
}

const updateVehicleLocation = async (vehicle) => {
    let i=0
    Vehicle.findByIdAndUpdate(vehicle._id, {currentLocation: getVehicle1Coordinates()[i]})
    i++
    console.log(vehicle.currentLocation)
}

const startServer = async () => {
    try{
        connectDB(process.env.MONGO_URL)
        serverStarted = true
        console.log(serverStarted)
    }catch(err){
        console.log(err)
    }
    app.listen(8080,'0.0.0.0', ()=> {
    console.log("Server has started on port http://localhost:8080")
})
}




startServer()