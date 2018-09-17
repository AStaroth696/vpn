package vpn.natife.com

import com.google.gson.annotations.SerializedName

data class ResponseModel(val ip: String,
                         val about: String,

                         @SerializedName("Pro!")
                         val pro: String,

                         @SerializedName("reject-fascism")
                         val reject: String)