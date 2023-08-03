
import Maintenance from "../models/Maintance.js"
import Trip from "../models/Trip.js"
import Vehicle from "../models/Vehicles.js"
import Driver from "../models/Driver.js"
import Route from "../models/Route.js"
/* rank drivers */
export const rankDrivers = async (req, res) =>{ 

    try{
      
         const ranks = await Trip.aggregate([
        {
            $lookup: {
              from: 'vehicles',
              localField: 'vehicle',
              foreignField: '_id',
              as: 'vehicle'
            }
        },
        {
            $unwind: '$vehicle'
        },
        {
          $lookup: {
            from: 'routes',
            localField: 'vehicle.route',
            foreignField: '_id',
            as: 'vehicle.route'
          }
        },
        {
          $unwind: '$vehicle.route'
        },
        {
          $lookup: {
            from: 'drivers',
            localField: 'vehicle.driver',
            foreignField: '_id',
            as: 'vehicle.driver'
          }
        },
        {
          $unwind: '$vehicle.driver'
        },
        {
          $lookup: {
            from: 'fueltypes',
            localField: 'vehicle.fuelType',
            foreignField: '_id',
            as: 'vehicle.fuelType'
          }
        },
        {
          $unwind: '$vehicle.fuelType'
        },

        { 
          $group: {
            _id: '$vehicle.driver',
            trips: { $sum: 1 },
            totalSeats:{$sum: "$vehicle.seatCapacity" },
            totalRevenue: {$sum: { $multiply: ["$vehicle.route.estimateFareAmt", "$vehicle.seatCapacity"]}},
            totalWeightedRevenue:  { $sum: { $multiply: ["$vehicle.route.estimateFareAmt", "$vehicle.seatCapacity", "$route.estimateDistance"]}}
        }
        },
        {
          $project: {
             _id: 0,
            driver: '$_id',
            trips: '$trips',
            totalRevenue: '$totalRevenue',
            avgRevenuePerSeat: { $divide: ["$totalRevenue", "$totalSeats"]},
            avgWeightedRevenuePerSeat:  { $divide: ["$totalRevenue", "$totalSeats"]},
          }
        },

        {"$sort": { "avgWeightedRevenuePerSeat": -1 } }
      ]);
      res.status(200).json(ranks)
    }catch (err){
        res.status(404).json( {message: err.message })
    }   
}


/* rank routes */ 
export const rankRoutes = async (req, res) =>{ 
    try{
         const ranks = await Trip.aggregate([
        
          {"$group": {
            _id: '$vehicle.route',
            trips: { $sum: 1 } // this means that the count will increment by 1
          }}, 

          {"$sort": { "trips": -1 } }
      ]);
      res.status(200).json(ranks)
    }catch (err){
        res.status(404).json( {message: err.message })
    }   
}


/* rank vehicles */
export const rankVehicles = async (req, res) =>{ 
    try{
        const ranks = await Trip.aggregate([
        {
            $lookup: {
              from: 'vehicles',
              localField: 'vehicle',
              foreignField: '_id',
              as: 'vehicle'
            }
        },
        {
            $unwind: '$vehicle'
        },
        {
          $lookup: {
            from: 'routes',
            localField: 'vehicle.route',
            foreignField: '_id',
            as: 'vehicle.route'
          }
        },
        {
          $unwind: '$vehicle.route'
        },
        {
          $lookup: {
            from: 'drivers',
            localField: 'vehicle.driver',
            foreignField: '_id',
            as: 'vehicle.driver'
          }
        },
        {
          $unwind: '$vehicle.driver'
        },
        {
          $lookup: {
            from: 'fueltypes',
            localField: 'vehicle.fuelType',
            foreignField: '_id',
            as: 'vehicle.fuelType'
          }
        },
        {
          $unwind: '$vehicle.fuelType'
        },

        { 
            $group: {
              _id: '$vehicle',
              trips: { $sum: 1 },
              totalSeats:{$sum: "$vehicle.seatCapacity" },
              totalRevenue: {$sum: { $multiply: ["$vehicle.route.estimateFareAmt", "$vehicle.seatCapacity"]}},
              totalWeightedRevenue:  { $sum: { $multiply: ["$vehicle.route.estimateFareAmt", "$vehicle.seatCapacity", "$route.estimateDistance"]}}
          }
        },
        {
            $project: {
               _id: 0,
              driver: '$_id',
              trips: '$trips',
              totalRevenue: '$totalRevenue',
              avgRevenuePerSeat: { $divide: ["$totalRevenue", "$totalSeats"]},
              avgWeightedRevenuePerSeat:  { $divide: ["$totalWeightedRevenue", "$totalSeats"]}
    
            }
        },
  
        {"$sort": { "avgWeightedRevenuePerSeat": -1 } }
      ]);
      res.status(200).json(ranks)
    }catch (err){
        res.status(404).json( {message: err.message })
    }
}




