import Driver from "../models/Driver.js"
import Vehicle from "../models/Vehicles.js"

export const createDriver = async (req, res) =>{
    try{
        const {
            userId,
            firstName,
            lastName,
            licenseNumber, 
            licenseExpiry,
            dateOfBirth, 
            phoneNumber, 
            email, 
            imageUrl,
            vehicle
        }  = req.body
       

        const newDriver = new Driver({ 
            userId,
            firstName, 
            lastName, 
            licenseNumber, 
            licenseExpiry,
            dateOfBirth, 
            phoneNumber, 
            email, 
            imageUrl,
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
        const drivers = await Driver.find().populate('vehicle')
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
            licenseExpiry,
            dateOfBirth, 
            phoneNumber, 
            email, 
            vehicle,
            imageUrl
        }  = req.body 
      
        const updatedDriver=  Driver.findByIdAndUpdate(
            id, 
            {
                firstName, 
                lastName, 
                licenseNumber, 
                licenseExpiry,
                dateOfBirth, 
                phoneNumber, 
                email, 
                vehicle,
                imageUrl 
            },
            function (err, result) {
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
export const deleteDriver = async (req, res)=> {
    try{
        const {id } = req.params
        Driver.findOneAndDelete({ _id: id }, function (err, result) {
            if (err){
                res.status(200).json("Driver does not exist")
            }
            else{
                res.status(200).json(result)
            }
        });
    }catch (err){
        res.status(404).json( {message: err.message })
    }

}



/* assign vehicle to a driver */ 
export const assignVehicle = async (req, res)=> {
    try{
        const { id } = req.params
        const { vehicle } = req.params

        const updatedVehicle= await Vehicle.findByIdAndUpdate(
            vehicle, 
            {
                driver: id
            },
          )

        const updatedDriver= await Driver.findByIdAndUpdate(
            id, 
            {
                vehicle
            }
           );  
        
        res.status(200).json(updatedDriver)

    }catch (err){
        res.status(404).json( {message: err.message })
    }

}











//Note 
//Assign a vehicle to a driver will be merely changing the vehicle attribute of the driver 
//fetching  the  interconnection of the driver and vehicle. it need to be a json data type 
// to avoid computation isssue
//when the  driver is assigned the vehicle, the vehicle that is assigned is passed as a json object to the attribute
// a similar case for other entities 
// 

// a background service to continuosly update the location of a vehicle 
// two function, get the location of the vehicle(arduino) at 2 seconds interval, 
//update the location of the vehicle(arduino interacting with the database)
// a background thread  with a  view model at the front end to detect the changes and render them on the map 
// simulate car movement from a previous location to the recent location 
            //previous location = x 
            //moveCar(x, y(new_location))
            //x=y


//In operation attribute should be set to true or  false as defined by time of operation. 
        //Defining of the time the vehicle needs to operate 


// a car is mapped to a route. 

// creating of a function that after every two seconds it will be updating the location of the car with a new element of an array 
// get trip has three objects that are returned, the vehicle has nested route info and driver info. When a trip is completed we are able to check against these attribute
// trip info has three attributes, the vehicle, the route, the driver, the start time, the end time, the start location, the end location. 