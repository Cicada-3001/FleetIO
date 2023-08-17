package com.example.retrofittutorial.api

import com.example.test.models.*
import retrofit2.Response
import retrofit2.http.*


interface SimpleApi {
    /* Get requests */
    @GET("api/v1/vehicles")
    suspend fun getVehicles(): Response<List<Vehicle>>


    @GET("api/v1/vehicles/byroute/{route}")
    suspend fun getVehiclesByRoute(
        @Path("route") vin:String
    ): Response<List<Vehicle>>


    @GET("api/v1/drivers")
    suspend fun getDrivers(): Response<List<Driver>>

    @GET("api/v1/fuels")
    suspend fun getFuels(): Response<List<Fuel>>

    @GET("api/v1/maintenances")
    suspend fun getMaintenances(): Response<List<Maintenance>>

    @GET("api/v1/trips/{startDate}/{endDate}")
    suspend fun getTrips(
        @Path("startDate") startDate:String,
        @Path("endDate") endDate:String
    ): Response<List<Trip>>

    @GET("api/v1/routes")
    suspend fun getRoutes(): Response<List<Route>>

    @GET("api/v1/analytics/rank/driver")
    suspend fun getDriverRanks(): Response<List<DriverStat>>

    @GET("api/v1/analytics/rank/route")
    suspend fun getRouteRanks(): Response<List<RouteStat>>

    @GET("api/v1/analytics/vehiclestats/{startDate}/{endDate}")
    suspend fun getVehicleStats(
        @Path("startDate") startDate:String,
        @Path("endDate") endDate:String
    ): Response<List<VehicleStat>>

    @GET("api/v1/analytics/driverstats/{startDate}/{endDate}")
    suspend fun getDriverStats(
        @Path("startDate") startDate:String,
        @Path("endDate") endDate:String
    ): Response<List<StatDriver>>


    @GET("api/v1/analytics/routestats/{startDate}/{endDate}")
    suspend fun getRouteStats(
        @Path("startDate") startDate:String,
        @Path("endDate") endDate:String
    ): Response<List<RouteStat>>


    @GET("api/v1/fuelTypes")
    suspend fun getFuelTypes(): Response<List<FuelType>>

    @GET("api/v1/analytics/counts")
    suspend fun getCounts(): Response<Counts>

    @GET("api/v1/otherexpenses")
    suspend fun getOtherExpense(): Response<List<OtherExpense>>

    @GET("api/v1/otherrevenues")
    suspend fun getOtherRevenue(): Response<List<OtherRevenue>>


    /* Get with request param */
    @GET("api/v1/vehicles/{userId}")
    suspend fun  getUserVehicles(
        @Path("userId") number:Int
    ):Response<Vehicle>

    @GET("api/v1/drivers/{userId}")
    suspend fun  getUserDrivers(
        @Path("userId") number:Int
    ):Response<Driver>

    @GET("api/v1/fuels/{userId}")
    suspend fun  getUserFuels(
        @Path("userId") number:Int
    ):Response<Fuel>

    @GET("api/v1/maintenances/{userId}")
    suspend fun  getUserMaintenances(
        @Path("userId") number:Int
    ):Response<Maintenance>

    @GET("api/v1/vehicle/fuels/{vehicleId}")
    suspend fun  getVehicleFuels(
        @Path("vehicleId") number:Int
    ):Response<Fuel>

    @GET("api/v1/vehicle/maintenances/{vehicleId}")
    suspend fun  getVehicleMaintenances(
        @Path("vehicle") number:Int
    ):Response<Maintenance>

    @GET("api/v1/trips/{userId}")
    suspend fun  getUserTrips(
        @Path("userId") number:Int
    ):Response<Trip>


    /*  Post requests */
    @POST("api/v1/vehicles")
    suspend fun addVehicle(
        @Body post: VehicleAdd
    ): Response<List<Vehicle>>

    @POST("api/v1/drivers")
    suspend fun addDriver(
        @Body post: DriverAdd
    ): Response<List<Driver>>


