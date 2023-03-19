import Trip from "../models/Trip.js"

export const createTrip = async (req, res) =>{
    try{
        const {
            vehicle,
            driver,
            startTime, 
            endTime, 
            route
        }  = req.body 
       
        const newTrip = new Trip({ 
            vehicle,
            driver,
            startTime, 
            endTime, 
            route
        })

        await newTrip.save()
        const trip = await Trip.find()
        res.status(201).json(trip)
    }catch(err){
        res.status(409).json({ message: err.message })
    }
}




/* read */ 
export const getTrips = async (req, res)=>{
    try{
        const trips = await Trip.find();
        res.status(200).json(trips);
    }catch (err){
        res.status(404).json({ message: err.message})
    }
}



/* update */
export const updateTrip = async (req, res) =>{ 
    try{
        const { id }  = req.params
        const {
            vehicle,
            driver,
            startTime, 
            endTime, 
            route
        } = req.body 

        const trip = await Trip.findById(id)
        const updatedTrip= await Post.findByIdAndUpdate(
            id, 
            {
                vehicle,
                driver,
                startTime, 
                endTime, 
                route
            }
        )

        res.status(200).json(updatedTrip)
    }catch (err){
        res.status(404).json({ message: err.message})
    }
}


/* delete */ 
export const deleteTrip = async (req, res)=> {
    try{
        const {id } = req.params
        Trip.findOneAndDelete({ _id: id }, function (err, result) {
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


