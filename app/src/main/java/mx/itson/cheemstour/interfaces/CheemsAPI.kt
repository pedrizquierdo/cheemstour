package mx.itson.cheemstour.interfaces

import mx.itson.cheemstour.entities.Trip
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface CheemsAPI {

    @GET("trips")
    fun getTrips() : Call<List<Trip>>

    @POST("trip")
    @Headers("Content-Type: application/json")
    fun saveTrip(@Body trip: Trip): Call<ResponseBody>
}