import Vehicle from "../models/Vehicles.js"

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
            availability,
            driver, 
            route, 
            fuelConsumptionRate
        }  = req.body 

        console.log(req.body)

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
            availability, 
            driver, 
            route, 
            fuelConsumptionRate
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
            vehicleType,
            plateNumber,
            make, 
            model, 
            year, 
            vin, 
            fuelType, 
            odometerReading, 
            currentLocation, 
            availability,
            driver, 
            route, 
            fuelConsumptionRate
        }  = req.body 

        const updatedVehicle= await Vehicle.findByIdAndUpdate(
            id, 
            {
                vehicleType,
                plateNumber,
                make, 
                model, 
                year, 
                vin, 
                fuelType, 
                odometerReading, 
                currentLocation, 
                availability,
                driver, 
                route, 
                fuelConsumptionRate
             
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
        const { id } = req.params
        console.log(id)
    
        Vehicle.findOneAndDelete({ _id: id }, function (err, result) {
            if (err){
                res.status(200).json("Vehicle does not exist")
            }
            else{
                res.status(200).json(result)
            }
        });
    }catch (err){
        res.status(404).json( {message: err.message })
    }
}



/* assign route */ 
export const assignRoute = async (req, res)=> {
    try{
        const { id } = req.params
        const { route } = req.body
        Vehicle.findByIdAndUpdate(id, { route: route },
        function (err, result) {
        if (err)
            res.status(200).json("Cannot assign route, an error occured")
        else
        res.status(200).json(result)
    });
    }catch (err){
        res.status(404).json( {message: err.message })
    }
}



/* assign Driver */ 
export const assignDriver = async (req, res)=> {
    try{
        const {id } = req.params
        const { driver } = req.body
        Vehicle.findByIdAndUpdate(id, { driver: driver },
        function (err, result) {
        if (err)
            res.status(200).json("Cannot assign driver, an error occured")
        else
        res.status(200).json(result)
    });
    }catch (err){
        res.status(404).json( {message: err.message })
    }
}



/* get current location */ 
export const updateCurrentLocation = async (req, res)=> {
    try{
        const {id } = req.params
        const  vehicle = Vehicle.find({ _id: id})
        res.status(200).json(vehicle)
    }catch (err){
        res.status(404).json( {message: err.message })
    }
}





/* mark the gefenced area */ 
export const addGeofencedArea = async (req, res)=> {
    try{
        const {id } = req.params
        const  vehicle = Vehicle.find({ _id: id} )
        Vehicle.deleteOne( { _id: id } )
        
        res.status(200).json(vehicle)
    }catch (err){
        res.status(404).json( {message: err.message })
    }
}


/*   check out of geofenced */ 
export const  alertGeofenceError = async (req, res)=> {
    try{
        const {id } = req.params
        const  vehicle = Vehicle.find({ _id: id} )
        Vehicle.deleteOne( { _id: id } )
        
        res.status(200).json(vehicle)
    }catch (err){
        res.status(404).json( {message: err.message })
    }
}











