package com.cibertec.apirestmapbox

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.location.LocationEngineRequest
import com.mapbox.android.core.location.LocationEngineResult
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.locationcomponent.location
import java.lang.Exception
import java.lang.ref.WeakReference

var mapView: MapView? = null
val DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L
val DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5

class MainActivity : AppCompatActivity() {

    lateinit var locationEngine: LocationEngine
    private val callback = LocationListeningCallBack(this)
    lateinit var permissionsManager: PermissionsManager

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapView = findViewById(R.id.mapView)
        //mapView?.getMapboxMap()?.loadStyleUri(Style.MAPBOX_STREETS)
        mapView?.getMapboxMap()?.loadStyleUri(
            Style.MAPBOX_STREETS,
            // After the style is loaded, initialize the location component
            object : Style.OnStyleLoaded {
                override fun onStyleLoaded(style: Style) {
                    mapView?.location?.updateSettings {
                        enabled = true
                        pulsingEnabled = true
                    }
                }
            }
        )

        var permissionsListener: PermissionsListener = object : PermissionsListener {
            override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
                TODO("Not yet implemented")
            }

            override fun onPermissionResult(granted: Boolean) {
                if(granted) {
                    Toast.makeText(this@MainActivity, "Permisos de ubicacion otorgados", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@MainActivity, "No se dio permisos de ubicacion", Toast.LENGTH_LONG).show()
                }
            }
        }

        locationEngine = LocationEngineProvider.getBestLocationEngine(this)
        var request = LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
            .setPriority(LocationEngineRequest.PRIORITY_NO_POWER)
            .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME)
            .build()

        if(PermissionsManager.areLocationPermissionsGranted(this)) {
            locationEngine.requestLocationUpdates(request, callback, mainLooper)
            locationEngine.getLastLocation(callback)
        } else {
            permissionsManager = PermissionsManager(permissionsListener)
            permissionsManager.requestLocationPermissions(this)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private class LocationListeningCallBack internal constructor(activity: MainActivity) : LocationEngineCallback<LocationEngineResult>{

        private val activityWeakReference: WeakReference<MainActivity>

        init { this.activityWeakReference = WeakReference(activity)
        }

        override fun onSuccess(result: LocationEngineResult?) {
            result?.lastLocation
            Log.d("LATITUD", result?.lastLocation?.latitude.toString())
            Log.d("LONGITUD", result?.lastLocation?.longitude.toString())
        }

        override fun onFailure(exception: Exception) {

        }
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }
    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }
    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }
    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }
}