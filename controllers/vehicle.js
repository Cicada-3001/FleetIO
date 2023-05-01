import Vehicle from "../models/Vehicles.js"

export const createVehicle = async (req, res) =>{
    try{
        const {
            userId,
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
            fuelConsumptionRate, 
            imageUrl, 
            seatCapacity
        }  = req.body 

        console.log(req.body)

        const newVehicle = new Vehicle({ 
            userId,
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
            fuelConsumptionRate,
            imageUrl, 
            seatCapacity
        })
        await newVehicle.save()
        const vehicle = await Vehicle.find().populate(
            [ 
            'driver',   
            'route',
            'fuelType'
            ]
            );
        res.status(201).json(vehicle)
    }catch(err){
        res.status(409).json({ message: err.message })
    }
}


/* read */ 
export const getVehicles = async (req, res)=>{
    try{
        const vehicles = await Vehicle.find().populate(
            [ 
            'driver',   
            'route',
            'fuelType'
            ]
            );
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
            fuelConsumptionRate,
            imageUrl, 
            seatCapacity
        }  = req.body 

        const updatedVehicle= Vehicle.findByIdAndUpdate(
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
                fuelConsumptionRate, 
                imageUrl, 
                seatCapacity
            } , 
            function (err, result){
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






/* mark geofence */
export const markGeofence = async (req, res) =>{ 
    try{
        const { id }  = req.params
        const {
            operationArea, 
            geofenceRadius
        }  = req.body 

        const updatedVehicle= Vehicle.findByIdAndUpdate(
            id, 
            {
                operationArea, 
                geofenceRadius
            } , 
            function (err, result){
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



/* mark availability */ 
export const markAvailability = async (req, res)=> {
    try{
        const { id } = req.params
        const { availability } = req.body
        Vehicle.findByIdAndUpdate(id, { availability: availability },
        function (err, result) {
        if (err)
            res.status(200).json("Cannot update availability, an error occured")
        else
        res.status(200).json(result)
    });
    }catch (err){
        res.status(404).json( {message: err.message })
    }
}



/* mark availability */ 
export const markMaintenance = async (req, res)=> {
    try{
        const { id } = req.params
        const { maintained } = req.body
        Vehicle.findByIdAndUpdate(id, { maintained: maintained },
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








