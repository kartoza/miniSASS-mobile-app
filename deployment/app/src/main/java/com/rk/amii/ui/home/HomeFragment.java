package com.rk.amii.ui.home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.graphics.RectF;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import com.rk.amii.BuildConfig;
import com.rk.amii.R;
import com.rk.amii.activities.SiteDetailActivity;
import com.rk.amii.database.DBHandler;
import com.rk.amii.databinding.FragmentHomeBinding;
import com.rk.amii.models.AssessmentModel;
import com.rk.amii.models.LocationPinModel;
import com.rk.amii.models.SitesModel;
import com.rk.amii.shared.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
//import org.maplibre.gl.MapLibre;
//import org.maplibre.gl.camera.CameraPosition;
//import org.maplibre.gl.geometry.LatLng;
//import org.maplibre.gl.maps.MapView;
//import org.maplibre.gl.maps.MapboxMap;
//import org.maplibre.gl.maps.OnMapReadyCallback;
//import org.maplibre.gl.maps.Style;
//import org.maplibre.gl.style.layers.SymbolLayer;
//import org.maplibre.gl.style.sources.GeoJsonSource;
//import org.maplibre.gl.geojson.Feature;
//import org.maplibre.gl.geojson.FeatureCollection;
//import org.maplibre.gl.geojson.Point;


