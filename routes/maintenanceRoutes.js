import express from "express"
import { verifyToken } from "../middleware/auth.js"
import { updateMaintenance, createMaintenance, getMaintenance } from "../controllers/maintance.js"


const router = express.Router()

router.post("/", verifyToken, createMaintenance)
router.get("/", verifyToken, getMaintenance)
router.patch("/", verifyToken,updateMaintenance)


export default router
