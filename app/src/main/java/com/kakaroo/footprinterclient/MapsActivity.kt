package com.kakaroo.footprinterclient

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.ui.IconGenerator
import com.kakaroo.footprinterclient.Entity.FootPrinter
import com.kakaroo.footprinterclient.databinding.ActivityMapsBinding
import com.kakaroo.footprinterclient.databinding.MarkerLayoutBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private lateinit var markerbinding: MarkerLayoutBinding
    private lateinit var mIconGenerator: IconGenerator

    var mLocationList : ArrayList<FootPrinter> = ArrayList<FootPrinter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        markerbinding = MarkerLayoutBinding.inflate(layoutInflater)

        mIconGenerator = IconGenerator(this)
        mIconGenerator.setContentView(markerbinding.root)

        if(intent.hasExtra(Common.INTENT_VALUE_NAME)) {
            mLocationList = intent.getSerializableExtra(Common.INTENT_VALUE_NAME) as ArrayList<FootPrinter>
        } else {
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

        val latLng = LatLng(mLocationList[Common.LIST_RECENT_INDEX].latitude, mLocationList[Common.LIST_RECENT_INDEX].longitude)
        /*val cameraPosition = CameraPosition.Builder()
            .target(latLng)
            .zoom(Common.MAP_ZOOM_VALUE)
            .build()*/

        var latLngList : ArrayList<LatLng> = ArrayList<LatLng>()

        //Marker
        val markerOptions = MarkerOptions()

        mLocationList.forEachIndexed { index, it ->
            if(index == 0) {
                markerbinding.tvMarker.visibility = View.INVISIBLE
                markerbinding.tvRecentMarker.visibility = View.VISIBLE
                //markerbinding.tvRecentMarker.text = (index).toString()
                mIconGenerator.setColor(R.color.location_color)
            } else {
                markerbinding.tvRecentMarker.visibility = View.INVISIBLE
                markerbinding.tvMarker.visibility = View.VISIBLE
                markerbinding.tvMarker.text = (index).toString()
                mIconGenerator.setColor(Color.TRANSPARENT)
                //mIconGenerator.setColor(R.color.marker_color)
            }

            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(mIconGenerator.makeIcon()))

            val latLng = LatLng(it.latitude, it.longitude)
            latLngList.add(latLng)

            markerOptions.position(latLng).title(it.date).snippet(it.time)
            mMap.addMarker((markerOptions))
        }

        var polylineOptions = PolylineOptions().color(Color.RED/*R.color.polyline_color*/).width(5f).addAll(latLngList)
        mMap.addPolyline(polylineOptions)

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Common.MAP_ZOOM_VALUE));
        //mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

        /*
         mMap = googleMap
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        updateLocation()
         */

        var mapUiSettings: UiSettings = mMap.getUiSettings()
        mapUiSettings.setZoomControlsEnabled(true)  // 줌버튼
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