import express from "express"
import { creatFuelType, deleteFuelType, getFuelTypes, updateFuelType } from "../controllers/fuelType.js"

const router = express.Router()


router.post("/", creatFuelType)
router.get("/",getFuelTypes)
router.patch("/:id", updateFuelType)
router.delete("/:id", deleteFuelType)


export default router
