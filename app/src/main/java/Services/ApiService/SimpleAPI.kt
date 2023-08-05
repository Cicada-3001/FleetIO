package Services.ApiService

import Model.Vehicle
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface SimpleAPI {
    @GET("api/v1/vehicles/{vin}")
    suspend fun getVehiclesByVin(
        @Path("vin") vin:String
    ): Response<ArrayList<Vehicle>>
}