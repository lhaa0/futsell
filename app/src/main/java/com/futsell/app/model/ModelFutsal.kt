package com.futsell.app.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ModelFutsal(
    @SerializedName("id_futsal") var id_futsal : Int,
    @SerializedName("nama_futsal") var nama_futsal : String,
    @SerializedName("alamat_futsal") var alamat_futsal : String,
    @SerializedName("latitude") var latitude : String,
    @SerializedName("longitude") var longitude : String,
    @SerializedName("open_at") var open_at : Int,
    @SerializedName("close_at") var close_at : Int,
    @SerializedName("rating") var rating : Int,
    @SerializedName("uid_admin") var uid_admin : String
) : Serializable