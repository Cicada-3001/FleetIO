import OtherRevenue from "../models/OtherRevenue.js"


export const createOtherRevenue = async (req, res) =>{
    try{
        const {
            userId,
            description,
            amount,
            created,
        }  = req.body 

        const newOtherRevenue = new OtherRevenue({ 
            userId,
            description, 
            amount, 
            created
        })
        await newOtherRevenue.save()
        const otherRevenue = await OtherRevenue.find()
        res.status(201).json(otherExpenses)
    }catch(err){
        res.status(409).json({ message: err.message })
    }
}


/* read */ 
export const getOtherRevenue = async (req, res)=>{
    try{
        const otherRevenue = await OtherRevenue.find()
        res.status(200).json(otherRevenue);
    }catch (err){
        res.status(404).json({ message: err.message})
    }
}



/* update */
export const updateOtherRevenue = async (req, res) =>{ 
    try{
        const { id }  = req.params
        const {
            userId,
            description,
            amount,
            created, 
        }  = req.body 
        const updatedMaintenance= OtherRevenue.findByIdAndUpdate(
            id, 
        {
            userId,
            description, 
            amount, 
            created, 
            
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
export const deleteOtherRevenue = async (req, res)=> {
    try{
        const {id } = req.params
        OtherRevenue.findOneAndDelete({ _id: id }, function (err, result) {
            if (err){
                res.status(200).json("Revenue does not exist")
            }
            else{
                res.status(200).json(result)
            }
        });
    }catch (err){
        res.status(404).json( {message: err.message })
    }
}