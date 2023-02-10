import User from  "../models/User.js"


/* delete */

export const deleteUser = async (req, res)=> {
    try{
        const {id } = req.params
        const  user = User.find({ _id: id} )
        User.deleteOne( { _id: id } )
        
        res.status(200).json(user)
    }catch (err){
        res.status(404).json( {message: err.message })
    }

}