export const getCounts  = async (req, res)=>{
    try{
     
     
      let date = new Date()
      let day = date.getDate();
      let month = date.getMonth() + 1;
      let year = date.getFullYear();
  
      // This arrangement can be altered based on how we want the date's format to appear.
      let todayDate = `${day}-${month}-${year}`;
      const needMaintenance = await Maintenance.countDocuments({maintained:true
        });

      const expiredLicenses = await  Driver.countDocuments({licenseExpiry:{$gt : todayDate}
        });

      const onRoute= await Vehicle.countDocuments({availability:"On Route"
        });

      const vehicles= await Vehicle.countDocuments(); 
      
      const drivers= await Driver.countDocuments();

      const trips= await Trip.countDocuments();  

      const routes= await Route.countDocuments();  

       const counts={
          needMaintenance:needMaintenance,
          expiredLicenses:expiredLicenses,
          onRoute:onRoute,
          vehicles:vehicles,
          drivers: drivers, 
          routes: routes,
          trips:trips
        }

        res.status(200).json(counts)
    }
  catch (err){
    console.log(err)
        res.status(404).json( {message: err.message })
  }
}



/* get  statistics vehicle wise for analytics */ 
export const vehicleStats = async (req, res)=> {
    try{
        let totalMaintenanceCost = 0 
        let totalFareRevenue = 0
        let totalFuelCost =  0 

        const { startDate }= req.params
        const { endDate }  = req.params

        const maintenanceCosts= await Maintenance.aggregate(
            [
              {
                $group: {
                  _id: "$vehicle",
                  totalMaintenanceCost: {
                    $sum: "$cost"
                  } 
                }
              }
        ]);
       /* const fuelFareCosts= await Trip.aggregate(
              [
                  {
                      $group: {
                          _id: "$vehicle",
                          totalFuelCost: {
                            $sum:  { $add: ["$vehicle.fuelType.price", { $multiply : ["$vehicle.route.estimateDistance", "$vehicle.fuelConsumptionRate"]}] }
                          },
                          totalFareRevenue: {
                          $sum:  "$vehicle.route.estimateFareAmt"
                        },

                      }
                  }
              ]); */
    
       const  vehicleStats= await Trip.aggregate([
        { $match:{ startTime: { $gte: startDate, $lte: endDate } } },
         {
           $lookup: {
             from: 'vehicles',
             localField: 'vehicle',
             foreignField: '_id',
             as: 'vehicle'
           }
         },
         {
           $unwind: '$vehicle'
         },
         {
           $lookup: {
             from: 'routes',
             localField: 'vehicle.route',
             foreignField: '_id',
             as: 'vehicle.route'
           }
         },
         {
           $unwind: '$vehicle.route'
         },
         {
           $lookup: {
             from: 'drivers',
             localField: 'vehicle.driver',
             foreignField: '_id',
             as: 'vehicle.driver'
           }
         },
         {
           $unwind: '$vehicle.driver'
         },
         {
           $lookup: {
             from: 'fueltypes',
             localField: 'vehicle.fuelType',
             foreignField: '_id',
             as: 'vehicle.fuelType'
           }
         },
         {
           $unwind: '$vehicle.fuelType'
         },
         {
           $group: {
              _id: '$vehicle',
              totalFuelCost: {
                $sum:  { $add: ["$vehicle.fuelType.price", { $multiply : ["$vehicle.route.estimateDistance", "$vehicle.fuelConsumptionRate"]}] }
              },
      
              totalSeats:{$sum: "$vehicle.seatCapacity" },
              trips: { $sum: 1 },
            totalTime: { $sum: "$vehicle.route.estimateTime"},
              totalDistance: {$sum: "$vehicle.route.estimateDistance"},
              totalRevenue: {$sum: { $multiply: ["$vehicle.route.estimateFareAmt", "$vehicle.seatCapacity"]}},
              totalWeightedRevenue:  { $sum: { $multiply: ["$vehicle.route.estimateFareAmt", "$vehicle.seatCapacity", "$vehicle.route.estimateDistance"]}}
          }
          },
          {
            $project: {
               _id: 0,
              vehicle: '$_id',
              trips: '$trips',
              totalFuelCost: '$totalFuelCost',
              totalFareRevenue: '$totalRevenue',             
              grossRevenue: { $sum: { $subtract: ['$totalRevenue','$totalFuelCost',] } },
              totalWeightedRevenue: '$totalWeightedRevenue',
              totalDistance: '$totalDistance',
              totalTime: '$totalTime',
              avgRevenuePerSeat: { $divide: ["$totalRevenue", "$totalSeats"]},
              avgWeightedRevenuePerSeat:  { $divide: ["$totalWeightedRevenue", "$totalSeats"]},
            }
          },
          {"$sort": { "avgWeightedRevenuePerSeat": -1 } }
       ])
       

      /*
       const costs = {
          maintenanceCosts: maintenanceCosts,
          fuelFareCosts: fuelFareCosts,
        }
      */
        res.status(200).json(vehicleStats) 

    }catch (err){
        res.status(404).json( {message: err.message })
    }
}





