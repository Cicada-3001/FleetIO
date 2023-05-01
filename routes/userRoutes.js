import express from "express"
import { login, register } from "../controllers/auth.js"
import { deleteUser, getUsers, updateUser } from "../controllers/user.js"


const router = express.Router()


router.post("/login", login)
router.post("/register", register)
router.get("/", getUsers)
router.delete("/:id", deleteUser)
router.patch("/:id",updateUser)



export default router
