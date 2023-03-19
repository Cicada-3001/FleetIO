import express from "express"
import { verifyToken } from "../middleware/auth.js"
import { createDriver, getDrivers, updateDriver, deleteDriver } from "../controllers/driver.js"


const router = express.Router()


router.post("/",createDriver)
router.get("/",getDrivers)
router.patch("/:id",updateDriver)
router.delete("/:id",deleteDriver)


export default router
