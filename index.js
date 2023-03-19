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

dotenv.config();
const app = express()

app.use(cors())
app.use(express.json())
app.use(express.urlencoded({ extended: true }));
app.use(helmet())
app.use(helmet.crossOriginResourcePolicy({ policy: "cross-origin"})); 
app.use(morgan("common"))





/* routes */

//app.use("/assets", express.static(path.join(__dirname, 'public/assets')))
app.use('/api/v1/drivers',driverRoutes)
app.use('/api/v1/maintenances',maintenanceRoutes)
app.use('/api/v1/trips',tripRoutes)
app.use('/api/v1/vehicles',vehicleRoutes)
app.use('/api/v1/fuels',fuelRoutes)
app.use('/api/v1/users',userRoutes)
app.use('/api/v1/routes',routeRoutes)



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



const startServer = async () => {
    try{
        connectDB(process.env.MONGO_URL)
    }catch(err){
        console.log(err)
    }
    app.listen(8080, ()=> console.log("Server has started on port http://localhost:8080"))
}




startServer();