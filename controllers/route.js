import Route from "../models/Route.js"



export const createRoute = async (req, res) =>{
    try{
        const {
            startPoint,
            endPoint,
            estimateDistance, 
            estimateTime,  
        }  = req.body 
       
        const newRoute = new Route({ 
            startPoint,
            endPoint,
            estimateDistance, 
            estimateTime, 
        
        })
        await newRoute.save()
        const  fuel = await Route.find()
        res.status(201).json(fuel)
    }catch(err){
        res.status(409).json({ message: err.message })
    }
}


/* read */ 
export const getRoute  = async (req, res)=>{
    try{
        const route = await Route.find();
        res.status(200).json(route);
    }catch (err){
        res.status(404).json({ message: err.message})
    }
}



/* update */
export const updateRoute = async (req, res) =>{ 
    try{
        const { id }  = req.params
        const {
            startPoint,
            endPoint,
            estimatedDistance, 
            estimatedTime,  
        }  = req.body 
        
        const updatedRoute= await Post.findByIdAndUpdate(
            id, 
            {
                startPoint,
                endPoint,
                estimatedDistance, 
                estimatedTime, 
            }    
        )

        res.status(200).json(updatedRoute)
    }catch (err){
        res.status(404).json({ message: err.message})
    }
}


/* delete */ 
export const deleteRoute = async (req, res)=> {
    try{
        const {id } = req.params
        const  fuel = Route.find({ _id: id} )
        Route.deleteOne( { _id: id } )
        
        res.status(200).json(fuel)
    }catch (err){
        res.status(404).json( {message: err.message })
    }

}
