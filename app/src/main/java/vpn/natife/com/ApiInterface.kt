package vpn.natife.com

import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.GET

interface ApiInterface{
    @GET("https://jsonip.com/")
    fun getIpJson(): Call<JsonElement>
}