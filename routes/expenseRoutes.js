import express from "express"
import { verifyToken } from "../middleware/auth.js"
import { createOtherExpense, deleteOtherExpense, getOtherExpense, updateOtherExpense } from "../controllers/otherexpense.js"


const router = express.Router()


router.post("/",createOtherExpense)
router.get("/",getOtherExpense)
router.patch("/:id",updateOtherExpense)
router.delete("/:id",deleteOtherExpense)


export default router
