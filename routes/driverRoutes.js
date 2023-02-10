import express from "express"
import { verifyToken } from "../middleware/auth"
import { createDriver, getDrivers, updateDriver } from "../controllers/driver"


const router = express.Router()


router.post("/", verifyToken, createDriver)
router.get("/", verifyToken, getDrivers)
router.patch("/", verifyToken,updateDriver)


export default router
