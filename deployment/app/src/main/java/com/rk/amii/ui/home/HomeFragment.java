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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
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
import com.rk.amii.models.UserModel;
import com.rk.amii.shared.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private ActivityResultLauncher<Intent> siteDetailLauncher;

    private String currentSiteId;
    private String currentSiteType;

    TextView mapMessage;
    private boolean isOnline = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the activity result launcher
        siteDetailLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Log.d("HomeFragment", "User returned from SiteDetailActivity");
                    Log.d("HomeFragment", "Site ID: " + currentSiteId);

                    // Handle the return with the site ID
                    onReturnFromSiteDetail();
                }
        );
    }

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

        // Setup map event listeners
        setupMapEventListeners();

        // Load the map style
        if (isOnline) {
            loadMapStyle();
        } else {
            // Load a basic style for offline mode
            mapboxMap.setStyle(new Style.Builder().fromUri("https://demotiles.maplibre.org/style.json"), style -> {
                addOfflineSites(style);
            });
        }

        // Set camera position to current location if available
        setMapCameraToCurrentLocation();
    }

    private void loadMapStyle() {
        try {
            // Hide loading indicator and message
            view.findViewById(R.id.idPBLoadingSites).setVisibility(View.GONE);
            view.findViewById(R.id.mapMessageView).setVisibility(View.GONE);

            // Create a simplified style JSON that doesn't include the layers
            String simplifiedStyleJson = "{\n" +
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
                    "    }\n" +
                    "  ]\n" +
                    "}";

            // Load the simplified style
            mapboxMap.setStyle(new Style.Builder().fromJson(simplifiedStyleJson), style -> {
                // Style loaded successfully
                Log.i("MapStyle", "Map style loaded successfully");

                // Now add the vector tile layers with our custom implementation
                addSiteLayers();

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

            String[] layerIds = {
                    "No invertebrates found - clean", "No invertebrates found - dirty",
                    "Seriously/critically modified (sandy) - clean", "Seriously/critically modified (sandy) - dirty",
                    "Seriously/critically modified (rocky) - clean", "Seriously/critically modified (rocky) - dirty",
                    "Largely modified (sandy) - clean", "Largely modified (sandy) - dirty",
                    "Largely modified(rocky) - clean", "Largely modified(rocky) - dirty",
                    "Moderately modified (sandy) - clean", "Moderately modified (sandy) - dirty",
                    "Moderately modified (rocky) - clean", "Moderately modified (rocky) - dirty",
                    "Largely natural/few modifications (sandy) - clean", "Largely natural/few modifications (sandy) - dirty",
                    "Largely natural/few modifications (rocky) - clean", "Largely natural/few modifications (rocky) - dirty",
                    "Unmodified (sandy) - clean", "Unmodified (sandy) - dirty",
                    "Unmodified (rocky) - clean", "Unmodified (rocky) - dirty",
                    "fallback-layer"
            };

            // Query all layers
            List<Feature> features = new ArrayList<>();
            for (String layerId : layerIds) {
                List<Feature> layerFeatures = mapboxMap.queryRenderedFeatures(clickRect, layerId);
                features.addAll(layerFeatures);
            }

            Log.d("FeatureClick", "Clicked at point: " + point.getLatitude() + "," + point.getLongitude());
            Log.d("FeatureClick", "Found " + features.size() + " features");

            if (!features.isEmpty()) {
                Feature feature = features.get(0);
                Log.d("FeatureClick", "Selected feature properties: " + feature.properties().toString());

                // Extract site ID from feature properties
                String siteId = null;

                // Try different property paths to find the site ID
                try {
                    // First try to get it directly from properties
                    if (feature.hasProperty("gid")) {
                        siteId = feature.getProperty("gid").getAsString();
                        Log.d("FeatureClick", "Found gid property: " + siteId);
                    }
                    else if (feature.hasProperty("sites_gid")) {
                        siteId = feature.getProperty("sites_gid").getAsString();
                        Log.d("FeatureClick", "Found sites_gid property: " + siteId);
                    }
                    else if (feature.hasProperty("id")) {
                        siteId = feature.getProperty("id").getAsString();
                        Log.d("FeatureClick", "Found id property: " + siteId);
                    }
                    // If we couldn't find it directly, try parsing the properties as JSON
                    else if (feature.hasProperty("properties")) {
                        String propertiesJson = feature.getProperty("properties").getAsString();
                        Log.d("FeatureClick", "Properties JSON: " + propertiesJson);

                        try {
                            JSONObject jsonObject = new JSONObject(propertiesJson);
                            if (jsonObject.has("gid")) {
                                siteId = jsonObject.getString("gid");
                                Log.d("FeatureClick", "Found gid in properties JSON: " + siteId);
                            } else if (jsonObject.has("sites_gid")) {
                                siteId = jsonObject.getString("sites_gid");
                                Log.d("FeatureClick", "Found sites_gid in properties JSON: " + siteId);
                            } else if (jsonObject.has("id")) {
                                siteId = jsonObject.getString("id");
                                Log.d("FeatureClick", "Found id in properties JSON: " + siteId);
                            }
                        } catch (JSONException e) {
                            Log.e("FeatureClick", "Error parsing properties JSON", e);
                        }
                    }

                    // If we still don't have a site ID, log all properties to help debug
                    if (siteId == null) {
                        Log.d("FeatureClick", "Could not find site ID. All properties:");
                        for (String key : feature.properties().keySet()) {
                            Log.d("FeatureClick", "  " + key + ": " + feature.getProperty(key).getAsString());
                        }
                    }
                } catch (Exception e) {
                    Log.e("FeatureClick", "Error extracting site ID", e);
                }

                // If we found a site ID, open the site detail activity
                if (siteId != null) {
                    Log.d("FeatureClick", "Opening site detail for ID: " + siteId);

                    // Store the site ID
                    currentSiteId = siteId;
                    currentSiteType = "online";

                    Intent intent = new Intent(HomeFragment.this.getContext(), SiteDetailActivity.class);
                    intent.putExtra("siteId", siteId);
                    intent.putExtra("type", "online");

                    // Use the launcher
                    siteDetailLauncher.launch(intent);

                    return true;
                }


                return true; // Return true even if we couldn't find a site ID, to indicate we handled the click
            }

            return false; // No features found at the clicked location
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
                                    assessment.getCollectorsName(),
                                    assessment.getOrganisation(),
                                    assessment.getObservationDate(),
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

    private void addSiteLayers() {
        Style style = mapboxMap.getStyle();
        if (style == null) {
            Log.e("VectorTileDebug", "Style is null, cannot add layers");
            return;
        }

        // First, check if layers already exist and remove them
        removeExistingLayers(style);

        // Add all the crab icons to the style
        addCrabIcons(style);

        // Add all the different crab layers based on river category, score, and flag

        // 1. No invertebrates found (score = 0)
        addSymbolLayer(style, "No invertebrates found - clean", "MiniSASS Observations",
                Expression.all(
                        Expression.eq(Expression.get("score"), Expression.literal("0")),
                        Expression.eq(Expression.get("flag"), Expression.literal("clean"))
                ),
                "crab_u");

        addSymbolLayer(style, "No invertebrates found - dirty", "MiniSASS Observations",
                Expression.all(
                        Expression.eq(Expression.get("score"), Expression.literal("0")),
                        Expression.eq(Expression.get("flag"), Expression.literal("dirty"))
                ),
                "crab_u_dirty");

        // 2. Seriously/critically modified (sandy) - score <= 4.8
        addSymbolLayer(style, "Seriously/critically modified (sandy) - clean", "MiniSASS Observations",
                Expression.all(
                        Expression.eq(Expression.get("river_cat"), Expression.literal("sandy")),
                        Expression.lte(Expression.toNumber(Expression.get("score")), Expression.literal(4.8)),
                        Expression.eq(Expression.get("flag"), Expression.literal("clean"))
                ),
                "crab_sm");

        addSymbolLayer(style, "Seriously/critically modified (sandy) - dirty", "MiniSASS Observations",
                Expression.all(
                        Expression.eq(Expression.get("river_cat"), Expression.literal("sandy")),
                        Expression.lte(Expression.toNumber(Expression.get("score")), Expression.literal(4.8)),
                        Expression.eq(Expression.get("flag"), Expression.literal("dirty"))
                ),
                "crab_sm_dirty");

        // 3. Seriously/critically modified (rocky) - score <= 5.3
        addSymbolLayer(style, "Seriously/critically modified (rocky) - clean", "MiniSASS Observations",
                Expression.all(
                        Expression.eq(Expression.get("river_cat"), Expression.literal("rocky")),
                        Expression.lte(Expression.toNumber(Expression.get("score")), Expression.literal(5.3)),
                        Expression.eq(Expression.get("flag"), Expression.literal("clean"))
                ),
                "crab_sm");

        addSymbolLayer(style, "Seriously/critically modified (rocky) - dirty", "MiniSASS Observations",
                Expression.all(
                        Expression.eq(Expression.get("river_cat"), Expression.literal("rocky")),
                        Expression.lte(Expression.toNumber(Expression.get("score")), Expression.literal(5.3)),
                        Expression.eq(Expression.get("flag"), Expression.literal("dirty"))
                ),
                "crab_sm_dirty");

        // 4. Largely modified (sandy) - score between 4.8 and 5.3
        addSymbolLayer(style, "Largely modified (sandy) - clean", "MiniSASS Observations",
                Expression.all(
                        Expression.eq(Expression.get("river_cat"), Expression.literal("sandy")),
                        Expression.lte(Expression.toNumber(Expression.get("score")), Expression.literal(5.3)),
                        Expression.gt(Expression.toNumber(Expression.get("score")), Expression.literal(4.8)),
                        Expression.eq(Expression.get("flag"), Expression.literal("clean"))
                ),
                "crab_p");

        addSymbolLayer(style, "Largely modified (sandy) - dirty", "MiniSASS Observations",
                Expression.all(
                        Expression.eq(Expression.get("river_cat"), Expression.literal("sandy")),
                        Expression.lte(Expression.toNumber(Expression.get("score")), Expression.literal(5.3)),
                        Expression.gt(Expression.toNumber(Expression.get("score")), Expression.literal(4.8)),
                        Expression.eq(Expression.get("flag"), Expression.literal("dirty"))
                ),
                "crab_p_dirty");

        // 5. Largely modified (rocky) - score between 5.3 and 5.6
        addSymbolLayer(style, "Largely modified(rocky) - clean", "MiniSASS Observations",
                Expression.all(
                        Expression.eq(Expression.get("river_cat"), Expression.literal("rocky")),
                        Expression.lte(Expression.toNumber(Expression.get("score")), Expression.literal(5.6)),
                        Expression.gt(Expression.toNumber(Expression.get("score")), Expression.literal(5.3)),
                        Expression.eq(Expression.get("flag"), Expression.literal("clean"))
                ),
                "crab_p");

        addSymbolLayer(style, "Largely modified(rocky) - dirty", "MiniSASS Observations",
                Expression.all(
                        Expression.eq(Expression.get("river_cat"), Expression.literal("rocky")),
                        Expression.lte(Expression.toNumber(Expression.get("score")), Expression.literal(5.6)),
                        Expression.gt(Expression.toNumber(Expression.get("score")), Expression.literal(5.3)),
                        Expression.eq(Expression.get("flag"), Expression.literal("dirty"))
                ),
                "crab_p_dirty");

        // 6. Moderately modified (sandy) - score between 5.3 and 5.8
        addSymbolLayer(style, "Moderately modified (sandy) - clean", "MiniSASS Observations",
                Expression.all(
                        Expression.eq(Expression.get("river_cat"), Expression.literal("sandy")),
                        Expression.lte(Expression.toNumber(Expression.get("score")), Expression.literal(5.8)),
                        Expression.gt(Expression.toNumber(Expression.get("score")), Expression.literal(5.3)),
                        Expression.eq(Expression.get("flag"), Expression.literal("clean"))
                ),
                "crab_f");

        addSymbolLayer(style, "Moderately modified (sandy) - dirty", "MiniSASS Observations",
                Expression.all(
                        Expression.eq(Expression.get("river_cat"), Expression.literal("sandy")),
                        Expression.lte(Expression.toNumber(Expression.get("score")), Expression.literal(5.8)),
                        Expression.gt(Expression.toNumber(Expression.get("score")), Expression.literal(5.3)),
                        Expression.eq(Expression.get("flag"), Expression.literal("dirty"))
                ),
                "crab_f_dirty");

        // 7. Moderately modified (rocky) - score between 5.6 and 6.1
        addSymbolLayer(style, "Moderately modified (rocky) - clean", "MiniSASS Observations",
                Expression.all(
                        Expression.eq(Expression.get("river_cat"), Expression.literal("rocky")),
                        Expression.lte(Expression.toNumber(Expression.get("score")), Expression.literal(6.1)),
                        Expression.gt(Expression.toNumber(Expression.get("score")), Expression.literal(5.6)),
                        Expression.eq(Expression.get("flag"), Expression.literal("clean"))
                ),
                "crab_f");

        addSymbolLayer(style, "Moderately modified (rocky) - dirty", "MiniSASS Observations",
                Expression.all(
                        Expression.eq(Expression.get("river_cat"), Expression.literal("rocky")),
                        Expression.lte(Expression.toNumber(Expression.get("score")), Expression.literal(6.1)),
                        Expression.gt(Expression.toNumber(Expression.get("score")), Expression.literal(5.6)),
                        Expression.eq(Expression.get("flag"), Expression.literal("dirty"))
                ),
                "crab_f_dirty");

        // 8. Largely natural/few modifications (sandy) - score between 5.8 and 6.8
        addSymbolLayer(style, "Largely natural/few modifications (sandy) - clean", "MiniSASS Observations",
                Expression.all(
                        Expression.eq(Expression.get("river_cat"), Expression.literal("sandy")),
                        Expression.lte(Expression.toNumber(Expression.get("score")), Expression.literal(6.8)),
                        Expression.gt(Expression.toNumber(Expression.get("score")), Expression.literal(5.8)),
                        Expression.eq(Expression.get("flag"), Expression.literal("clean"))
                ),
                "crab_g");

        addSymbolLayer(style, "Largely natural/few modifications (sandy) - dirty", "MiniSASS Observations",
                Expression.all(
                        Expression.eq(Expression.get("river_cat"), Expression.literal("sandy")),
                        Expression.lte(Expression.toNumber(Expression.get("score")), Expression.literal(6.8)),
                        Expression.gt(Expression.toNumber(Expression.get("score")), Expression.literal(5.8)),
                        Expression.eq(Expression.get("flag"), Expression.literal("dirty"))
                ),
                "crab_g_dirty");

        // 9. Largely natural/few modifications (rocky) - score between 6.1 and 7.2
        addSymbolLayer(style, "Largely natural/few modifications (rocky) - clean", "MiniSASS Observations",
                Expression.all(
                        Expression.eq(Expression.get("river_cat"), Expression.literal("rocky")),
                        Expression.lte(Expression.toNumber(Expression.get("score")), Expression.literal(7.2)),
                        Expression.gt(Expression.toNumber(Expression.get("score")), Expression.literal(6.1)),
                        Expression.eq(Expression.get("flag"), Expression.literal("clean"))
                ),
                "crab_g");

        addSymbolLayer(style, "Largely natural/few modifications (rocky) - dirty", "MiniSASS Observations",
                Expression.all(
                        Expression.eq(Expression.get("river_cat"), Expression.literal("rocky")),
                        Expression.lte(Expression.toNumber(Expression.get("score")), Expression.literal(7.2)),
                        Expression.gt(Expression.toNumber(Expression.get("score")), Expression.literal(6.1)),
                        Expression.eq(Expression.get("flag"), Expression.literal("dirty"))
                ),
                "crab_g_dirty");

        // 10. Unmodified (sandy) - score > 6.8
        addSymbolLayer(style, "Unmodified (sandy) - clean", "MiniSASS Observations",
                Expression.all(
                        Expression.eq(Expression.get("river_cat"), Expression.literal("sandy")),
                        Expression.gt(Expression.toNumber(Expression.get("score")), Expression.literal(6.8)),
                        Expression.eq(Expression.get("flag"), Expression.literal("clean"))
                ),
                "crab_n");

        addSymbolLayer(style, "Unmodified (sandy) - dirty", "MiniSASS Observations",
                Expression.all(
                        Expression.eq(Expression.get("river_cat"), Expression.literal("sandy")),
                        Expression.gt(Expression.toNumber(Expression.get("score")), Expression.literal(6.8)),
                        Expression.eq(Expression.get("flag"), Expression.literal("dirty"))
                ),
                "crab_n_dirty");

        // 11. Unmodified (rocky) - score > 7.2
        addSymbolLayer(style, "Unmodified (rocky) - clean", "MiniSASS Observations",
                Expression.all(
                        Expression.eq(Expression.get("river_cat"), Expression.literal("rocky")),
                        Expression.gt(Expression.toNumber(Expression.get("score")), Expression.literal(7.2)),
                        Expression.eq(Expression.get("flag"), Expression.literal("clean"))
                ),
                "crab_n");

        addSymbolLayer(style, "Unmodified (rocky) - dirty", "MiniSASS Observations",
                Expression.all(
                        Expression.eq(Expression.get("river_cat"), Expression.literal("rocky")),
                        Expression.gt(Expression.toNumber(Expression.get("score")), Expression.literal(7.2)),
                        Expression.eq(Expression.get("flag"), Expression.literal("dirty"))
                ),
                "crab_n_dirty");
    }

    // Helper method to remove existing layers
    private void removeExistingLayers(Style style) {
        // List of layer IDs to check and remove
        String[] layerIds = {
                "No invertebrates found - clean", "No invertebrates found - dirty",
                "Seriously/critically modified (sandy) - clean", "Seriously/critically modified (sandy) - dirty",
                "Seriously/critically modified (rocky) - clean", "Seriously/critically modified (rocky) - dirty",
                "Largely modified (sandy) - clean", "Largely modified (sandy) - dirty",
                "Largely modified(rocky) - clean", "Largely modified(rocky) - dirty",
                "Moderately modified (sandy) - clean", "Moderately modified (sandy) - dirty",
                "Moderately modified (rocky) - clean", "Moderately modified (rocky) - dirty",
                "Largely natural/few modifications (sandy) - clean", "Largely natural/few modifications (sandy) - dirty",
                "Largely natural/few modifications (rocky) - clean", "Largely natural/few modifications (rocky) - dirty",
                "Unmodified (sandy) - clean", "Unmodified (sandy) - dirty",
                "Unmodified (rocky) - clean", "Unmodified (rocky) - dirty",
        };

        for (String layerId : layerIds) {
            if (style.getLayer(layerId) != null) {
                style.removeLayer(layerId);
                Log.d("VectorTileDebug", "Removed existing layer: " + layerId);
            }
        }
    }

    // Helper method to add a symbol layer with the given filter and icon
    private void addSymbolLayer(Style style, String layerId, String sourceId, Expression filter, String iconImage) {
        try {
            // Create the layer with clustering enabled
            SymbolLayer layer = new SymbolLayer(layerId, sourceId);
            layer.setSourceLayer("public.minisass_observations");

            // Set properties with clustering considerations
            layer.withProperties(
                    PropertyFactory.iconImage(iconImage),
                    PropertyFactory.iconSize(
                            // Scale icons based on zoom level to reduce crowding
                            Expression.interpolate(
                                    Expression.exponential(1.5f),
                                    Expression.zoom(),
                                    Expression.stop(5, 0.8f),   // Smaller at low zoom
                                    Expression.stop(10, 1.2f),  // Medium at mid zoom
                                    Expression.stop(15, 1.8f)   // Larger at high zoom
                            )
                    ),
                    PropertyFactory.iconAllowOverlap(false), // Prevent overlap
                    PropertyFactory.iconIgnorePlacement(false), // Respect placement
                    PropertyFactory.iconOptional(true), // Allow icons to be hidden if crowded
                    // Add collision detection
                    PropertyFactory.iconPadding(2f),
                    // Adjust opacity based on zoom
                    PropertyFactory.iconOpacity(
                            Expression.interpolate(
                                    Expression.linear(),
                                    Expression.zoom(),
                                    Expression.stop(5, 0.7f),
                                    Expression.stop(10, 0.9f),
                                    Expression.stop(15, 1.0f)
                            )
                    )
            );

            // Apply the filter
            layer.setFilter(filter);
            style.addLayer(layer);

            Log.d("VectorTileDebug", "Added layer with clustering: " + layerId);
        } catch (Exception e) {
            Log.e("VectorTileDebug", "Error adding layer " + layerId + ": " + e.getMessage(), e);
        }
    }

    // Helper method to add all the crab icons to the style
    private void addCrabIcons(Style style) {
        // Create and add all the crab icons
        addCrabIcon(style, "crab_u", R.drawable.crab_u);
        addCrabIcon(style, "crab_u_dirty", R.drawable.crab_u_dirty);
        addCrabIcon(style, "crab_sm", R.drawable.crab_sm);
        addCrabIcon(style, "crab_sm_dirty", R.drawable.crab_sm_dirty);
        addCrabIcon(style, "crab_p", R.drawable.crab_p);
        addCrabIcon(style, "crab_p_dirty", R.drawable.crab_p_dirty);
        addCrabIcon(style, "crab_f", R.drawable.crab_f);
        addCrabIcon(style, "crab_f_dirty", R.drawable.crab_f_dirty);
        addCrabIcon(style, "crab_g", R.drawable.crab_g);
        addCrabIcon(style, "crab_g_dirty", R.drawable.crab_g_dirty);
        addCrabIcon(style, "crab_n", R.drawable.crab_n);
        addCrabIcon(style, "crab_n_dirty", R.drawable.crab_n_dirty);
    }

    // Helper method to create a crab icon
    private void addCrabIcon(Style style, String iconName, int drawableId) {
        Drawable drawable = AppCompatResources.getDrawable(requireContext(), drawableId);
        if (drawable != null) {
            Drawable wrappedDrawable = DrawableCompat.wrap(drawable.mutate());
            Bitmap bitmap = drawableToBitmap(wrappedDrawable);
            style.addImage(iconName, bitmap);
            Log.d("VectorTileDebug", "Added icon: " + iconName);
        } else {
            Log.e("VectorTileDebug", "Could not load drawable for icon: " + iconName);
        }
    }

    private void setupMapEventListeners() {
        try {
            // Add a listener for map clicks to manually trigger debugging
            mapboxMap.addOnMapClickListener(point -> {
                Log.d("MapDebug", "Map clicked at: " + point.getLatitude() + ", " + point.getLongitude());
                Style style = mapboxMap.getStyle();
                return false; // Return false to allow other click listeners to process the click
            });

            Log.d("MapDebug", "Map click listener set up successfully");
        } catch (Exception e) {
            Log.e("MapDebug", "Error setting up map event listeners: " + e.getMessage(), e);
        }
    }

    private void onReturnFromSiteDetail() {
        if (Objects.equals(currentSiteType, "online")) {
            SitesModel site = dbHandler.getSiteByOnlineId(Integer.parseInt(currentSiteId));
            dbHandler.deleteSite(String.valueOf(site.getSiteId()));
        }
    }
}
