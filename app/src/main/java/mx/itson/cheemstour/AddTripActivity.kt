package mx.itson.cheemstour

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import mx.itson.cheemstour.entities.Trip
import mx.itson.cheemstour.interfaces.CheemsAPI
import mx.itson.cheemstour.utils.RetrofitUtil
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddTripActivity : AppCompatActivity(), OnMapReadyCallback {

    private var map: GoogleMap? = null
    private var selectedLatLng: LatLng? = null

    private lateinit var etName: EditText
    private lateinit var etCity: EditText
    private lateinit var etLatitude: EditText
    private lateinit var etLongitude: EditText
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_trip)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etName      = findViewById(R.id.etName)
        etCity      = findViewById(R.id.etCity)
        etLatitude  = findViewById(R.id.etLatitude)
        etLongitude = findViewById(R.id.etLongitude)
        btnSave     = findViewById(R.id.btnSave)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapPicker) as SupportMapFragment
        mapFragment.getMapAsync(this)

        btnSave.setOnClickListener { saveTrip() }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map!!.mapType = GoogleMap.MAP_TYPE_HYBRID

        val defaultLocation = LatLng(27.9207, -110.8990)
        map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f))

        map!!.setOnMapClickListener { latLng ->
            map!!.clear()
            map!!.addMarker(MarkerOptions().position(latLng).draggable(true))
            map!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))

            selectedLatLng = latLng
            etLatitude.setText(latLng.latitude.toString())
            etLongitude.setText(latLng.longitude.toString())
        }
    }

    private fun saveTrip() {
        val name = etName.text.toString().trim()
        val city = etCity.text.toString().trim()
        val latText = etLatitude.text.toString().trim()
        val lngText = etLongitude.text.toString().trim()

        if (name.isEmpty() || city.isEmpty() || latText.isEmpty() || lngText.isEmpty()) {
            Toast.makeText(this, "Please fill all fields and pick a location on the map", Toast.LENGTH_SHORT).show()
            return
        }

        val latitude  = latText.toDoubleOrNull()
        val longitude = lngText.toDoubleOrNull()

        if (latitude == null || longitude == null) {
            Toast.makeText(this, "Invalid coordinates", Toast.LENGTH_SHORT).show()
            return
        }

        val trip = Trip(null, name, city, latitude, longitude)
        Log.d("AddTrip", "Sending: name=$name, city=$city, lat=$latitude, lng=$longitude")

        val call: Call<ResponseBody> = RetrofitUtil.getApi().saveTrip(trip)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody?>,
                response: Response<ResponseBody?>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AddTripActivity, "Location saved", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@AddTripActivity, "Error saving location", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Log.e("AddTripActivity", "API error: ${t.message}")
                Toast.makeText(this@AddTripActivity, "Connection error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}