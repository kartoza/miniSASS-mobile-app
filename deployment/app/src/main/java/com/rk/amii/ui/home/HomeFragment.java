package com.rk.amii.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.expressions.Expression;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.rk.amii.R;
import com.rk.amii.activities.SiteDetailActivity;
import com.rk.amii.database.DBHandler;
import com.rk.amii.databinding.FragmentHomeBinding;
import com.rk.amii.models.AssessmentModel;
import com.rk.amii.models.LocationPinModel;
import com.rk.amii.models.SitesModel;
import com.rk.amii.shared.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
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
        "        \"http://192.168.1.7:7800/tiles/public.minisass_observations/{z}/{x}/{y}.pbf\"\n" +
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
        "  ]\n" +
        "}";


    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();

            // Update map camera and location marker if mapboxMap is initialized
            if (mapboxMap != null) {
                // Update camera position
                mapboxMap.setCameraPosition(new CameraPosition.Builder()
                        .target(new LatLng(lat, lng))
                        .zoom(mapboxMap.getCameraPosition().zoom)  // Maintain current zoom level
                        .build());

                // Update the location marker
                Style style = mapboxMap.getStyle();
                if (style != null && style.getSource("current-location-source") != null) {
                    GeoJsonSource locationSource = (GeoJsonSource) style.getSource("current-location-source");
                    if (locationSource != null) {
                        locationSource.setGeoJson(Point.fromLngLat(lng, lat));
                    }
                }

                Log.i("Location", "Updated camera and marker to new location: " + lat + ", " + lng);
            }
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

        location_manager = (LocationManager) HomeFragment.this.getContext().getSystemService(Context.LOCATION_SERVICE);
        location_manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE, mLocationListener);

        return view;
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;

        // Load the map style
        if (isOnline) {
            // Create a style with OpenStreetMap raster tiles and MiniSASS vector tiles
            String osmStyleJson = "{\n" +
                    "  \"version\": 8,\n" +
                    "  \"sources\": {\n" +
                    "    \"osm\": {\n" +
                    "      \"type\": \"raster\",\n" +
                    "      \"tiles\": [\"https://a.tile.openstreetmap.org/{z}/{x}/{y}.png\"],\n" +
                    "      \"tileSize\": 256,\n" +
                    "      \"attribution\": \"&copy; OpenStreetMap Contributors\",\n" +
                    "      \"maxzoom\": 19\n" +
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

                // Setup click listener for features
                setupFeatureClickListener();

                // Add offline sites
                addOfflineSites(style);
            });
        } else {
            // Load a basic style for offline mode
            mapboxMap.setStyle(new Style.Builder().fromUri("https://demotiles.maplibre.org/style.json"), style -> {
                addOfflineSites(style);
            });
        }

        // laod map
        loadMapStyle();

        // Set camera position to current location if available
        setMapCameraToCurrentLocation();
    }

    private void addMiniSASSLayer(Style style, String layerId, String sourceLayer, Expression[] filters,
                                  String iconImage, float iconSize) {
        // Check if layer already exists
        if (style.getLayer(layerId) != null) {
            Log.i("MapStyle", "Layer " + layerId + " already exists, skipping");
            return;
        }

        // Create filter expression
        Expression filterExpr = Expression.all(filters);

        // Add a symbol layer for this category
        SymbolLayer layer = new SymbolLayer(layerId, "MiniSASS Observations");
        layer.setSourceLayer(sourceLayer);
        layer.setFilter(filterExpr);

        layer.withProperties(
                PropertyFactory.iconImage(iconImage),
                PropertyFactory.iconSize(iconSize),
                PropertyFactory.iconAllowOverlap(true)
        );

        style.addLayer(layer);
        Log.i("MapStyle", "Added layer: " + layerId);
    }


    private void loadMapStyle() {
        try {
            // Hide loading indicator and message
            view.findViewById(R.id.idPBLoadingSites).setVisibility(View.GONE);
            view.findViewById(R.id.mapMessageView).setVisibility(View.GONE);

            // Note: In a production app, you might want to load this from a file or URL
            mapboxMap.setStyle(new Style.Builder().fromJson(STYLE_JSON), style -> {
                // Style loaded successfully
                Log.i("MapStyle", "Map style loaded successfully");

                debugVectorTiles();

//                // Add default icons programmatically
//                Drawable crabDrawable = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_crab_24);
//                if (crabDrawable != null) {
//                    Bitmap crabBitmap = drawableToBitmap(crabDrawable);
//                    style.addImage("crab_u", crabBitmap);
//
//                    // Create a red version for "dirty" status
//                    Drawable crabDirtyDrawable = DrawableCompat.wrap(crabDrawable.mutate());
//                    DrawableCompat.setTint(crabDirtyDrawable, Color.RED);
//                    Bitmap crabDirtyBitmap = drawableToBitmap(crabDirtyDrawable);
//                    style.addImage("crab_u_dirty", crabDirtyBitmap);
//
//                    Log.d("MapStyle", "Added custom crab icons");
//                } else {
//                    Log.e("MapStyle", "Could not load crab drawable");
//                }

//                 Setup click listener for features
                setupFeatureClickListener();

//                 Add offline sites if needed
                addOfflineSites(style);
            });
        } catch (Exception e) {
            Log.e("MapStyle", "Error loading map style: " + e.getMessage());
            mapMessage.setText("Could not load map style. Please try again.");
            view.findViewById(R.id.onlineSitesRetry).setVisibility(View.VISIBLE);
        }
    }

    // Helper method to convert drawable to bitmap
    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    private void setupFeatureClickListener() {
        mapboxMap.addOnMapClickListener(point -> {
            // Query features at the clicked point
            PointF screenPoint = mapboxMap.getProjection().toScreenLocation(point);

            // Create a small rectangle around the clicked point to make selection easier
            RectF clickRect = new RectF(
                    screenPoint.x - 20, screenPoint.y - 20,
                    screenPoint.x + 20, screenPoint.y + 20);

            // Query the specific debug layer you've created
            List<Feature> features = mapboxMap.queryRenderedFeatures(clickRect, "debug-layer");

            Log.d("FeatureClick", "Clicked at point: " + point.getLatitude() + "," + point.getLongitude());
            Log.d("FeatureClick", "Found " + features.size() + " features");


            if (!features.isEmpty()) {
                Feature feature = features.get(0);
                Log.d("FeatureClick", "Selected feature properties:");

                // Log all properties to see what's available
                for (String key : feature.properties().keySet()) {
                    Log.d("FeatureClick", "  " + key + ": " + feature.getProperty(key).getAsString());
                }

                // Get the properties as a string
                String propertiesString = feature.getStringProperty("properties");
                Log.d("FeatureClick", "Properties string: " + propertiesString);

                String siteId = "";
                if (propertiesString != null && !propertiesString.isEmpty()) {
                    try {
                        // Parse the properties string into a JSON object
                        JSONObject propertiesJson = new JSONObject(propertiesString);

                        // Try to get sites_id
                        if (propertiesJson.has("sites_id")) {
                            siteId = propertiesJson.getString("sites_id");
                            Log.d("FeatureClick", "Found sites_id: " + siteId);
                        } else if (propertiesJson.has("sites_gid")) {
                            siteId = propertiesJson.getString("sites_gid");
                            Log.d("FeatureClick", "Found sites_gid: " + siteId);
                        } else if (propertiesJson.has("gid")) {
                            siteId = propertiesJson.getString("gid");
                            Log.d("FeatureClick", "Found gid: " + siteId);
                        } else {
                            // Log all available properties to help identify the correct one
                            Log.d("FeatureClick", "Available properties in JSON:");
                            Iterator<String> keys = propertiesJson.keys();
                            while (keys.hasNext()) {
                                String key = keys.next();
                                Log.d("FeatureClick", "  " + key + ": " + propertiesJson.get(key));
                            }
                        }
                    } catch (JSONException e) {
                        Log.e("FeatureClick", "Error parsing properties JSON", e);
                    }
                } else {
                    // If properties is not available as a string, try other approaches
                    Log.d("FeatureClick", "Properties string is null or empty");

                    // Try direct property access
                    if (feature.hasProperty("sites_id")) {
                        siteId = feature.getStringProperty("sites_id");
                    } else if (feature.hasProperty("sites_gid")) {
                        siteId = feature.getStringProperty("sites_gid");
                    } else if (feature.hasProperty("gid")) {
                        siteId = feature.getStringProperty("gid");
                    }
                }

                if (!siteId.isEmpty()) {
                    Log.d("FeatureClick", "Opening site detail for ID: " + siteId);

                    // Open site detail activity
                    Intent intent = new Intent(HomeFragment.this.getContext(), SiteDetailActivity.class);
                    intent.putExtra("siteId", siteId);
                    intent.putExtra("type", "online");
                    HomeFragment.this.getContext().startActivity(intent);

                    return true;
                }

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

    @SuppressLint("MissingPermission")
    private void setMapCameraToCurrentLocation() {
        // Try to get last known location
        Location lastKnownLocation = null;
        try {
            lastKnownLocation = location_manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation == null) {
                lastKnownLocation = location_manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        } catch (Exception e) {
            Log.e("Location", "Error getting last known location", e);
        }

        if (lastKnownLocation != null) {
            // Use the last known location
            final LatLng currentLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

            mapboxMap.setCameraPosition(new CameraPosition.Builder()
                    .target(currentLocation)
                    .zoom(12)  // Closer zoom for user's location
                    .build());
            Log.i("Location", "Set camera to current location: " +
                    lastKnownLocation.getLatitude() + ", " + lastKnownLocation.getLongitude());

            // Add a blue circle to mark the current location
            Style style = mapboxMap.getStyle();
            if (style != null) {
                // Remove existing location marker if any
                if (style.getSource("current-location-source") != null) {
                    style.removeLayer("current-location-circle");
                    style.removeSource("current-location-source");
                }

                // Create a GeoJSON source with the current location
                GeoJsonSource locationSource = new GeoJsonSource("current-location-source",
                        Feature.fromGeometry(Point.fromLngLat(
                                currentLocation.getLongitude(),
                                currentLocation.getLatitude())));
                style.addSource(locationSource);

                // Add a circle layer to represent the location
                CircleLayer locationCircle = new CircleLayer("current-location-circle", "current-location-source");
                locationCircle.withProperties(
                        PropertyFactory.circleColor(Color.BLUE),
                        PropertyFactory.circleRadius(8f),
                        PropertyFactory.circleStrokeWidth(2f),
                        PropertyFactory.circleStrokeColor(Color.WHITE),
                        PropertyFactory.circleOpacity(0.8f)
                );
                style.addLayer(locationCircle);
            }
        } else {
            // Fall back to South Africa if no location is available
            mapboxMap.setCameraPosition(new CameraPosition.Builder()
                    .target(new LatLng(-28.15, 25.2))
                    .zoom(5)
                    .build());
            Log.i("Location", "No location available, using default position");
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

    private void debugVectorTiles() {
        // Add a debug layer that shows all features from the vector source
        CircleLayer debugLayer = new CircleLayer("debug-layer", "MiniSASS Observations");
        debugLayer.setSourceLayer("public.minisass_observations");
        debugLayer.withProperties(
                PropertyFactory.circleColor(Color.RED),
                PropertyFactory.circleRadius(10f),
                PropertyFactory.circleOpacity(0.7f)
        );

        // Add the debug layer to the style
        mapboxMap.getStyle().addLayer(debugLayer);

        mapboxMap.addOnCameraIdleListener(() -> {
            Log.d("VectorTileDebug", "Camera idle, checking for features");
            List<Feature> features = mapboxMap.queryRenderedFeatures(
                    new RectF(0, 0, mapView.getWidth(), mapView.getHeight()),
                    "debug-layer"
            );
            Log.d("VectorTileDebug", "Found " + features.size() + " features in the current view");

            if (!features.isEmpty()) {
                Feature feature = features.get(0);
                Log.d("VectorTileDebug", "Sample feature properties:");
                for (String key : feature.properties().keySet()) {
                    Log.d("VectorTileDebug", "  " + key + ": " + feature.getProperty(key).getAsString());
                }
            }
            // Remove the return statement
        });
    }
}
