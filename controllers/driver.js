import Driver from  "../models/Driver.js"

export const createDriver = async (req, res) =>{
    try{
        const {
            firstName,
            lastName,
            licenseNumber, 
            dateOfBirth, 
            phoneNumber, 
            email, 
            vehicle
        }  = req.body 
       

        const newDriver = new Driver({ 
            firstName, 
            lastName, 
            licenseNumber, 
            dateOfBirth, 
            phoneNumber, 
            email, 
            vehicle
        })
        await newDriver.save()
        const driver = await Driver.find()
        res.status(201).json(driver)
    }catch(err){
        res.status(409).json({ message: err.message })
    }
}


/* read */ 
export const getDrivers = async (req, res)=>{
    try{
        const drivers = await Driver.find();
        res.status(200).json(drivers);
    }catch (err){
        res.status(404).json({ message: err.message})
    }
}



/* update */
export const updateDriver = async (req, res) =>{ 
    try{
        const { id }  = req.params
        const {
            firstName,
            lastName,
            licenseNumber, 
            dateOfBirth, 
            phoneNumber, 
            email, 
            vehicle
        }  = req.body 
        const driver = await Driver.findById(id)
        const updatedDriver= await Post.findByIdAndUpdate(
            id, 
            {
                firstName, 
                lastName, 
                licenseNumber, 
                dateOfBirth, 
                phoneNumber, 
                email, 
                vehicle 
            }  
        )

        res.status(200).json(updatedDriver)
    }catch (err){
        res.status(404).json({ message: err.message})
    }
}



