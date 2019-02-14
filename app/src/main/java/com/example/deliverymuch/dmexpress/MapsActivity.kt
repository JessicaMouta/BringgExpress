package com.example.deliverymuch.dmexpress

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log



import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


import com.bringg.ordertrack.realtime.callbacks.RealTimeConnectionCallback
import com.bringg.ordertrack.BringgClient
import com.bringg.ordertrack.models.Order
import com.bringg.ordertrack.realtime.event_listeners.DriverLocationListener
import com.bringg.ordertrack.realtime.event_listeners.OrderStatusListener


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, RealTimeConnectionCallback {

    private lateinit var mMap: GoogleMap
    private val EXAMPLE_DEV_ACCESS_TOKEN = "EXAMPLE_DEV_ACCESS_TOKEN"
    private val EXAMPLE_UUID = "EXAMPLE_UUID"
    private var bringgClient: BringgClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        bringgClient = BringgClient(this, this, EXAMPLE_DEV_ACCESS_TOKEN)
        bringgClient?.connect()

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
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))


    }

    override fun onDestroy() {
        // it is very important to disconnect the client when we are done with it
        // so it will stop using battery and network.
        super.onDestroy()
        bringgClient?.disconnect()
    }

    override fun onConnect() {
        Log.d("MapsActivityDM", "connected")

        // tell the client to start listening for events related to the order with the given uuid.
        // any event will be handled by the orderStatusChangedListener.
        bringgClient?.watchOrder(EXAMPLE_UUID, orderListener)
    }

    override fun onAck(event: String?, success: Boolean, message: String?) {
    }

    override fun onDisconnect() {
    }

    override fun onError(message: String?) {
    }

    // ==========================================================================
    // ========================== event listeners ===============================

    private val driverListener = DriverLocationListener { lat, lng ->
        Log.d("MapsActivityDM", "driver location: $lat, $lng")
    }

    val orderListener = object : OrderStatusListener {
        override fun onOrderAccepted(order: Order?) {
            Log.i("BRINGGDM", "onOrderAccepted")
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onOrderStarted(order: Order?) {
            Log.i("BRINGGDM", "onOrderStarted")
            print("BRINGGDM ${order?.driverUUID}")
            if (order?.driverUUID != null) {
                // tell the client to start listening for events on the driver with the given id.
                // any event will be handled by the driverLocationListener.
                bringgClient?.watchDriver(order?.driverUUID, order.sharedUUID, driverListener)
            }
        }

        override fun onOrderCanceled() {
            Log.i("BRINGGDM", "onOrderCanceled")
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onOrderAssigned(order: Order?) {
            Log.i("BRINGGDM", "onOrderAssigned")
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onOrderDone() {
            Log.i("BRINGGDM", "onOrderDone")
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onOrderArrived(order: Order?) {
            Log.i("BRINGGDM", "onOrderArrived")
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }
}
