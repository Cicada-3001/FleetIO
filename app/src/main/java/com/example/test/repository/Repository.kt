package com.example.test.repository


import com.example.retrofittutorial.api.RetrofitInstance
import com.example.test.models.*
import retrofit2.Response
import retrofit2.http.GET

class Repository {

    /* get methods */
    suspend fun getVehicles(): Response<List<Vehicle>> {
       return RetrofitInstance.api.getVehicles()
    }

    suspend fun getDrivers(): Response<List<Driver>> {
        return RetrofitInstance.api.getDrivers()
    }

    suspend fun getFuels(): Response<List<Fuel>> {
        return RetrofitInstance.api.getFuels()
    }

    suspend fun getMaintenances(): Response<List<Maintenance>>{
        return RetrofitInstance.api.getMaintenances()
    }

    suspend fun getTrips(startDt: String, endDt:String): Response<List<Trip>>{
        return RetrofitInstance.api.getTrips(startDt, endDt)
    }

    suspend fun getFuelTypes(): Response<List<FuelType>>{
        return RetrofitInstance.api.getFuelTypes()
    }

     suspend fun getRoutes(): Response<List<Route>>{
         return RetrofitInstance.api.getRoutes()
     }

   /* suspend fun getVehicleRanks(): Response<List<VehicleStat>> {
        return RetrofitInstance.api.getVehicleRanks()
    }*/

    suspend fun getCounts(): Response<Counts>{
        return RetrofitInstance.api.getCounts()
    }

    suspend fun getOtherExpenses(): Response<List<OtherExpense>>{
        return RetrofitInstance.api.getOtherExpense()
    }

    suspend fun getOtherRevenue(): Response<List<OtherRevenue>>{
        return RetrofitInstance.api.getOtherRevenue()
    }


    suspend fun getDriverRanks(): Response<List<DriverStat>>{
        return RetrofitInstance.api.getDriverRanks()
    }

    suspend fun getRoutesRank(): Response<List<RouteStat>>{
        return RetrofitInstance.api.getRouteRanks()
    }

    suspend fun getVehicleStats(startDate:String,endDate:String): Response<List<VehicleStat>>{
        return RetrofitInstance.api.getVehicleStats(startDate, endDate)
    }


    suspend fun getRouteStats(startDate:String, endDate:String): Response<List<RouteStat>>{
        return RetrofitInstance.api.getRouteStats(startDate, endDate)
    }


    suspend fun getDriverStats(startDate:String, endDate:String): Response<List<StatDriver>>{
        return RetrofitInstance.api.getDriverStats(startDate, endDate)
    }





    /* post methods */
    suspend fun addVehicle(vehicle: VehicleAdd):  Response<List<Vehicle>>{
        return RetrofitInstance.api.addVehicle(vehicle)
    }

    suspend fun addDriver(driver: DriverAdd):  Response<List<Driver>>{
        return RetrofitInstance.api.addDriver(driver)
    }

    suspend fun addFuel(fuel: Fuel):  Response<List<Fuel>> {
        return RetrofitInstance.api.addFuel(fuel)
    }

    suspend fun addMaintenance(maintenance: MaintenanceAdd):  Response<List<Maintenance>>{
        return RetrofitInstance.api.addMaintenance(maintenance)
    }

    suspend fun addTrip(trip: TripAdd):  Response<List<TripAdd>> {
        return RetrofitInstance.api.addTrip(trip)
    }

    suspend fun addRoute(route: Route):  Response<List<Route>> {
        return RetrofitInstance.api.addRoute(route)
    }

    suspend fun registerUser(user: User):  Response<User> {
        return RetrofitInstance.api.registerUser(user)
    }

    suspend fun loginUser(auth: Auth):  Response<User> {
        return RetrofitInstance.api.loginUser(auth)
    }

    suspend fun addOtherExpense(expense: OtherExpense):  Response<List<OtherExpense>> {
        return RetrofitInstance.api.addOtherExpense(expense)
    }

    suspend fun addOtherRevenue(revenue: OtherRevenue):  Response<List<OtherRevenue>> {
        return RetrofitInstance.api.addOtherRevenue(revenue)
    }



    /* patch methods */
    suspend fun updateAccount(userId: String, user: User):  Response<User> {
        return RetrofitInstance.api.updateAccount(userId,user)
    }

    suspend fun assignVehicle(driverId: String, vehicleId: String ):  Response<DriverAdd> {
        return RetrofitInstance.api.assignVehicle(driverId, vehicleId)
    }

    suspend fun assignDriver(vehicleId: String, driverId: String ):  Response<VehicleAdd> {
        return RetrofitInstance.api.assignDriver(vehicleId, driverId)
    }
    suspend fun markMaintenance(maintenanceId: String):  Response<MaintenanceAdd> {
        return RetrofitInstance.api.markMaintenance(maintenanceId)
    }


    suspend fun markGeofence(vehicleId: String, operationArea:String, geofenceRadius:Double):  Response<VehicleAdd>{
        return RetrofitInstance.api.markGeofence(vehicleId, operationArea, geofenceRadius)
    }

    suspend fun updateVehicle(vehicleId: String, vehicle:VehicleAdd):  Response<VehicleAdd> {
        return RetrofitInstance.api.updateVehicle(vehicleId,vehicle)
    }

    suspend fun updateDriver(driverId: String, driver: DriverAdd):  Response<DriverAdd> {
        return RetrofitInstance.api.updateDriver(driverId, driver)
    }

    suspend fun updateMaintenance(maintenanceId: String, maintenance: MaintenanceAdd):  Response<MaintenanceAdd> {
        return RetrofitInstance.api.updateMaintenance(maintenanceId,maintenance)
    }

    suspend fun updateRoute(routeId: String, route:Route):  Response<Route> {
        return RetrofitInstance.api.updateRoute(routeId, route)
    }


    /* delete methods */
    suspend fun deleteAccount(userId: String):  Response<User> {
        return RetrofitInstance.api.deleteAccount(userId)
    }

    suspend fun deleteVehicle(vehicleId: String ):  Response<VehicleAdd> {
        return RetrofitInstance.api.deleteVehicle(vehicleId)
    }

    suspend fun deleteMaintenance(maintenanceId: String):  Response<MaintenanceAdd> {
        return RetrofitInstance.api.deleteMaintenance(maintenanceId)
    }

    suspend fun deleteDriver(driverId: String):  Response<DriverAdd> {
        return RetrofitInstance.api.deleteDriver(driverId)
    }

    suspend fun deleteTrip(tripId: String):  Response<TripAdd> {
        return RetrofitInstance.api.deleteTrip(tripId)
    }

    suspend fun deleteRoute(routeId: String):  Response<Route> {
        return RetrofitInstance.api.deleteRoute(routeId)
    }

}