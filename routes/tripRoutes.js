import express from "express"
import { login } from "../controllers/auth.js"
import { createTrip, getTrips, updateTrip} from "../controllers/trip.js"
import { verifyToken } from "../middleware/auth.js"
const router = express.Router()


router.post("/", verifyToken, createTrip)
router.get("/", verifyToken, getTrips)
router.patch("/", verifyToken,updateTrip)


export default router
