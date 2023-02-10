import Maintenance from "../mongodb/models/Maintance.js"

export const createMaintenance = async (req, res) =>{
    try{
        const {
            vehicle,
            maintenanceType,
            date, 
            cost, 
            description, 
        }  = req.body 

        const newMaintenance = new Maintenance({ 
            vehicle, 
            maintenanceType, 
            date, 
            cost, 
            description
        })
        await newMaintenance.save()
        const maintenance = await Maintenance.find()
        res.status(201).json(maintenance)
    }catch(err){
        res.status(409).json({ message: err.message })
    }
}


/* read */ 
export const getMaintenance = async (req, res)=>{
    try{
        const maintenance = await Maintenance.find();
        res.status(200).json(maintenance);
    }catch (err){
        res.status(404).json({ message: err.message})
    }
}



/* update */
export const updateMaintenance = async (req, res) =>{ 
    try{
        const { id }  = req.params
        const {
            vehicle,
            maintenanceType,
            date, 
            cost, 
            description, 
        }  = req.body 
        const maintenance = await Maintenance.findById(id)
        const updatedMaintenance= await Maintenance.findByIdAndUpdate(
            id, 
            {
                vehicle, 
                maintenanceType, 
                date, 
                cost, 
                description, 
            }  
        )

        res.status(200).json(updatedMaintenance)
    }catch (err){
        res.status(404).json({ message: err.message})
    }
}



