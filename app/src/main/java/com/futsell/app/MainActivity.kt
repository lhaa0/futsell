package com.futsell.app

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log.e
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.futsell.app.activity.LoginActivity
import com.futsell.app.activity.OrderActivity
import com.futsell.app.activity.UserActivity
import com.futsell.app.adapter.MainAdapter
import com.futsell.app.model.ModelFutsal
import com.futsell.app.util.PrefsHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.maps.android.ui.IconGenerator
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : FragmentActivity(), OnMapReadyCallback, LocationListener,
    MainAdapter.onItemClickListener, SearchView.OnQueryTextListener {

    lateinit var gMap: GoogleMap
    private var locationManager: LocationManager? = null
    private val MIN_TIME: Long = 400
    private val MIN_DISTANCE = 1000f
    lateinit var location: Location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val sToolbar = (applicationContext as AppCompatActivity)
//        sToolbar.setSupportActionBar(toolbar)
//        sToolbar.supportActionBar!!.setDisplayShowHomeEnabled(true)
//        sToolbar.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        sToolbar.supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_person_24dp)

        toolbar.inflateMenu(R.menu.menu_main)
        val menuItem = toolbar.menu.findItem(R.id.action_search)
        val searchView = menuItem.actionView as SearchView
        searchView.setOnQueryTextListener(this)
        searchView.queryHint = "Search"
        toolbar.setTitle(PrefsHelper(this).getPref(PrefsHelper.FULLNAME))
        toolbar.setNavigationIcon(R.drawable.ic_person_24dp)
        toolbar.setNavigationOnClickListener {
            startActivity(Intent(this, UserActivity::class.java))
        }

        rcMain.layoutManager = LinearLayoutManager(this)

        (supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment).getMapAsync(
            this
        )
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        getLocation()
    }

    @SuppressLint("MissingPermission")
    fun getLocation() {
        locationManager!!.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            MIN_TIME,
            MIN_DISTANCE,
            this
        )
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
        this.location = location
        getData()
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String?) {
    }

    override fun onProviderDisabled(provider: String?) {
    }

    override fun onClick(futsal: ModelFutsal) {
        val intent = Intent(this, OrderActivity::class.java)
        intent.putExtra("data", futsal)
        startActivity(intent)
    }

//    fun getData(location: Location?) {
//        val apiInterface = ApiClient().getClient().create(ApiInterface::class.java)
//        apiInterface.tb_futsal().enqueue(object : Callback<ArrayList<ModelFutsal>> {
//            override fun onResponse(call: Call<ArrayList<ModelFutsal>>, response: Response<ArrayList<ModelFutsal>>) {
//                if (response.code() == 200) {
//                    val iconFactory = IconGenerator(this@MainActivity)
////                    rcMain.adapter = MainAdapter(response.body()!!, this@MainActivity)
//                    val futsals = ArrayList<ModelFutsal>()
//                    var no = 0
//                    for (point in response.body()!!) {
//                        val target = Location("target");
//                        target.latitude = point.latitude.toDouble()
//                        target.longitude = point.longitude.toDouble()
//                        if (location!!.distanceTo(target) < 5000) {
//                            no++
//                            futsals.add(point)
//                            val marker = LatLng(point.latitude.toDouble(), point.longitude.toDouble())
//                            gMap.addMarker(
//                                MarkerOptions()
//                                    .icon(BitmapDescriptorFactory.fromBitmap(iconFactory
//                                        .makeIcon("$no")))
//                                    .position(marker)
//                                    .title(point.nama_futsal)
//                            )
//                        }
//
//
//                    }
//                    rcMain.adapter = MainAdapter(futsals, this@MainActivity)
//                } else {
//                    e("response", "${response.code()}")
//                    Toast.makeText(this@MainActivity, "Kesalahan Server", Toast.LENGTH_LONG).show()
//                }
//            }
//
//            override fun onFailure(call: Call<ArrayList<ModelFutsal>>, t: Throwable) {
//                e("failure", "error", t)
//                Toast.makeText(this@MainActivity, "Kesalahan Koneksi", Toast.LENGTH_LONG).show()
//            }
//
//        })
//    }

    fun getData() {
        gMap.clear()
        val dbRef = FirebaseDatabase.getInstance().getReference("dataFutsal")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val iconFactory = IconGenerator(this@MainActivity)
//                    rcMain.adapter = MainAdapter(response.body()!!, this@MainActivity)
                val futsals = ArrayList<ModelFutsal>()
                var no = 0
                p0.children.forEach {
                    val data = it.getValue(ModelFutsal::class.java)
                    val target = Location("target");
                    target.latitude = data!!.latitude!!.toDouble()
                    target.longitude = data.longitude!!.toDouble()
                    if (location.distanceTo(target) < 5000) {
                        no++
                        futsals.add(data)
                        val marker = LatLng(data.latitude!!.toDouble(), data.longitude!!.toDouble())
                        gMap.addMarker(
                            MarkerOptions()
                                .icon(
                                    BitmapDescriptorFactory.fromBitmap(
                                        iconFactory
                                            .makeIcon("$no")
                                    )
                                )
                                .position(marker)
                                .title(data.nama_futsal)
                        )
                    }
                }
                rcMain.adapter = MainAdapter(futsals, this@MainActivity)
            }

            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error Server", Toast.LENGTH_LONG).show()
                e("dbError", p0.message, p0.toException())
            }
        })
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        gMap.clear()
        if (query!!.isEmpty()) {
            getData()
            return false
        } else {
            e("text", query)
            var notError = false
            val dbRef = FirebaseDatabase.getInstance().getReference("dataFutsal")
            dbRef.addValueEventListener(object : ValueEventListener {
                @SuppressLint("DefaultLocale")
                override fun onDataChange(p0: DataSnapshot) {
                    val iconFactory = IconGenerator(this@MainActivity)
//                    rcMain.adapter = MainAdapter(response.body()!!, this@MainActivity)
                    val futsals = ArrayList<ModelFutsal>()
                    var no = 0
                    p0.children.forEach {
                        val data = it.getValue(ModelFutsal::class.java)
                        e("ok", data!!.nama_futsal!!)
                        if (data.nama_futsal!!.toLowerCase().contains(query.toLowerCase()) ||
                            data.alamat_futsal!!.toLowerCase().contains(query.toLowerCase())
                        ) {
                            no++
                            futsals.add(data)
                            val marker =
                                LatLng(data.latitude!!.toDouble(), data.longitude!!.toDouble())
                            gMap.addMarker(
                                MarkerOptions()
                                    .icon(
                                        BitmapDescriptorFactory.fromBitmap(
                                            iconFactory
                                                .makeIcon("$no")
                                        )
                                    )
                                    .position(marker)
                                    .title(data.nama_futsal)
                            )

                        }
                    }
                    rcMain.adapter = MainAdapter(futsals, this@MainActivity)
                    notError = true
                }

                override fun onCancelled(p0: DatabaseError) {
                    Toast.makeText(this@MainActivity, "Error Server", Toast.LENGTH_LONG).show()
                    e("dbError", p0.message, p0.toException())
                    notError = false
                }
            })
            return notError
        }
    }

    override fun onResume() {
        super.onResume()
        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
