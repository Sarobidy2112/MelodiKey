package com.example.melodikey;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;
import java.util.Locale;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;

public class MapLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap myMap;
    private final int FINE_PERMISSION_CODE = 1;
    private Marker searchMarker;

    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    SearchView mapSearch;

    Button locationConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map_location);


        locationConfirm = findViewById(R.id.locationConfirm);

        mapSearch = findViewById(R.id.mapSearch);
        mapSearch.setEnabled(false);

        mapSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (myMap == null) {
                    Toast.makeText(MapLocationActivity.this, "La carte n'est pas encore prête", Toast.LENGTH_SHORT).show();
                    return false;
                }

                String locationSearch = mapSearch.getQuery().toString();
                List<Address> addressList = null;

                if (locationSearch != null && !locationSearch.isEmpty()) {
                    Geocoder geocoder = new Geocoder(MapLocationActivity.this);

                    try {
                        addressList = geocoder.getFromLocationName(locationSearch, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (addressList != null && !addressList.isEmpty()) {
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                        myMap.clear();

                        // Ajoute le nouveau marqueur
                        searchMarker = myMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(locationSearch));
                        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));


                    } else {
                        Toast.makeText(MapLocationActivity.this, "Aucun résultat trouvé", Toast.LENGTH_SHORT).show();
                    }
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();


        locationConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myMap != null) {
                    LatLng selectedLatLng;
                    String searchedPlaceName = (searchMarker != null) ? searchMarker.getTitle() : null;

                    // Si l'utilisateur a fait une recherche, utiliser le marqueur de recherche
                    if (searchMarker != null) {
                        selectedLatLng = searchMarker.getPosition();
                    }
                    // Sinon, utiliser la position actuelle de la caméra
                    else {
                        selectedLatLng = myMap.getCameraPosition().target;
                    }

                    // Utiliser le Geocoder pour obtenir les détails de l'adresse
                    Geocoder geocoder = new Geocoder(MapLocationActivity.this, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(
                                selectedLatLng.latitude,
                                selectedLatLng.longitude,
                                1
                        );

                        StringBuilder locationText = new StringBuilder();

                        // 1. Ajouter le nom recherché s'il existe
                        if (searchedPlaceName != null && !searchedPlaceName.isEmpty()) {
                            locationText.append(searchedPlaceName);
                        }

                        // 2. Ajouter les détails de l'adresse (ville, quartier, rue)
                        if (addresses != null && !addresses.isEmpty()) {
                            Address address = addresses.get(0);

                            String locality = address.getLocality();       // Ville (ex: "Antananarivo")
                            String subLocality = address.getSubLocality(); // Quartier (ex: "Analakely")
                            String thoroughfare = address.getThoroughfare(); // Rue (ex: "Rue Ratsimilaho")

                            // Construire une adresse lisible
                            if (locality != null && !locality.isEmpty()) {
                                if (locationText.length() > 0) locationText.append(", ");
                                locationText.append(locality);
                            }
                            if (subLocality != null && !subLocality.isEmpty()) {
                                if (locationText.length() > 0) locationText.append(", ");
                                locationText.append(subLocality);
                            }
                            if (thoroughfare != null && !thoroughfare.isEmpty()) {
                                if (locationText.length() > 0) locationText.append(", ");
                                locationText.append(thoroughfare);
                            }
                        }

                        // Si aucun texte n'a été généré (cas rare), utiliser "Position sélectionnée"
                        if (locationText.length() == 0) {
                            locationText.append("Position sélectionnée");
                        }

                        // Retourner le résultat à OrganizeActivity
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("selected_location", locationText.toString());
                        setResult(RESULT_OK, resultIntent);
                        finish();

                    } catch (IOException e) {
                        e.printStackTrace();
                        // En cas d'erreur, retourner le nom recherché ou "Position inconnue"
                        String fallbackText = (searchedPlaceName != null) ? searchedPlaceName : "Position inconnue";
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("selected_location", fallbackText);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }
                }
            }
        });
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    currentLocation = location;

                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    assert mapFragment != null;
                    mapFragment.getMapAsync(MapLocationActivity.this);
                } else {
                    Toast.makeText(MapLocationActivity.this, "Impossible d'obtenir la localisation actuelle", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;
        LatLng coordonne = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        myMap.addMarker(new MarkerOptions()
                .position(coordonne)
                .title("Votre position actuelle"));

        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordonne, 12f));

        mapSearch.setEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, int deviceId) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId);
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission de localisation acceptée", Toast.LENGTH_SHORT).show();
                getLastLocation();
            } else {
                Toast.makeText(this, "Permission de localisation refusée", Toast.LENGTH_SHORT).show();
            }
        }
    }
}