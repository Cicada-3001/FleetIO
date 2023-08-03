import Trip from "../models/Trip.js"

export const createTrip = async (req, res) =>{
    try{
        const {
            userId,
            vehicle,
            startTime, 
            endTime, 
        }  = req.body 
       
        const newTrip = new Trip({ 
            userId,
            vehicle,
            startTime, 
            endTime, 
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
        const { startDate }= req.params
        const { endDate }  = req.params

        const trips = await Trip.find({startTime:{$gte:startDate},$lte:endDate}).populate({
            path: 'vehicle',
            populate: 
             [ 
             'fuelType',
             'route',
             'driver'
             ]
          }).sort({"endTime": "desc"});



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
            userId,
            vehicle, 
            startTime, 
            endTime
        } = req.body 
        const updatedTrip= await Trip.findByIdAndUpdate(
            id, 
            {
                userId,
                vehicle,
                startTime, 
                endTime, 
            
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
export const deleteTrip = async (req, res)=> {
    try{
        const {id } = req.params
        Trip.findOneAndDelete({ _id: id },  (err, result)=> {
            if (err){
                res.status(200).json("Trip does not exist")
            }
            else{
                res.status(200).json(result)
            }
        });
    }catch (err){
        res.status(404).json( {message: err.message })
    }
}