    @POST("api/v1/fuels")
    suspend fun addFuel(
        @Body post: Fuel
    ): Response<List<Fuel>>


    @POST("api/v1/maintenances")
    suspend fun addMaintenance(
        @Body post: MaintenanceAdd
    ): Response<List<Maintenance>>


    @POST("api/v1/trips")
    suspend fun addTrip(
        @Body post: TripAdd
    ): Response<List<TripAdd>>

    @POST("api/v1/routes")
    suspend fun addRoute(
        @Body post: Route
    ): Response<List<Route>>

    @POST("api/v1/users/register")
    suspend fun registerUser(
        @Body user: User
    ): Response<User>


    @POST("api/v1/users/login")
    suspend fun loginUser(
        @Body auth:Auth
    ): Response<User>


    @POST("api/v1/otherexpenses")
    suspend fun addOtherExpense(
        @Body expense: OtherExpense
    ): Response<List<OtherExpense>>

    @POST("api/v1/otherrevenues")
    suspend fun addOtherRevenue(
        @Body revenue: OtherRevenue
    ): Response<List<OtherRevenue>>




    /* Patch requests */
    @PATCH("api/v1/users/{userId}")
    suspend fun  updateAccount(
        @Path("userId") id:String,
        @Body post: User
    ): Response<User>


    @PATCH("api/v1/drivers/assignvehicle/{driverId}/{vehicleId}")
    suspend fun assignVehicle(
        @Path("driverId") id:String,
        @Path("vehicleId") vehicle: String
    ): Response<DriverAdd>

    @PATCH("api/v1/vehicles/assignDriver/{driverId}")
    suspend fun assignDriver(
        @Path("id") id:String,
        @Path("driver") driverId:String,

    ): Response<VehicleAdd>

    @PATCH("api/v1/maintenances/markmaintenance/{maintenanceId}")
    suspend fun markMaintenance(
        @Path("maintenanceId") id:String,
    ): Response<MaintenanceAdd>


    @PATCH("api/v1/vehicles/markgeofence/{vehicleId}")
    suspend fun markGeofence(
        @Path("vehicleId") id:String,
        @Query("area") operationArea:String,
        @Query("radius") geofenceRadius:Double
    ): Response<VehicleAdd>



    @PATCH("api/v1/vehicles/{vehicleId}")
    suspend fun updateVehicle(
        @Path("vehicleId") id:String,
        @Body post: VehicleAdd
    ): Response<VehicleAdd>


    @PATCH("api/v1/drivers/{driverId}")
    suspend fun updateDriver(
        @Path("driverId") id:String,
        @Body post: DriverAdd
    ): Response<DriverAdd>


    @PATCH("api/v1/maintenances/{maintenanceId}")
    suspend fun updateMaintenance(
        @Path("maintenanceId") id:String,
        @Body post: MaintenanceAdd
    ): Response<MaintenanceAdd>


    @PATCH("api/v1/routes/{routeId}")
    suspend fun updateRoute(
        @Path("routeId") id:String,
        @Body post: Route
    ): Response<Route>


    /* delete requests  */
    @DELETE("api/v1/users/{userId}")
    suspend fun deleteAccount(
        @Path("userId") id:String
    ): Response<User>


    @DELETE("api/v1/vehicles/{vehicleId}")
    suspend fun  deleteVehicle(
        @Path("vehicleId") id:String
    ): Response<VehicleAdd>


    @DELETE("api/v1/drivers/{driverId}")
    suspend fun deleteDriver(
        @Path("driverId") id:String
    ): Response<DriverAdd>


    @DELETE("api/v1/routes/{routeId}")
    suspend fun  deleteRoute(
        @Path("routeId") id:String
    ): Response<Route>


    @DELETE("api/v1/trips/{tripId}")
    suspend fun deleteTrip(
        @Path("tripId") id:String
    ): Response<TripAdd>

    @DELETE("api/v1/maintenances/{maintenanceId}")
    suspend fun deleteMaintenance(
        @Path("maintenanceId") id:String
    ): Response<MaintenanceAdd>





















}