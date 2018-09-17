package vpn.natife.com

import android.app.Activity
import android.content.Intent
import android.net.VpnService
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonElement
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {

    private var service: ApiInterface? = null
    private var currentIp: String? = null
    private var intent1: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val gson = Gson()
        val retrofit = Retrofit.Builder()
                .baseUrl("https://jsonip.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(OkHttpClient().newBuilder().addInterceptor(object : Interceptor{
                    override fun intercept(chain: Interceptor.Chain?): okhttp3.Response {
                        Log.d("MYVPN", "Request: ${chain?.request()?.url()}, ${chain?.request()?.headers()}")
                        val response = chain?.proceed(chain.request())
                        Log.d("MYVPN", "Response: " + response?.body().toString())
                        return response!!
                    }
                }).build())
                .build()
        service = retrofit.create(ApiInterface::class.java)

        intent1 = VpnService.prepare(this)
        if (intent1 != null) {
            Log.d("MYVPN", "Starting activity")
            startActivityForResult(intent1, 1112)
        } else {
            Log.d("MYVPN", "Intent null")
            onActivityResult(1112, Activity.RESULT_OK, null)
        }

//        val intent = Intent(this, LocalVpnService::class.java)
//        intent.action = VpnService.SERVICE_INTERFACE
//        intent.putExtra("DATA_EXTRA", "Test string")
//        startService(intent)

        button.setOnClickListener {
            startService(intent)
            service?.getIpJson()?.enqueue(object : Callback<JsonElement> {
                override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                    Log.d("MYVPN", "Request failure ${t?.message}")
                }

                override fun onResponse(call: Call<JsonElement>?, response: Response<JsonElement>?) {
                    currentIp = response?.body()!!.asJsonObject["ip"].asString
                    Log.d("MYVPN", "Response ip: $currentIp")
                }
            })
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("MYVPN", "Activity request cod: $requestCode, result code: $resultCode")
        if (requestCode == 1112 && resultCode == Activity.RESULT_OK){
            Log.d("MYVPN", "Result ok")
            startService(Intent(this, LocalVpnService::class.java))
        }
    }
}
