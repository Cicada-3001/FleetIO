import express from "express"
import { verifyToken } from "../middleware/auth.js"
import { updateMaintenance, createMaintenance, getMaintenance, deleteMaintenance, markMaintenance } from "../controllers/maintance.js"


const router = express.Router()

router.post("/", createMaintenance)
router.get("/", getMaintenance)
router.patch("/:id", updateMaintenance)
router.patch("/markmaintenance/:id", markMaintenance)
router.delete("/:id", deleteMaintenance)


export default router
