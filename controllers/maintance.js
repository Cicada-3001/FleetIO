import Maintenance from "../models/Maintance.js"


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
            status
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
                status
            }  
        )
        res.status(200).json(updatedMaintenance)
    }catch (err){
        res.status(404).json({ message: err.message})
    }
}



/* delete */ 
export const deleteMaintenance = async (req, res)=> {
    try{
        const {id } = req.params
        Maintenance.findOneAndDelete({ _id: id }, function (err, result) {
            if (err){
                res.status(200).json("Maintenance does not exist")
            }
            else{
                res.status(200).json(result)
            }
        });
    }catch (err){
        res.status(404).json( {message: err.message })
    }
}


