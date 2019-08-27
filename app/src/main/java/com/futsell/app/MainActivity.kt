package com.futsell.app

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Log.e
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.futsell.app.adapter.MainAdapter
import com.futsell.app.model.ModelFutsal
import com.futsell.app.util.ApiClient
import com.futsell.app.util.ApiInterface
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.android.synthetic.main.activity_main.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.ui.IconGenerator
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception


class MainActivity : FragmentActivity(), OnMapReadyCallback, LocationListener, MainAdapter.onItemClickListener {

    lateinit var gMap: GoogleMap
    lateinit var mapFragment: SupportMapFragment
    private var locationManager: LocationManager? = null
    private val MIN_TIME: Long = 400
    private val MIN_DISTANCE = 1000f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rcMain.layoutManager = LinearLayoutManager(this)

        (supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment).getMapAsync(this)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        getLocation()
    }

    @SuppressLint("MissingPermission")
    fun getLocation() {
        locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this)
    }

    override fun onMapReady(mMap: GoogleMap?) {
        gMap = mMap!!

        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        gMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        gMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))


        //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER
    }

    override fun onLocationChanged(location: Location?) {
        val latLng = LatLng(location!!.getLatitude(), location.getLongitude())
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
        gMap.animateCamera(cameraUpdate)
        locationManager!!.removeUpdates(this)
        getData(location)
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String?) {
    }

    override fun onProviderDisabled(provider: String?) {
    }

    override fun onClick(futsal: ModelFutsal) {

    }

    fun getData(location: Location?) {
        val apiInterface = ApiClient().getClient().create(ApiInterface::class.java)
        apiInterface.tb_futsal().enqueue(object : Callback<ArrayList<ModelFutsal>> {
            override fun onResponse(call: Call<ArrayList<ModelFutsal>>, response: Response<ArrayList<ModelFutsal>>) {
                if (response.code() == 200) {
                    val iconFactory = IconGenerator(this@MainActivity)
//                    rcMain.adapter = MainAdapter(response.body()!!, this@MainActivity)
                    val futsals = ArrayList<ModelFutsal>()
                    var no = 0
                    for (point in response.body()!!) {
                        val target = Location("target");
                        target.latitude = point.latitude.toDouble()
                        target.longitude = point.longitude.toDouble()
                        if (location!!.distanceTo(target) < 5000) {
                            no++
                            futsals.add(point)
                            val marker = LatLng(point.latitude.toDouble(), point.longitude.toDouble())
                            gMap.addMarker(
                                MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromBitmap(iconFactory
                                        .makeIcon("$no")))
                                    .position(marker)
                                    .title(point.nama_futsal)
                            )
                        }


                    }
                    rcMain.adapter = MainAdapter(futsals, this@MainActivity)
                } else {
                    e("response", "${response.code()}")
                    Toast.makeText(this@MainActivity, "Kesalahan Server", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ArrayList<ModelFutsal>>, t: Throwable) {
                e("failure", "error", t)
                Toast.makeText(this@MainActivity, "Kesalahan Koneksi", Toast.LENGTH_LONG).show()
            }

        })
    }


}
