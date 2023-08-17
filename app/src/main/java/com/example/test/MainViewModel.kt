package com.example.test

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retrofittutorial.api.RetrofitInstance
import com.example.test.models.*
import com.example.test.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response

class MainViewModel(private val repository: Repository): ViewModel() {
        val vehiclesResponse: MutableLiveData<Response<List<Vehicle>>> = MutableLiveData()
        val driversResponse:  MutableLiveData<Response<List<Driver>>> = MutableLiveData()
        val fuelsResponse:  MutableLiveData<Response<List<Fuel>>> = MutableLiveData()
        val maintenancesResponse:  MutableLiveData<Response<List<Maintenance>>> = MutableLiveData()
        val tripsResponse:  MutableLiveData<Response<List<Trip>>> = MutableLiveData()
        val routesResponse:  MutableLiveData<Response<List<Route>>> = MutableLiveData()
        val fuelTypesResponse:  MutableLiveData<Response<List<FuelType>>> = MutableLiveData()
        val vehicleRankResponse:  MutableLiveData<Response<List<VehicleStat>>> = MutableLiveData()
        val driverRankResponse:  MutableLiveData<Response<List<DriverStat>>> = MutableLiveData()
        val routesRankResponse:  MutableLiveData<Response<List<RouteStat>>> = MutableLiveData()
        val vehicleStatsResponse:  MutableLiveData <Response<List<VehicleStat>>> = MutableLiveData()
        val vehicleResponse: MutableLiveData<Response<VehicleAdd>> = MutableLiveData()
        val vehicleResponse2: MutableLiveData<Response<Vehicle>> = MutableLiveData()
        val driverResponse:  MutableLiveData<Response<DriverAdd>> = MutableLiveData()
        val maintenanceResponse:  MutableLiveData<Response<MaintenanceAdd>> = MutableLiveData()
        val tripResponse:  MutableLiveData<Response<List<TripAdd>>> = MutableLiveData()
        val userResponse: MutableLiveData<Response<User>> = MutableLiveData()
        val routeResponse: MutableLiveData<Response<Route>> = MutableLiveData()
        val tiripResponse: MutableLiveData<Response<TripAdd>> = MutableLiveData()

    val countsResponse: MutableLiveData<Response<Counts>> = MutableLiveData()
        val expensesResponse: MutableLiveData<Response<List<OtherExpense>>> = MutableLiveData()
        val revenueResponse: MutableLiveData<Response<List<OtherRevenue>>> = MutableLiveData()
        val routeStatsResponse:  MutableLiveData <Response<List<RouteStat>>> = MutableLiveData()
        val driverStatsResponse:  MutableLiveData <Response<List<StatDriver>>> = MutableLiveData()


    /*    get requests    */
        fun getVehicles() {
            viewModelScope.launch {
                val response: Response<List<Vehicle>> = repository.getVehicles()
                vehiclesResponse.value= response
            }
        }


        fun getVehiclesByRoute(route: String) {
            viewModelScope.launch {
                val response: Response<List<Vehicle>> = repository.getVehiclesByRoute(route)
                vehiclesResponse.value= response
            }
        }

        fun getCounts(){
            viewModelScope.launch {
                val response: Response<Counts> = repository.getCounts()
                countsResponse.value= response
            }
        }

        fun getOtherExpenses(){
            viewModelScope.launch {
                val response: Response<List<OtherExpense>> = repository.getOtherExpenses()
                expensesResponse.value= response
            }
        }

        fun getOtherRevenue(){
            viewModelScope.launch {
                val response: Response<List<OtherRevenue>> = repository.getOtherRevenue()
                revenueResponse.value= response
            }
        }

        fun getDrivers() {
            viewModelScope.launch {
                val response: Response<List<Driver>> = repository.getDrivers()
                driversResponse.value= response
            }
        }

        fun getFuels() {
            viewModelScope.launch {
                val response: Response<List<Fuel>> = repository.getFuels()
                fuelsResponse.value= response
            }
        }

        fun getMaintenances() {
            viewModelScope.launch {
                val response: Response<List<Maintenance>> = repository.getMaintenances()
                maintenancesResponse.value= response
            }
        }

        fun getRoutes() {
            viewModelScope.launch {
                val response: Response<List<Route>> = repository.getRoutes()
                routesResponse.value= response
            }
        }

        fun getTrips(startDt: String, endDt:String) {
            viewModelScope.launch {
                val response: Response<List<Trip>> = repository.getTrips(startDt, endDt)
                tripsResponse.value= response
            }
        }


      /*
        fun getVehicleRanks(){
            viewModelScope.launch {
                val response: Response<List<VehicleStat>> = repository.getVehicleRanks()
                vehicleRankResponse.value= response
            }
        } */

        fun getDriverRanks(){
            viewModelScope.launch {
                val response: Response<List<DriverStat>> = repository.getDriverRanks()
                driverRankResponse.value= response
            }
        }

        fun getRoutesRank(){
            viewModelScope.launch {
                val response: Response<List<RouteStat>> = repository.getRoutesRank()
                routesRankResponse.value= response
            }
        }

        fun getVehicleStats(startDate: String, endDate: String){
            viewModelScope.launch {
                val response: Response<List<VehicleStat>> = repository.getVehicleStats(startDate,endDate)
                vehicleStatsResponse.value= response
            }
        }


        fun getRouteStats(startDate: String, endDate: String){
            viewModelScope.launch {
                val response: Response<List<RouteStat>> = repository.getRouteStats(startDate, endDate)
                routeStatsResponse.value= response
            }
        }



