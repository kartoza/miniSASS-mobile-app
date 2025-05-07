package com.rk.amii.ui.home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
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
import com.rk.amii.services.ApiService;
import com.rk.amii.shared.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private MapView map = null;
    private View view;
    private DBHandler dbHandler;
    int LOCATION_REFRESH_TIME = 5000;
    int LOCATION_REFRESH_DISTANCE = 500;
    LocationManager location_manager;
    private ArrayList<SitesModel> sites;
    private Button retryBtn;
    TextView mapMessage;
    private boolean isOnline = false;

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

        map = (MapView) view.findViewById(R.id.map);
        retryBtn = view.findViewById(R.id.onlineSitesRetry);
        mapMessage = view.findViewById(R.id.mapMessageText);

        if (!isOnline) {
            view.findViewById(R.id.idPBLoadingSites).setVisibility(View.GONE);
            mapMessage.setText("Could not fetch online sites. Connect your device to the internet if you want to view the online sites on the map.");
        }

        retryBtn.setOnClickListener(view -> {
            this.fetchOnlineSites();
        });

        map.setMinZoomLevel(3.0);

        location_manager = (LocationManager) HomeFragment.this.getContext().getSystemService(Context.LOCATION_SERVICE);
        location_manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE, mLocationListener);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
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
                return;
            }
        } catch(Exception e) {
            System.out.println("Could not determine if location services are enabled.");
        }

        /* Deprecated in API 26 */
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.post(() -> {
            GeoPoint geoPoint = new GeoPoint(-24.00, 24.029957);
            IMapController iMapController = map.getController();
            iMapController.setCenter(geoPoint);
            iMapController.setZoom(6.0);
        });

        this.addSiteMarkers(getSiteLocations(), "offline");

        if (isOnline) {
            this.fetchOnlineSites();
        }

    }

    private void fetchOnlineSites() {
        ApiService service = new ApiService(this.getActivity().getApplicationContext());

        // Get online sites and add markers to the map
        try {
            service.getSites(result -> {
                try {
                    JSONArray onlineSites = new JSONArray(result);

                    ArrayList<LocationPinModel> onlineMarkers = new ArrayList<>();

                    for (int i=0; i < onlineSites.length(); i++) {
                        String geom = onlineSites.getJSONObject(i).getString("the_geom");
                        Pattern pattern = Pattern.compile("\\((.*?)\\)");
                        Matcher matcher = pattern.matcher(geom);
                        if (matcher.find())
                        {
                            String[] point = matcher.group(1).split(" ");

                            onlineMarkers.add(new LocationPinModel(
                                    Double.parseDouble(point[1]),
                                    Double.parseDouble(point[0]),
                                    Utils.getStatusColor(0, "Sandy"),
                                    Integer.parseInt(onlineSites.getJSONObject(i).getString("gid"))));
                        }
                    }


                    this.addSiteMarkers(onlineMarkers, "online");
                    view.findViewById(R.id.mapMessageView).setVisibility(View.GONE);
                } catch (JSONException e) {
                    mapMessage = view.findViewById(R.id.mapMessageText);
                    mapMessage.setText("Could not fetch online sites");
                    view.findViewById(R.id.onlineSitesRetry).setVisibility(View.VISIBLE);
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            System.out.println("ERR: " + e);
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

    private void addSiteMarkers(ArrayList<LocationPinModel> siteLocations, String type) {
        RadiusMarkerClusterer clusterer = new RadiusMarkerClusterer(this.getContext());
        Drawable d = getResources().getDrawable(R.drawable.ic_map_cluster_40);
        clusterer.setIcon(drawableToBitmap(d));
        clusterer.getTextPaint().setColor(getResources().getColor(R.color.white));
        //clusterer.getTextPaint().setTextSize(20.0f);


        for (int i = 0; i < siteLocations.size(); i++) {

            LocationPinModel siteLocation = siteLocations.get(i);

            GeoPoint startPoint = new GeoPoint(siteLocation.getLatitude(), siteLocation.getLongitude());
            Marker startMarker = new Marker(map);

            Drawable unwrappedDrawable = AppCompatResources.getDrawable(this.getContext(), R.drawable.ic_crab_24);
            Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
            DrawableCompat.setTint(wrappedDrawable, Color.parseColor(siteLocation.getPinColor()));

            startMarker.setIcon(wrappedDrawable);
            startMarker.setPosition(startPoint);
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            clusterer.add(startMarker);

            dbHandler = new DBHandler(HomeFragment.this.getActivity());
            SitesModel currentSite = dbHandler.getSiteById(siteLocation.getPinId());

            if (type == "offline") {
                startMarker.setOnMarkerClickListener((marker, mapView) -> {
                    Log.i("INFO", "SiteID: " + currentSite.getSiteId());
                    Intent intent = new Intent(HomeFragment.this.getContext(), SiteDetailActivity.class);
                    intent.putExtra("siteId", currentSite.getSiteId().toString());
                    intent.putExtra("type", type);

                    HomeFragment.this.getContext().startActivity(intent);
                    return false;
                });
            } else if (type == "online") {
                startMarker.setOnMarkerClickListener((marker, mapView) -> {
                    /*Bundle args = new Bundle();
                    args.putString("siteId", siteLocation.getPinId().toString());
                    OnlineSiteDialog dialog = new OnlineSiteDialog();
                    dialog.setArguments(args);
                    dialog.show(getFragmentManager(), "online_site_dialog");
                    return false;*/
                    Intent intent = new Intent(HomeFragment.this.getContext(), SiteDetailActivity.class);
                    intent.putExtra("siteId", siteLocation.getPinId().toString());
                    intent.putExtra("type", type);

                    HomeFragment.this.getContext().startActivity(intent);
                    return false;
                });
            }
        }
        map.getOverlays().add(clusterer);
        IGeoPoint center = map.getMapCenter();
        map.getController().setCenter(center);
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.setTint(Color.parseColor("#539987"));
        drawable.draw(canvas);

        return bitmap;
    }


    @Override
    public void onResume() {
        super.onResume();
        isOnline = Utils.isNetworkAvailable(getContext());
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}