import express from "express"
import { login } from "../controllers/auth.js"
import { createTrip, getTrips, updateTrip, deleteTrip} from "../controllers/trip.js"
import { verifyToken } from "../middleware/auth.js"
const router = express.Router()


router.post("/", createTrip)
router.get("/", getTrips)
router.patch("/:id", updateTrip)
router.delete("/:id",deleteTrip)


export default router