        fun getDriverStats(startDate: String, endDate: String){
            viewModelScope.launch {
                val response: Response<List<StatDriver>> = repository.getDriverStats(startDate, endDate)
                driverStatsResponse.value= response
            }
        }



        fun getFuelTypes(){
            viewModelScope.launch {
                val response: Response<List<FuelType>> = repository.getFuelTypes()
                fuelTypesResponse.value= response
            }
        }


        //post request
        fun addVehicle(vehicle: VehicleAdd){
            viewModelScope.launch {
                val response: Response<List<Vehicle>> = repository.addVehicle(vehicle)
                vehiclesResponse.value = response
            }
        }

        fun addDriver(driver: DriverAdd){
            viewModelScope.launch {
                val response: Response<List<Driver>> = repository.addDriver(driver)
                driversResponse.value = response
            }
        }

        fun addFuel(fuel: Fuel){
            viewModelScope.launch {
                val response: Response<List<Fuel>> = repository.addFuel(fuel)
                fuelsResponse.value = response
            }
        }

        fun addMaintenance(maintenance: MaintenanceAdd){
            viewModelScope.launch {
                val response: Response<List<Maintenance>> = repository.addMaintenance(maintenance)
                maintenancesResponse.value = response
            }
        }


        fun addRoute(route: Route){
            viewModelScope.launch {
                val response: Response<List<Route>> = repository.addRoute(route)
                routesResponse.value = response
            }
        }

        fun addTrip(trip: TripAdd) {
            viewModelScope.launch {
                val response: Response<List<TripAdd>> = repository.addTrip(trip)
                tripResponse.value = response
            }
        }


        fun addOtherExpense(expense: OtherExpense){
            viewModelScope.launch {
                val response: Response<List<OtherExpense>> = repository.addOtherExpense(expense)
                expensesResponse.value= response
            }
        }

        fun addOtherRevenue(revenue: OtherRevenue){
            viewModelScope.launch {
                val response: Response<List<OtherRevenue>> = repository.addOtherRevenue(revenue)
                revenueResponse.value= response
            }
        }


        fun registerUser(user: User) {
            viewModelScope.launch {
                val response: Response<User> = repository.registerUser(user)
                userResponse.value = response
            }
        }

         fun loginUser(auth: Auth){
             viewModelScope.launch {
                 val response: Response<User> = repository.loginUser(auth)
                 userResponse.value = response
             }
         }

        fun assignVehicle(driverId: String, vehicleId:String){
            viewModelScope.launch {
                val response: Response<DriverAdd> = repository.assignVehicle(driverId,vehicleId)
                driverResponse.value = response
            }
        }

        fun assignDriver(vehicleId: String, driverId:String) {
            viewModelScope.launch {
                val response: Response<VehicleAdd> = repository.assignDriver(vehicleId, driverId)
                vehicleResponse.value = response
            }
        }



    //patch requests
        fun updateAccount(userId: String, user: User){
            viewModelScope.launch {
                val response: Response<User> = repository.updateAccount(userId, user)
                userResponse.value = response
            }
        }

        fun markMaintenance(maintenanceId: String){
            viewModelScope.launch {
                val response: Response<MaintenanceAdd> = repository.markMaintenance(maintenanceId)
                maintenanceResponse.value = response
            }
        }


        fun markGeofence(vehicleId: String, operationArea:String, geofenceRadius:Double){
            viewModelScope.launch {
                val response: Response<VehicleAdd> = repository.markGeofence(vehicleId,operationArea,geofenceRadius)
                vehicleResponse.value = response
            }
        }


        fun updateVehicle(vehicleId: String, vehicle:VehicleAdd){
            viewModelScope.launch {
                val response: Response<VehicleAdd> = repository.updateVehicle(vehicleId, vehicle)
                vehicleResponse.value = response
            }
        }

        fun updateDriver(driverId: String, driver: DriverAdd){
            viewModelScope.launch {
                val response: Response<DriverAdd> = repository.updateDriver(driverId, driver)
                driverResponse.value = response
            }
        }

        fun updateMaintenance(maintenanceId: String, maintenance: MaintenanceAdd){
            viewModelScope.launch {
                val response: Response<MaintenanceAdd> = repository.updateMaintenance(maintenanceId, maintenance)
                maintenanceResponse.value = response
            }
        }

        fun updateRoute(routeId: String, route:Route){
            viewModelScope.launch {
                val response: Response<Route> = repository.updateRoute(routeId, route)
                routeResponse.value = response
            }
        }



        //delete Requests
        fun deleteAccount(userId: String){
            viewModelScope.launch {
                val response: Response<User> = repository.deleteAccount(userId)
                userResponse.value = response
            }
        }

        fun deleteMaintenance(maintenanceId: String){
            viewModelScope.launch {
                val response: Response<MaintenanceAdd> = repository.deleteMaintenance(maintenanceId)
                maintenanceResponse.value = response
            }
        }

        fun deleteVehicle(vehicleId: String){
            viewModelScope.launch {
                val response: Response<VehicleAdd> = repository.deleteVehicle(vehicleId)
                vehicleResponse.value = response
            }
        }

        fun deleteDriver(driverId: String){
            viewModelScope.launch {
                val response: Response<DriverAdd> = repository.deleteDriver(driverId)
                driverResponse.value = response
            }
        }

        fun deleteRoute(routeId: String){
            viewModelScope.launch {
                val response: Response<Route> = repository.deleteRoute(routeId)
                routeResponse.value = response
            }
        }

        fun deleteTrip(tripId: String){
            viewModelScope.launch {
                val response: Response<TripAdd> = repository.deleteTrip(tripId)
                tiripResponse.value = response
            }
        }
}








