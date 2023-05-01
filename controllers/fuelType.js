import FuelType from "../models/FuelType.js"


export const creatFuelType = async (req, res) =>{
    try{
        const {
            userId,
            name,
            price
        }  = req.body
       
        const newFuelType = new FuelType({ 
            userId,
            name, 
            price
        })
        await newFuelType.save()
        const fuelType = await FuelType.find()
        res.status(201).json(fuelType)
    }catch(err){
        res.status(409).json({ message: err.message })
    }
}

/* read */ 
export const getFuelTypes = async (req, res)=>{
    try{
        const fuelTypes = await FuelType.find();
        res.status(200).json(fuelTypes);
    }catch (err){
        res.status(404).json({ message: err.message})
    }
}

/* update */
export const updateFuelType = async (req, res) =>{ 
    try{
        const { id }  = req.params
        const {
            userId,
            name,
            price, 
        }  = req.body 
        const fuelType = await FuelType.findById(id)
        const updatedFuelType= await FuelType.findByIdAndUpdate(
            id, 
            {
                userId, 
                name, 
                price, 
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
export const deleteFuelType = async (req, res)=> {
    try{
        const {id } = req.params
        FuelType.findOneAndDelete({ _id: id }, function (err, result) {
            if (err){
                res.status(200).json(" Fuel Type does not exist")
            }
            else{
                res.status(200).json(result)
            }
        });
    }catch (err){
        res.status(404).json( {message: err.message })
    }

}



