import bcrypt from "bcrypt"
import jwt from "jsonwebtoken"
import User from "../models/User.js";
import * as dotenv  from 'dotenv'


dotenv.config();


/* register user  */ 
export const register = async (req, res)=> {
    try{ 
        const { 
            fullname,
            phone, 
            email, 
            password,  
            created,
            lastLogin
        } = req.body;

        const user = await User.findOne( {email: email })

        if(user)
            res.status(200).json("The user with username "+email+" already exists")
        else{
            const salt = await bcrypt.genSalt()
            const passwordHash = await bcrypt.hash(password, salt)
            const createdOn = new Date()
            const newUser = new User({
                fullname, 
                phone, 
                email, 
                password: passwordHash, 
                created: createdOn,
                lastLogin: createdOn
            });
    
            const savedUser=  await newUser.save(); 
            res.status(201).json(savedUser)
        }
       
    } catch(err){
        res.status(500).json({error: err.message})
    }
}

 
/* login */ 
export const login = async (req, res) => {
    try{
        const { email, password } = req.body
        const user = await User.findOne( {email: email })
        if(!user){
            return res.status(400).json( {msg: "User does not exist"});}
        const isMatch = await bcrypt.compare(password, user.password)
        if(!isMatch) return res.status(400).json({ msg: "Invalid credentials. "})
        const token = jwt.sign({ id: user._id}, process.env.JWT_SECRET)
        delete user.password;
        User.findByIdAndUpdate(user._id, { lastLogin: new Date() },
            function (err, result) {
            if (err)
                res.status(200).json("Cannot assign route, an error occured")
            else{
                res.status(200).json(result)
                user= result
            } 
        });

        res.status(200).json( {user})
    } catch(err){
        res.status(500).json({ error: err.message })
    }
}