/* calculate revenue */ 
export const driverStats = async (req, res)=> {
  try{
      let totalMaintenanceCost = 0 
      let totalFareRevenue = 0
      let totalFuelCost =  0 


      
      const { startDate }= req.params
      const { endDate }  = req.params


      const maintenanceCosts= await Maintenance.aggregate(
          [
            {
              $group: {
                _id: "$vehicle",
                totalMaintenanceCost: {
                  $sum: "$cost"
                } 
              }
            }
      ]);
     /* const fuelFareCosts= await Trip.aggregate(
            [
                {
                    $group: {
                        _id: "$vehicle",
                        totalFuelCost: {
                          $sum:  { $add: ["$vehicle.fuelType.price", { $multiply : ["$vehicle.route.estimateDistance", "$vehicle.fuelConsumptionRate"]}] }
                        },
                        totalFareRevenue: {
                        $sum:  "$vehicle.route.estimateFareAmt"
                      },

                    }
                }
            ]); */
  

     const  driverStats= await Trip.aggregate([
      { $match:{ startTime: { $gte: startDate, $lte: endDate } } },
       {
         $lookup: {
           from: 'vehicles',
           localField: 'vehicle',
           foreignField: '_id',
           as: 'vehicle'
         }
       },
       {
         $unwind: '$vehicle'
       },
       {
         $lookup: {
           from: 'routes',
           localField: 'vehicle.route',
           foreignField: '_id',
           as: 'vehicle.route'
         }
       },
       {
         $unwind: '$vehicle.route'
       },
       {
         $lookup: {
           from: 'drivers',
           localField: 'vehicle.driver',
           foreignField: '_id',
           as: 'vehicle.driver'
         }
       },
       {
         $unwind: '$vehicle.driver'
       },
       {
         $lookup: {
           from: 'fueltypes',
           localField: 'vehicle.fuelType',
           foreignField: '_id',
           as: 'vehicle.fuelType'
         }
       },
       {
         $unwind: '$vehicle.fuelType'
       },
       {
         $group: {
            _id: '$vehicle.driver',
            totalFuelCost: {
              $sum:  { $add: ["$vehicle.fuelType.price", { $multiply : ["$vehicle.route.estimateDistance", "$vehicle.fuelConsumptionRate"]}] }
            },
    
            totalSeats:{$sum: "$vehicle.seatCapacity" },
            trips: { $sum: 1 },
          totalTime: { $sum: "$vehicle.route.estimateTime"},
            totalDistance: {$sum: "$vehicle.route.estimateDistance"},
            totalRevenue: {$sum: { $multiply: ["$vehicle.route.estimateFareAmt", "$vehicle.seatCapacity"]}},
            totalWeightedRevenue:  { $sum: { $multiply: ["$vehicle.route.estimateFareAmt", "$vehicle.seatCapacity", "$vehicle.route.estimateDistance"]}}
        }
        },
        {
          $project: {
             _id: 0,
            driver: '$_id',   
            trips: '$trips',
            totalFuelCost: '$totalFuelCost',
            totalFareRevenue: '$totalRevenue',             
            grossRevenue: { $sum: { $subtract: ['$totalRevenue','$totalFuelCost',] } },
            totalWeightedRevenue: '$totalWeightedRevenue',
            totalDistance: '$totalDistance',
            totalTime: '$totalTime',
            avgRevenuePerSeat: { $divide: ["$totalRevenue", "$totalSeats"]},
            avgWeightedRevenuePerSeat:  { $divide: ["$totalWeightedRevenue", "$totalSeats"]}
          }
        },
        {"$sort": { "avgWeightedRevenuePerSeat": -1 } }
     ])
     

    /*
     const costs = {
        maintenanceCosts: maintenanceCosts,
        fuelFareCosts: fuelFareCosts,
      }
    */
      res.status(200).json(driverStats) 

  }catch (err){
      res.status(404).json( {message: err.message })
  }
}




