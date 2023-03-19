import express from "express"
import { verifyToken } from "../middleware/auth.js"
import { updateMaintenance, createMaintenance, getMaintenance, deleteMaintenance } from "../controllers/maintance.js"


const router = express.Router()

router.post("/", createMaintenance)
router.get("/", getMaintenance)
router.patch("/:id", updateMaintenance)
router.delete("/:id", deleteMaintenance)


export default router
