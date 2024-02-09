package com.example.juliangarridogps
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.lang.Math.*
import kotlin.math.pow

class MainActivity : AppCompatActivity(), LocationListener {

    private lateinit var locationManager: LocationManager
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            // Permiso de ubicación otorgado, obtener la ubicación actual
            obtainLocation()
        }
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            showCoordinateInputDialog()
        }
    }

    private fun showCoordinateInputDialog() {
        val inputLayout = LinearLayout(this)
        inputLayout.orientation = LinearLayout.VERTICAL
        val inputLatitud = EditText(this)
        val inputLongitud = EditText(this)
        inputLatitud.hint = "Latitud"
        inputLongitud.hint = "Longitud"
        inputLayout.setPadding(50, 0, 50, 0)
        inputLayout.addView(inputLatitud)
        inputLayout.addView(inputLongitud)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Introducir Coordenadas")
            .setView(inputLayout)
            .setPositiveButton("Aceptar") { dialog, which ->
                val latitud = inputLatitud.text.toString().toDouble()
                val longitud = inputLongitud.text.toString().toDouble()
                // Aquí puedes hacer algo con las coordenadas introducidas por el usuario
                // Por ejemplo, calcular la distancia entre la ubicación actual y las coordenadas introducidas
                // o almacenarlas en tu ViewModel
                Log.d("Coordenadas", "Coordenadas introducidas -> Latitud:$latitud Longitud:$longitud")
                calcularDistancia(latitud,longitud)
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }

    private fun obtainLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
    }

    override fun onLocationChanged(location: Location) {
        // La ubicación actual se encuentra en el objeto 'location'
        val latitude = location.latitude
        val longitude = location.longitude
        // Utiliza la ubicación como sea necesario
        Log.d("Ubicacion", "Latitud: $latitude, Longitud: $longitude")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permiso de ubicación otorgado, obtener la ubicación actual
                    obtainLocation()
                } else {
                    // Permiso de ubicación denegado, informar al usuario o tomar medidas alternativas
                    // ...
                }
                return
            }
        }
    }

    private fun calcularDistancia(latitudCoordenada: Double, longitudCoordenada: Double) {
        // Coordenadas de tu ubicación actual (ejemplo)
        val latitudActual = 40.7128
        val longitudActual = -74.0060

        // Radio de la Tierra en kilómetros
        val radioTierra = 6371

        // Convertir las coordenadas a radianes
        val latitud1 = Math.toRadians(latitudActual)
        val latitud2 = Math.toRadians(latitudCoordenada)
        val longitud1 = Math.toRadians(longitudActual)
        val longitud2 = Math.toRadians(longitudCoordenada)

        // Diferencia de latitud y longitud
        val difLatitud = latitud2 - latitud1
        val difLongitud = longitud2 - longitud1

        // Fórmula de Haversine
        val a = sin(difLatitud / 2).pow(2) + cos(latitud1) * cos(latitud2) * sin(difLongitud / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val distancia = radioTierra * c

        Log.d("Distancia", "La distancia a la coordenada introducida es: $distancia km")
    }
}