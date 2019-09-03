package com.futsell.app.model

import com.google.firebase.database.IgnoreExtraProperties
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@IgnoreExtraProperties
data class ModelFutsal(
    var id_futsal: Int? = 0,
    var nama_futsal: String? = "",
    var alamat_futsal: String? = "",
    var latitude: String? = "",
    var longitude: String? = "",
    var open_at: Int? = 0,
    var close_at: Int? = 0,
    var rating: Int? = 0,
    var uid_admin: String? = ""
) : Serializable