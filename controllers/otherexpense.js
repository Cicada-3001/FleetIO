import OtherExpense from "../models/OtherExpense.js"

export const createOtherExpense = async (req, res) =>{
    try{
        const {
            userId,
            description,
            amount,
            created,
        }  = req.body 

        const newOtherExpense = new OtherExpense({ 
            userId,
            description, 
            amount, 
            created
        })
        await newOtherExpense.save()
        const otherExpenses = await OtherExpense.find()
        res.status(201).json(otherExpenses)
    }catch(err){
        res.status(409).json({ message: err.message })
    }
}


/* read */ 
export const getOtherExpense = async (req, res)=>{
    try{
        const otherExpenses = await OtherExpense.find()
        res.status(200).json(otherExpenses);
    }catch (err){
        res.status(404).json({ message: err.message})
    }
}



/* update */
export const updateOtherExpense = async (req, res) =>{ 
    try{
        const { id }  = req.params
        const {
            userId,
            description,
            amount,
            created, 
        }  = req.body 
        const updatedMaintenance= OtherExpense.findByIdAndUpdate(
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
export const deleteOtherExpense = async (req, res)=> {
    try{
        const {id } = req.params
        OtherExpense.findOneAndDelete({ _id: id }, function (err, result) {
            if (err){
                res.status(200).json("Other expense does not exist")
            }
            else{
                res.status(200).json(result)
            }
        });
    }catch (err){
        res.status(404).json( {message: err.message })
    }
}

