import User from "../models/User.js"




/* get users */
export const getUsers = async (req, res)=>{
    try{
        const users = await User.find();
        res.status(200).json(users);
    }catch (err){
        res.status(404).json({ message: err.message})
    }
}





/* update */
export const updateUser = async (req, res) =>{ 
    try{
        const { id }  = req.params
        const {
           fullname, 
           phone, 
           email, 
           password, 
        }  = req.body 

        const updatedVehicle= await User.findByIdAndUpdate(
            id, 
            {
                fullname,
                phone,
                email, 
                password, 
            }  
        )

        res.status(200).json(updatedUser)
    }catch (err){
        res.status(404).json({ message: err.message})
    }
}



/* delete */ 
export const deleteUser = async (req, res)=> {
    try{
        const { id } = req.params
        console.log(id)
    
        User.findOneAndDelete({ _id: id }, function (err, result) {
            if (err){
                res.status(200).json("User does not exist")
            }
            else{
                res.status(200).json(result)
            }
        });
    }catch (err){
        res.status(404).json( {message: err.message })
    }
}


















