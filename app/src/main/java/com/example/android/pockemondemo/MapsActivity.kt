package com.example.android.pockemondemo

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Exception

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        loadPockemons()
        checkPermission()
    }

    val AccessLocation = 123
    fun checkPermission()
    {
        if(Build.VERSION.SDK_INT>=23)
        {
            if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),AccessLocation)
                return
            }

        }
        getUserLocation()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            AccessLocation->if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                getUserLocation()

            }else{
                Toast.makeText(this,"location access is deny",Toast.LENGTH_LONG).show()
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    fun getUserLocation()
    {

        Toast.makeText(this,"Location access now",Toast.LENGTH_LONG).show()

        val mylocation = MyLocationListener()
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3,3f,mylocation)
        val myThread = MyThread()
        myThread.start()
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera

    }

    var mylocation : Location?=null
    inner class MyLocationListener : LocationListener{
        constructor(){
            mylocation  = Location("me")
            mylocation!!.longitude=0.0
            mylocation!!.latitude=0.0

        }
        override fun onLocationChanged(location: Location?) {
            mylocation = location
          

        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

        }

        override fun onProviderEnabled(p0: String?) {

        }

        override fun onProviderDisabled(p0: String?) {

        }

    }

    var oldLocation:Location?=null
    inner class MyThread : Thread {

        constructor():super(){

            oldLocation = Location("oldLocation")
            oldLocation!!.longitude=0.0
            oldLocation!!.latitude=0.0
        }

        override fun run() {
            while (true)
            {
                try {
                            if(oldLocation!!.distanceTo(mylocation)==0f)
                            {
                                continue
                            }
                    oldLocation=mylocation
                runOnUiThread {
                    mMap!!.clear()
                    val sydney = LatLng(mylocation!!.latitude,mylocation!!.longitude)
                    mMap.addMarker(MarkerOptions().position(sydney).title("Me").snippet("here my location").icon(
                        BitmapDescriptorFactory.fromResource(R.drawable.mario))
                    )
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,14f))

                    for(i in 0..listOfPockemons.size-1){
                        var newPockemon = listOfPockemons[i]
                        if(newPockemon.isCatch==false)
                        {
                            val pockLocation = LatLng(newPockemon.location!!.latitude,newPockemon.location!!.longitude)
                            mMap.addMarker(MarkerOptions().position(pockLocation).title(newPockemon.name).snippet(newPockemon.des).icon(
                                BitmapDescriptorFactory.fromResource(newPockemon.image!!))
                            )
                            if(mylocation!!.distanceTo(newPockemon.location)<2)
                            {
                                power = power +newPockemon.power!!
                                newPockemon.isCatch=true
                                listOfPockemons[i]= newPockemon
                                Toast.makeText(applicationContext, "you catch the pockemon", Toast.LENGTH_LONG).show()
                            }
                        }

                    }
                }


                    Thread.sleep(1000)
                }catch (ex:Exception){}
            }

        }
    }
    var power=0.0
    var listOfPockemons  = ArrayList<Pockemon>()

    fun loadPockemons()
    {
        listOfPockemons.add(Pockemon("Gamaken","living in japan",R.drawable.pock1,34.5,37.5,-122.0))
        listOfPockemons.add(Pockemon("San","living in Usa",R.drawable.pock2,90.5,37.5,-122.0))
        listOfPockemons.add(Pockemon("Gamaken","living in japan",R.drawable.pock3,37.5,37.5,-122.0))
    }
}