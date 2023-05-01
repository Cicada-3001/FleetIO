import express from "express"
import { rankDrivers, vehicleStats, rankVehicles, rankRoutes, getCounts   } from "../controllers/analytics.js"


const router = express.Router()


router.get("/rank/driver", rankDrivers)
router.get("/rank/vehicle", rankVehicles)
router.get("/rank/route", rankRoutes)
router.get("/vehiclestats",vehicleStats)
router.get("/counts",getCounts)



export default router