import express from "express"
import { verifyToken } from "../middleware/auth.js"
import { createFuel, getFuel, updateFuel, deleteFuel} from "../controllers/fuel.js"


const router = express.Router()




router.post("/", createFuel)
router.get("/", getFuel)
router.patch("/:id", updateFuel)
router.delete("/:id", deleteFuel)




export default router
