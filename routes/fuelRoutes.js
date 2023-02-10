import express from "express"
import { verifyToken } from "../middleware/auth"
import { createFuel, getFuel, updateFuel } from "../controllers/fuel"


const router = express.Router()




router.post("/", verifyToken, createFuel)
router.get("/", verifyToken, getFuel)
router.patch("/", verifyToken,updateFuel)




export default router
