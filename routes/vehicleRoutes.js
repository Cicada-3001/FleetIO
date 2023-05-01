import express from "express"
import { verifyToken } from "../middleware/auth.js"
import { createVehicle, getVehicles, updateVehicle, deleteVehicle } from "../controllers/vehicle.js"

const router = express.Router()

router.post("/", createVehicle)
router.get("/", getVehicles)
router.patch("/:id", updateVehicle)
router.delete("/:id",deleteVehicle)



export default router
