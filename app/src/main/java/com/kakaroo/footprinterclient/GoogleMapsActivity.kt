package com.kakaroo.footprinterclient

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.kakaroo.footprinterclient.Entity.FootPrinter
import com.kakaroo.footprinterclient.databinding.ActivityMapsBinding

class GoogleMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    var mLocationList : ArrayList<FootPrinter> = ArrayList<FootPrinter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent.hasExtra(Common.INTENT_VALUE_NAME))
        {
            mLocationList = intent.getSerializableExtra(Common.INTENT_VALUE_NAME) as ArrayList<FootPrinter>
        }
        else
        {
            Log.e(Common.LOG_TAG, "가져온 데이터 없음")
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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

        val latLng = LatLng(mLocationList[Common.LIST_RECENT_INDEX].latitude, mLocationList[Common.LIST_RECENT_INDEX].longitude)
        val cameraPosition = CameraPosition.Builder()
            .target(latLng)
            .zoom(Common.MAP_ZOOM_VALUE)
            .build()

        //Marker
        val markerOptions = MarkerOptions()

        val bmMarker: BitmapDrawable = getResources().getDrawable(R.mipmap.marker) as BitmapDrawable
        val bmRecMarker: BitmapDrawable = getResources().getDrawable(R.mipmap.recent_marker) as BitmapDrawable

        val bm: Bitmap = bmMarker.getBitmap()
        val bmRec: Bitmap = bmRecMarker.getBitmap()

        val smallMarker: Bitmap = Bitmap.createScaledBitmap(bm, Common.MARKER_WIDTH, Common.MARKER_HEIGHT, false)
        val smallRecMarker: Bitmap = Bitmap.createScaledBitmap(bmRec, Common.MARKER_WIDTH, Common.MARKER_HEIGHT, false)

        mLocationList.forEachIndexed { index, it ->
            if(index == 0) {
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallRecMarker))
            } else {
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
            }
            markerOptions.position(LatLng(it.latitude, it.longitude))
            markerOptions.title((index+1).toString())
            markerOptions.snippet(it.time)
            //mMap.addMarker(MarkerOptions().position(latlng).title(mLocationList[0].time))
            mMap.addMarker((markerOptions))
        }

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

        /*
         mMap = googleMap
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        updateLocation()
         */
    }
/*
    // 위치 정보를 받아오는 역할
    @SuppressLint("MissingPermission") //requestLocationUpdates는 권한 처리가 필요한데 현재 코드에서는 확인 할 수 없음. 따라서 해당 코드를 체크하지 않아도 됨.
    fun updateLocation() {
        val locationRequest = LocationRequest.create()
        locationRequest.run {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 1000
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult?.let {
                    for(location in it.locations) {
                        Log.d("Location", "${location.latitude} , ${location.longitude}")
                        setLastLocation(location)
                    }
                }
            }
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
    }

    fun setLastLocation(lastLocation: Location) {
        val LATLNG = LatLng(lastLocation.latitude, lastLocation.longitude)
        val markerOptions = MarkerOptions()
            .position(LATLNG)
            .title("Here!")

        val cameraPosition = CameraPosition.Builder()
            .target(LATLNG)
            .zoom(15.0f)
            .build()
        mMap.clear()
        mMap.addMarker(markerOptions)
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }
*/
}