/* calculate revenue */ 
export const routeStats = async (req, res)=> {
  try{
      let totalMaintenanceCost = 0 
      let totalFareRevenue = 0
      let totalFuelCost =  0 
      
      const { startDate }= req.params
      const { endDate }  = req.params


      const maintenanceCosts= await Maintenance.aggregate(
          [
            {
              $group: {
                _id: "$vehicle",
                totalMaintenanceCost: {
                  $sum: "$cost"
                } 
              }
            }
      ]);
     /* const fuelFareCosts= await Trip.aggregate(
            [
                {
                    $group: {
                        _id: "$vehicle",
                        totalFuelCost: {
                          $sum:  { $add: ["$vehicle.fuelType.price", { $multiply : ["$vehicle.route.estimateDistance", "$vehicle.fuelConsumptionRate"]}] }
                        },
                        totalFareRevenue: {
                        $sum:  "$vehicle.route.estimateFareAmt"
                      },

                    }
                }
            ]); */
  

     const  vehicleStats= await Trip.aggregate([
      { $match:{ startTime: { $gte: startDate, $lte: endDate } } },
       {
         $lookup: {
           from: 'vehicles',
           localField: 'vehicle',
           foreignField: '_id',
           as: 'vehicle'
         }
       },
       {
         $unwind: '$vehicle'
       },
       {
         $lookup: {
           from: 'routes',
           localField: 'vehicle.route',
           foreignField: '_id',
           as: 'vehicle.route'
         }
       },
       {
         $unwind: '$vehicle.route'
       },
       {
         $lookup: {
           from: 'drivers',
           localField: 'vehicle.driver',
           foreignField: '_id',
           as: 'vehicle.driver'
         }
       },
       {
         $unwind: '$vehicle.driver'
       },
       {
         $lookup: {
           from: 'fueltypes',
           localField: 'vehicle.fuelType',
           foreignField: '_id',
           as: 'vehicle.fuelType'
         }
       },
       {
         $unwind: '$vehicle.fuelType'
       },
       {
         $group: {
            _id: '$vehicle.route',
            totalFuelCost: {
              $sum:  { $add: ["$vehicle.fuelType.price", { $multiply : ["$vehicle.route.estimateDistance", "$vehicle.fuelConsumptionRate"]}] }
            },
    
            totalSeats:{$sum: "$vehicle.seatCapacity" },
            trips: { $sum: 1 },
            totalTime: { $sum: "$vehicle.route.estimateTime"},
            totalDistance: {$sum: "$vehicle.route.estimateDistance"},
            totalRevenue: {$sum: { $multiply: ["$vehicle.route.estimateFareAmt", "$vehicle.seatCapacity"]}},
            totalWeightedRevenue:  { $sum: { $multiply: ["$vehicle.route.estimateFareAmt", "$vehicle.seatCapacity", "$vehicle.route.estimateDistance"]}}
        }
        },
        {
          $project: {
             _id: 0,
            route: '$_id',
            trips: '$trips',
            totalFuelCost: '$totalFuelCost',
            totalFareRevenue: '$totalRevenue',             
            grossRevenue: { $sum: { $subtract: ['$totalRevenue','$totalFuelCost',] } },
            totalWeightedRevenue: '$totalWeightedRevenue',
            totalDistance: '$totalDistance',
            totalTime: '$totalTime',
            avgRevenuePerSeat: { $divide: ["$totalRevenue", "$totalSeats"]},
            avgWeightedRevenuePerSeat:  { $divide: ["$totalWeightedRevenue", "$totalSeats"]}
          }
        },
        {"$sort": { "avgWeightedRevenuePerSeat": -1 } }
     ])
     

    /*
     const costs = {
        maintenanceCosts: maintenanceCosts,
        fuelFareCosts: fuelFareCosts,
      }
    */
      res.status(200).json(vehicleStats) 

  }catch (err){
      res.status(404).json( {message: err.message })
  }
}


