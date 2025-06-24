package com.example.zerofood.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.InputStream

private const val TAG = "MercadoMapScreen"
private const val API_KEY = "AIzaSyDbA3i2q9yVWI8--c6Lwz-9SyMmmoMPa1U" // <---- COLOQUE SUA CHAVE AQUI

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MercadoMapScreen() {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var mapStyleOptions by remember { mutableStateOf<MapStyleOptions?>(null) }
    var markets by remember { mutableStateOf<List<Place>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    // Carrega estilo customizado
    LaunchedEffect(Unit) {
        try {
            val inputStream: InputStream = context.assets.open("map_style.json")
            val json = inputStream.bufferedReader().use { it.readText() }
            mapStyleOptions = MapStyleOptions(json)
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao carregar estilo do mapa", e)
        }
    }

    // Obter localização do usuário se permissão concedida
    LaunchedEffect(locationPermissionState.status.isGranted) {
        if (locationPermissionState.status.isGranted) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        userLocation = LatLng(location.latitude, location.longitude)
                    } else {
                        val request = LocationRequest.create().apply {
                            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                            interval = 0
                            fastestInterval = 0
                            numUpdates = 1
                        }
                        fusedLocationClient.requestLocationUpdates(
                            request,
                            object : LocationCallback() {
                                override fun onLocationResult(result: LocationResult) {
                                    val loc = result.lastLocation
                                    if (loc != null) {
                                        userLocation = LatLng(loc.latitude, loc.longitude)
                                    }
                                }
                            },
                            Looper.getMainLooper()
                        )
                    }
                }
            }
        } else {
            locationPermissionState.launchPermissionRequest()
        }
    }

    // Quando a localização do usuário estiver disponível, buscar mercados próximos pela Places API
    LaunchedEffect(userLocation) {
        userLocation?.let { location ->
            markets = fetchNearbyMarkets(location.latitude, location.longitude)
        }
    }

    Column(Modifier.fillMaxSize()) {
        if (userLocation != null && mapStyleOptions != null) {
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(userLocation!!, 15f)
            }

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(mapStyleOptions = mapStyleOptions),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    myLocationButtonEnabled = true
                ),
                onMapLoaded = { Log.d(TAG, "Mapa carregado") }
            ) {
                Marker(
                    state = MarkerState(position = userLocation!!),
                    title = "Você está aqui",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                )

                markets.forEachIndexed { index, place ->
                    Marker(
                        state = MarkerState(position = LatLng(place.geometry.location.lat, place.geometry.location.lng)),
                        title = place.name,
                        snippet = place.vicinity,
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Carregando mapa ou aguardando permissão...", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

// Modelo de dados para resposta da Places API (parcial)

@Serializable
data class PlacesResponse(
    val results: List<Place>
)

@Serializable
data class Place(
    val name: String,
    val vicinity: String = "",
    val geometry: Geometry
)

@Serializable
data class Geometry(
    val location: LocationLatLng
)

@Serializable
data class LocationLatLng(
    val lat: Double,
    val lng: Double
)

// Função para buscar mercados próximos usando Ktor HTTP Client e Google Places API
suspend fun fetchNearbyMarkets(lat: Double, lng: Double): List<Place> = withContext(Dispatchers.IO) {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true // para ignorar campos desconhecidos do JSON
            })
        }
    }

    val radius = 1500 // metros (1.5 km)
    val type = "grocery_or_supermarket"

    val url =
        "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=$lat,$lng&radius=$radius&type=$type&key=$API_KEY"

    try {
        val response: PlacesResponse = client.get(url).body()
        client.close()
        return@withContext response.results
    } catch (e: Exception) {
        Log.e(TAG, "Erro ao buscar mercados próximos: ", e)
        client.close()
        emptyList()
    }
}
