import express from "express"
import { verifyToken } from "../middleware/auth.js"
import { createVehicle, getVehicles, updateVehicle, deleteVehicle, getVehicleByVin, markGeofence, assignDriver } from "../controllers/vehicle.js"

const router = express.Router()

router.post("/", createVehicle)
router.get("/", getVehicles)
router.get("/:vin", getVehicleByVin)
router.patch("/:id", updateVehicle)
router.patch("/assignDriver/:id/:driver",assignDriver)
router.delete("/:id",deleteVehicle)
router.patch("/markgeofence/:id",markGeofence)




export default router