//import com.mapbox.mapboxsdk.Mapbox;
//import com.mapbox.mapboxsdk.camera.CameraPosition;
//import com.mapbox.mapboxsdk.geometry.LatLng;
//import com.mapbox.mapboxsdk.maps.MapView;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.VectorSource;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.style.expressions.Expression;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private FragmentHomeBinding binding;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private View view;
    private DBHandler dbHandler;
    int LOCATION_REFRESH_TIME = 5000;
    int LOCATION_REFRESH_DISTANCE = 500;
    LocationManager location_manager;
    private ArrayList<SitesModel> sites;
    private Button retryBtn;
    TextView mapMessage;
    private boolean isOnline = false;

    // Style constants
    private static final String STYLE_JSON = "{\n" +
            "  \"version\": 8,\n" +
            "  \"name\": \"miniSASS\",\n" +
            "  \"metadata\": {\"maputnik:renderer\": \"mbgljs\"},\n" +
            "  \"center\": [25.2, -28.15],\n" +
            "  \"zoom\": 5,\n" +
            "  \"sources\": {\n" +
            "    \"OSM tiles\": {\n" +
            "      \"type\": \"raster\",\n" +
            "      \"tiles\": [\"https://tile.openstreetmap.org/{z}/{x}/{y}.png\"],\n" +
            "      \"minzoom\": 0,\n" +
            "      \"maxzoom\": 24\n" +
            "    },\n" +
            "    \"MiniSASS Observations\": {\n" +
            "      \"type\": \"vector\",\n" +
            "      \"tiles\": [\n" +
            "        \"http://192.168.122.1:7800/tiles/public.minisass_observations/{z}/{x}/{y}.pbf\"\n" +
            "      ],\n" +
            "      \"minZoom\": 0,\n" +
            "      \"maxZoom\": 14\n" +
            "    }\n" +
            "  },\n" +
            "  \"sprite\": \"https://raw.githubusercontent.com/kartoza/miniSASS/main/django_project/webmapping/styles/icons/minisass_sprites_larger\",\n" +
            "  \"glyphs\": \"https://api.maptiler.com/fonts/{fontstack}/{range}.pbf?key=cc4PpmmWZP73LjU1nsw3\",\n" +
            "  \"layers\": [\n" +
            "    {\n" +
            "      \"id\": \"OSM Background\",\n" +
            "      \"type\": \"raster\",\n" +
            "      \"source\": \"OSM tiles\",\n" +
            "      \"layout\": {\"visibility\": \"visible\"},\n" +
            "      \"paint\": {\"raster-resampling\": \"linear\"}\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": \"No invertebrates found - dirty\",\n" +
            "      \"type\": \"symbol\",\n" +
            "      \"source\": \"MiniSASS Observations\",\n" +
            "      \"source-layer\": \"public.minisass_observations\",\n" +
            "      \"filter\": [\"all\", [\"==\", \"score\", \"0\"], [\"==\", \"flag\", \"dirty\"]],\n" +
            "      \"layout\": {\n" +
            "        \"text-field\": \"\",\n" +
            "        \"icon-image\": \"crab_u_dirty\",\n" +
            "        \"visibility\": \"visible\",\n" +
            "        \"icon-size\": {\"stops\": [[5, 0.5], [17, 2]]}\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": \"No invertebrates found - clean\",\n" +
            "      \"type\": \"symbol\",\n" +
            "      \"source\": \"MiniSASS Observations\",\n" +
            "      \"source-layer\": \"public.minisass_observations\",\n" +
            "      \"filter\": [\"all\", [\"==\", \"score\", \"0\"], [\"==\", \"flag\", \"clean\"]],\n" +
            "      \"layout\": {\n" +
            "        \"text-field\": \"\",\n" +
            "        \"icon-image\": \"crab_u\",\n" +
            "        \"visibility\": \"visible\",\n" +
            "        \"icon-size\": {\"stops\": [[5, 0.5], [17, 2]]}\n" +
            "      }\n" +
            "    }\n" +
            "    /* Additional layers omitted for brevity */\n" +
            "  ]\n" +
            "}";

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    public HomeFragment() {
    }

    @SuppressLint("MissingPermission")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        isOnline = Utils.isNetworkAvailable(this.getContext());

        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        retryBtn = view.findViewById(R.id.onlineSitesRetry);
        mapMessage = view.findViewById(R.id.mapMessageText);

        if (!isOnline) {
            view.findViewById(R.id.idPBLoadingSites).setVisibility(View.GONE);
            mapMessage.setText("Could not fetch online sites. Connect your device to the internet if you want to view the online sites on the map.");
        }

        retryBtn.setOnClickListener(view -> {
            if (mapboxMap != null) {
                loadMapStyle();
            }
        });

        location_manager = (LocationManager) HomeFragment.this.getContext().getSystemService(Context.LOCATION_SERVICE);
        location_manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE, mLocationListener);

        return view;
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;

        // Set initial camera position to South Africa
        mapboxMap.setCameraPosition(new CameraPosition.Builder()
                .target(new LatLng(-28.15, 25.2)) // Center from the style JSON
                .zoom(5) // Zoom from the style JSON
                .build());

        // Load the map style
        if (isOnline) {
            // Create a style with OpenStreetMap raster tiles
            String osmStyleJson = "{\n" +
                    "  \"version\": 8,\n" +
                    "  \"sources\": {\n" +
                    "    \"osm\": {\n" +
                    "      \"type\": \"raster\",\n" +
                    "      \"tiles\": [\"https://a.tile.openstreetmap.org/{z}/{x}/{y}.png\"],\n" +
                    "      \"tileSize\": 256,\n" +
                    "      \"attribution\": \"&copy; OpenStreetMap Contributors\",\n" +
                    "      \"maxzoom\": 19\n" +
                    "    },\n" +
                    "    \"MiniSASS Observations\": {\n" +
                    "      \"type\": \"vector\",\n" +
                    "      \"tiles\": [\n" +
                    "        \"https://minisass.org/tiles/public.minisass_observations/{z}/{x}/{y}.pbf\"\n" +
                    "      ],\n" +
                    "      \"minZoom\": 0,\n" +
                    "      \"maxZoom\": 14\n" +
                    "    }\n" +
                    "  },\n" +
                    "  \"layers\": [\n" +
                    "    {\n" +
                    "      \"id\": \"osm\",\n" +
                    "      \"type\": \"raster\",\n" +
                    "      \"source\": \"osm\"\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";

            mapboxMap.setStyle(new Style.Builder().fromJson(osmStyleJson), style -> {
                // Style loaded successfully
                Log.i("MapStyle", "OSM base map loaded successfully");

                // Add vector tile source and layers for miniSASS observations
                loadMiniSASSLayers(style);

                // Setup click listener for features
                setupFeatureClickListener();
            });
        } else {
            // Load a basic style for offline mode
            mapboxMap.setStyle(new Style.Builder().fromUri("https://demotiles.maplibre.org/style.json"), style -> {
                addOfflineSites(style);
            });
        }

        // Check if location services are enabled
        checkLocationServices();
    }

    private void loadMiniSASSLayers(Style style) {
        try {
            // Add vector source for miniSASS observations
            VectorSource miniSASSSource = new VectorSource(
                    "MiniSASS Observations",
                    "https://minisass.org/tiles/public.minisass_observations/{z}/{x}/{y}.pbf"
            );
            style.addSource(miniSASSSource);

            // Add a simple symbol layer for the observations
            SymbolLayer observationsLayer = new SymbolLayer("minisass-observations", "MiniSASS Observations");
            observationsLayer.setSourceLayer("public.minisass_observations");

            // Use a circle if you don't have the crab icon
            observationsLayer.withProperties(
                    PropertyFactory.iconImage("marker-15"),  // Use a default marker icon
                    PropertyFactory.iconSize(1.5f),
                    PropertyFactory.iconAllowOverlap(true)
            );

            style.addLayer(observationsLayer);

            Log.i("MapStyle", "MiniSASS layers added successfully");

            // Hide loading indicator and message
            view.findViewById(R.id.idPBLoadingSites).setVisibility(View.GONE);
            view.findViewById(R.id.mapMessageView).setVisibility(View.GONE);

        } catch (Exception e) {
            Log.e("MapStyle", "Error loading MiniSASS layers: " + e.getMessage());
            mapMessage.setText("Could not load online sites. Please try again.");
            view.findViewById(R.id.onlineSitesRetry).setVisibility(View.VISIBLE);
        }
    }

    private void loadMapStyle() {
        try {
            // Hide loading indicator and message
            view.findViewById(R.id.idPBLoadingSites).setVisibility(View.GONE);
            view.findViewById(R.id.mapMessageView).setVisibility(View.GONE);

            // Load style from JSON string
            // Note: In a production app, you might want to load this from a file or URL
            mapboxMap.setStyle(new Style.Builder().fromJson(STYLE_JSON), style -> {
                // Style loaded successfully
                Log.i("MapStyle", "Map style loaded successfully");

                // Setup click listener for features
                setupFeatureClickListener();

                // Add offline sites if needed
                addOfflineSites(style);
            });
        } catch (Exception e) {
            Log.e("MapStyle", "Error loading map style: " + e.getMessage());
            mapMessage.setText("Could not load map style. Please try again.");
            view.findViewById(R.id.onlineSitesRetry).setVisibility(View.VISIBLE);
        }
    }

    private void setupFeatureClickListener() {
        mapboxMap.addOnMapClickListener(point -> {
            // Query features at the clicked point
            PointF screenPoint = mapboxMap.getProjection().toScreenLocation(point);

            // Create a small rectangle around the clicked point to make selection easier
            RectF clickRect = new RectF(
                    screenPoint.x - 10, screenPoint.y - 10,
                    screenPoint.x + 10, screenPoint.y + 10);

            // Query all layers that start with our pattern
            List<Feature> features = mapboxMap.queryRenderedFeatures(clickRect,
                    (Expression.literal(true)));

            // Filter features to only include those from MiniSASS Observations source
            List<Feature> minisassFeatures = new ArrayList<>();
            for (Feature feature : features) {
                if (feature.getStringProperty("source") != null &&
                        feature.getStringProperty("source").equals("MiniSASS Observations")) {
                    minisassFeatures.add(feature);
                }
            }

            if (!minisassFeatures.isEmpty()) {
                Feature feature = minisassFeatures.get(0);

                // Extract site ID from feature properties
                String siteId = feature.getStringProperty("gid");

                // Open site detail activity
                Intent intent = new Intent(HomeFragment.this.getContext(), SiteDetailActivity.class);
                intent.putExtra("siteId", siteId);
                intent.putExtra("type", "online");
                HomeFragment.this.getContext().startActivity(intent);

                return true;
            }
            return false;
        });
    }

    private void addOfflineSites(Style style) {
        ArrayList<LocationPinModel> offlineSites = getSiteLocations();

        if (offlineSites.isEmpty()) {
            return;
        }

        // Create features from offline sites
        List<Feature> features = new ArrayList<>();
        for (LocationPinModel site : offlineSites) {
            Feature feature = Feature.fromGeometry(
                    Point.fromLngLat(site.getLongitude(), site.getLatitude()));
            feature.addStringProperty("id", String.valueOf(site.getPinId()));
            feature.addStringProperty("color", site.getPinColor());
            features.add(feature);
        }

        // Add source for offline sites
        GeoJsonSource offlineSource = new GeoJsonSource("offline-sites",
                FeatureCollection.fromFeatures(features));
        style.addSource(offlineSource);

        // Add layer for offline sites
        SymbolLayer offlineLayer = new SymbolLayer("offline-sites-layer", "offline-sites");
        offlineLayer.setProperties(
                PropertyFactory.iconImage("crab_u"), // Use a default icon
                PropertyFactory.iconSize(1.5f),
                PropertyFactory.iconAllowOverlap(true)
        );

        style.addLayer(offlineLayer);

        // Add click listener for offline sites
        mapboxMap.addOnMapClickListener(point -> {
            PointF screenPoint = mapboxMap.getProjection().toScreenLocation(point);
            List<Feature> clickedFeatures = mapboxMap.queryRenderedFeatures(
                    screenPoint, "offline-sites-layer");

            if (!clickedFeatures.isEmpty()) {
                Feature clickedFeature = clickedFeatures.get(0);
                String siteId = clickedFeature.getStringProperty("id");

                Intent intent = new Intent(HomeFragment.this.getContext(), SiteDetailActivity.class);
                intent.putExtra("siteId", siteId);
                intent.putExtra("type", "offline");
                HomeFragment.this.getContext().startActivity(intent);

                return true;
            }
            return false;
        });
    }

    private void checkLocationServices() {
        boolean location_enabled = false;
        try {
            location_enabled = location_manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!location_enabled) {
                new AlertDialog.Builder(HomeFragment.this.getContext())
                        .setTitle("Location")
                        .setMessage("Your location services seems to be turned off. Please turn on your location services to continue.")
                        .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_map)
                        .show();
            }
        } catch(Exception e) {
            System.out.println("Could not determine if location services are enabled.");
        }
    }

    private ArrayList<LocationPinModel> getSiteLocations() {
        ArrayList<LocationPinModel> siteLocations = new ArrayList<>();
        dbHandler = new DBHandler(HomeFragment.this.getActivity());
        sites = dbHandler.getSites();
        for (int i = 0; i < sites.size(); i++) {
            // TODO: VALIDATE correctly
            SitesModel site = sites.get(i);
            String statusColor;
            if (site.getSiteLocation().contains(",")) {
                Double lat = Double.parseDouble(site.getSiteLocation().split(",")[0]);
                Double lng = Double.parseDouble(site.getSiteLocation().split(",")[1]);

                System.out.println(lat + ","+lng);

                ArrayList<Integer> assessmentIds = dbHandler.getAssessmentIdsBySiteId((int)site.getSiteId());
                ArrayList<AssessmentModel> assessments = new ArrayList<>();

                System.out.println(assessmentIds);

                for (int j = 0; j < assessmentIds.size(); j++) {
                    AssessmentModel assessment = dbHandler.getAssessmentById(assessmentIds.get(j));
                    assessments.add(
                            new AssessmentModel(
                                    assessmentIds.get(j),
                                    assessment.getOnlineAssessmentId(),
                                    assessment.getMiniSassScore(),
                                    assessment.getMiniSassMLScore(),
                                    assessment.getNotes(),
                                    assessment.getPh(),
                                    assessment.getWaterTemp(),
                                    assessment.getDissolvedOxygen(),
                                    assessment.getDissolvedOxygenUnit(),
                                    assessment.getElectricalConductivity(),
                                    assessment.getElectricalConductivityUnit(),
                                    assessment.getWaterClarity()
                            )
                    );
                }

                if (assessments.size() > 0) {
                    statusColor = Utils.getStatusColor(assessments.get(0).getMiniSassScore(),
                            site.getRiverType());
                } else {
                    statusColor = Utils.getStatusColor(0, "Sandy");
                }

                siteLocations.add(new LocationPinModel(lat, lng, statusColor, site.getSiteId()));
            }
        }
        return siteLocations;
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        isOnline = Utils.isNetworkAvailable(getContext());
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
        binding = null;
    }
}
