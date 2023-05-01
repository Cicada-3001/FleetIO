import Fuel from "../models/Fuel.js"

export const createFuel = async (req, res) =>{
    try{
        const {
            userId,
            vehicle,
            fuelType,
            location, 
            cost, 
            gallons, 
        }  = req.body 
       
        const newFuel = new Fuel({ 
            userId,
            vehicle,
            fuelType,
            location, 
            cost, 
            gallons,  
        })
        await newFuel.save()
        const  fuel = await Fuel.find()
        res.status(201).json(fuel)
    }catch(err){
        res.status(409).json({ message: err.message })
    }
}


/* read */ 
export const getFuel = async (req, res)=>{
    try{
        const fuel = await Fuel.find();
        res.status(200).json(fuel);
    }catch (err){
        res.status(404).json({ message: err.message})
    }
}



/* update */
export const updateFuel = async (req, res) =>{ 
    try{
        const { id }  = req.params
        const {
            vehicle,
            fuelType,
            location, 
            cost, 
            gallons, 
        }  = req.body 
        
        const updatedFuel= await Post.findByIdAndUpdate(
            id, 
            {
                vehicle,
                fuelType,
                location, 
                cost, 
                gallons, 
            }    
        )

        res.status(200).json(updatedFuel)
    }catch (err){
        res.status(404).json({ message: err.message})
    }
}


/* delete */ 
export const deleteFuel = async (req, res)=> {
    try{
        const {id } = req.params
        const  fuel = Fuel.find({ _id: id} )
        Fuel.deleteOne( { _id: id } )
        
        res.status(200).json(fuel)
    }catch (err){
        res.status(404).json( {message: err.message })
    }

}
