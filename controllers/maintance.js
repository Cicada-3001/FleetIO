import Maintenance from "../models/Maintance.js"
import Trip from "../models/Trip.js"

export const createMaintenance = async (req, res) =>{
    try{
        const {
            userId,
            vehicle,
            maintenanceType,
            date, 
            cost,
            description, 
            maintained
        }  = req.body 

        const newMaintenance = new Maintenance({ 
            userId,
            vehicle, 
            maintenanceType, 
            date, 
            cost, 
            description, 
            maintained
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
        const maintenance = await Maintenance.find().populate(
            {   path: 'vehicle',
                populate: 
                [ 
                'fuelType',
                'route',
                'driver'
                ]
            }
        );
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
            userId,
            vehicle,
            maintenanceType,
            date, 
            cost, 
            description, 
            maintained
        }  = req.body 
        const updatedMaintenance= Maintenance.findByIdAndUpdate(
            id, 
        {
                userId,
                vehicle, 
                maintenanceType, 
                date, 
                cost, 
                description, 
                maintained
            },
            function  (err, result)  {
                if (err){
                    console.log(err)
                }
                else{
                    res.status(200).json(result)
                }
            });
    }catch (err){
        res.status(404).json({ message: err.message})
    }
}


/* delete */ 
export const markMaintenance = async (req, res)=> {
    try{
        const { id }  = req.params

        const maintenance = Maintenance.findById(id)
        const updatedMaintenance= await Maintenance.findByIdAndUpdate(
            id, 
            {
               maintained:  maintenance.maintained
            },
            (err, result) => {
                if (err){
                    console.log(err)
                }
                else{
                    res.status(200).json(result)
                }
            });
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












