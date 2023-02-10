import Vehicle from  "../models/Vehicle.js"

export const createVehicle = async (req, res) =>{
    try{
        const {
            vehicleType,
            plateNumber,
            make, 
            model, 
            year, 
            vin, 
            fuelType, 
            odometerReading, 
            currentLocation, 
            availability
        }  = req.body 
       

        const newVehicle = new Vehicle({ 
            vehicleType, 
            plateNumber, 
            make, 
            model, 
            year, 
            vin, 
            fuelType, 
            odometerReading, 
            currentLocation, 
            availability
        })
        await newVehicle.save()
        const vehicle = await Vehicle.find()
        res.status(201).json(vehicle)
    }catch(err){
        res.status(409).json({ message: err.message })
    }
}


/* read */ 
export const getVehicles = async (req, res)=>{
    try{
        const vehicles = await Vehicle.find();
        res.status(200).json(vehicles);
    }catch (err){
        res.status(404).json({ message: err.message})
    }
}




/* update */
export const updateVehicle = async (req, res) =>{ 
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

        const vehicles = await Vehicle.findById(id)
        const updatedVehicle= await Vehicle.findByIdAndUpdate(
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

        res.status(200).json(updatedVehicle)
    }catch (err){
        res.status(404).json({ message: err.message})
    }
}



/* delete */ 
export const deleteVehicle = async (req, res)=> {
    try{
        const {id } = req.params
        const  vehicle = Vehicle.find({ _id: id} )
        Vehicle.deleteOne( { _id: id } )
        
        res.status(200).json(vehicle)
    }catch (err){
        res.status(404).json( {message: err.message })
    }

}



