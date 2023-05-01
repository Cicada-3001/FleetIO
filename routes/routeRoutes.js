import express from "express"
import { verifyToken } from "../middleware/auth.js"
import { updateRoute, getRoute, createRoute, deleteRoute } from "../controllers/route.js"


const router = express.Router()

router.post("/", createRoute)
router.get("/", getRoute)
router.patch("/:id", updateRoute)
router.delete("/:id", deleteRoute)


export default router
