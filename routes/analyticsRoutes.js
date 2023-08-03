import express from "express"
import { rankDrivers, vehicleStats, rankVehicles, rankRoutes, getCounts, routeStats, driverStats   } from "../controllers/analytics.js"

const router = express.Router()


router.get("/rank/driver", rankDrivers)
router.get("/rank/vehicle", rankVehicles)
router.get("/rank/route", rankRoutes)
router.get("/vehiclestats/:startDate/:endDate",vehicleStats)
router.get("/routestats/:startDate/:endDate",routeStats)
router.get("/driverstats/:startDate/:endDate",driverStats)
router.get("/counts",getCounts)



export default router