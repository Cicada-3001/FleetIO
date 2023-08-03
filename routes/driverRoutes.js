import express from "express"
import { verifyToken } from "../middleware/auth.js"
import { createDriver, getDrivers, updateDriver, deleteDriver, assignVehicle } from "../controllers/driver.js"


const router = express.Router()


router.post("/",createDriver)
router.get("/",getDrivers)
router.patch("/:id",updateDriver)
router.patch("/assignvehicle/:id/:vehicle", assignVehicle)
router.delete("/:id",deleteDriver)


export default router
