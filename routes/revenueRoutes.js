import express from "express"
import { verifyToken } from "../middleware/auth.js"
import { createOtherRevenue, deleteOtherRevenue, getOtherRevenue, updateOtherRevenue } from "../controllers/otherrevenue.js"


const router = express.Router()


router.post("/",createOtherRevenue)
router.get("/",getOtherRevenue)
router.patch("/:id",updateOtherRevenue)
router.delete("/:id",deleteOtherRevenue)


export default router