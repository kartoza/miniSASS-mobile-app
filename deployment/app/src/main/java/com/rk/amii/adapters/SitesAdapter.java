package com.rk.amii.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.rk.amii.activities.ImageViewActivity;
import com.rk.amii.models.AssessmentModel;
import com.rk.amii.database.DBHandler;
import com.rk.amii.R;
import com.rk.amii.models.SitesModel;
import com.rk.amii.services.ApiService;
import com.rk.amii.shared.Utils;
import com.rk.amii.activities.EditSiteActivity;
import com.rk.amii.activities.SiteDetailActivity;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SitesAdapter extends RecyclerView.Adapter<SitesAdapter.ViewHolder> {

    private Context context;
    private ArrayList<SitesModel> sites;
    private DBHandler dbHandler;
    private ArrayList<AssessmentModel> assessments;
    private ArrayList<Integer> assessmentIds;
    private Bitmap bitmap;
    private boolean isOnline;

    public SitesAdapter(Context context, ArrayList<SitesModel> sites) {
        this.context = context;
        this.sites = sites;
    }

    @NonNull
    @Override
    public SitesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SitesAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.sites_item,
                parent, false));
    }

    /**
     * Bind view holder
     * @param holder
     * @param position
     */
    public void onBindViewHolder(@NonNull SitesAdapter.ViewHolder holder, int position) {
        SitesModel modal = sites.get(position);
        holder.siteName.setText(modal.getSiteName());
        holder.riverName.setText(modal.getRiverName());

        isOnline = Utils.isNetworkAvailable(context);

        dbHandler = new DBHandler(this.context);

        ArrayList<String> siteImagePaths = dbHandler.getSiteImagePathsBySiteId(modal.getSiteId());

        try {
            if (siteImagePaths.size() > 0) {
                Uri imageUri = Uri.fromFile(new File(siteImagePaths.get(0)));
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
                Matrix matrix = new Matrix();
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                holder.siteImage.setImageBitmap(bitmap);

                holder.siteImage.setOnClickListener(view -> {
                    Intent i = new Intent(context, ImageViewActivity.class);
                    i.putExtra("image", siteImagePaths.get(0));
                    context.startActivity(i);
                });
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        assessmentIds = dbHandler.getAssessmentIdsBySiteId((int)modal.getSiteId());
        assessments = new ArrayList<>();

        for (int i = 0; i < assessmentIds.size(); i++) {
            AssessmentModel assessment = dbHandler.getAssessmentById(assessmentIds.get(i));
            assessments.add(
                    new AssessmentModel(
                            assessmentIds.get(i),
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
            holder.siteStatus.setText(Utils.calculateCondition(assessments.get(0).getMiniSassScore(), modal.getRiverType()));

            holder.siteStatus.setTextColor(Color.parseColor(
                    Utils.getStatusColor(assessments.get(0).getMiniSassScore(), modal.getRiverType())));

            setTextViewDrawableColor(holder.crabStatus, Color.parseColor(
                    Utils.getStatusColor(assessments.get(0).getMiniSassScore(), modal.getRiverType())));
        }

        /**
         * Set the onclick listener for the view button of the site.
         * If the button is click the user will be taken to the site view.
         */
        holder.itemView.setOnClickListener(view -> {
            Log.i("INFO", "SiteID: " + modal.getSiteId());
            Intent intent = new Intent(context, SiteDetailActivity.class);
            intent.putExtra("siteId", modal.getSiteId().toString());
            intent.putExtra("type", "offline");
            context.startActivity(intent);
        });

        /**
         * Set the onclick listener for the edit button of the site.
         * If the button is click the user will be taken to the site edit view.
         */
        holder.edit.setOnClickListener(view -> {
            Intent intent = new Intent(context, EditSiteActivity.class);
            intent.putExtra("siteId", modal.getSiteId().toString());
            intent.putExtra("onlineSiteId", modal.getOnlineSiteId());
            context.startActivity(intent);
        });

        /**
         * Set the onclick listener for the delete button of the site.
         * If the button is click the user will be prompted with a message to confirm the
         * deletion of the site. If the user chooses "yes" the site will be removed.
         */
        holder.delete.setOnClickListener(view -> new AlertDialog.Builder(context)
                .setTitle(context.getResources().getString(R.string.delete_site))
                .setMessage(context.getResources().getString(R.string.are_you_sure_delete_site))
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    // Make sure the device is connected to the internet, otherwise the site can't be deleted
                    // from the device or online.
                    if (isOnline) {
                        long deleted = dbHandler.deleteSite(Integer.toString(modal.getSiteId()));
                        if (deleted != 0) {
                            final int position1 = holder.getAdapterPosition();
                            sites.remove(position1);
                            notifyItemRemoved(position1);
                            notifyItemRangeChanged(position1, sites.size());
                        }

                        //delete online site
                        try {
                            ApiService service = new ApiService(context);
                            boolean updated = service.deleteSiteById(String.valueOf(modal.getOnlineSiteId()));
                            System.out.println("DELETED: " + updated);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        new AlertDialog.Builder(context)
                                .setTitle(context.getResources().getString(R.string.could_not_delete_site))
                                .setMessage(context.getResources().getString(R.string.connect_device_to_internet_try_again))
                                .setNeutralButton(context.getResources().getString(R.string.ok), (dialog1, which1) -> {
                                })
                                .setIcon(R.drawable.ic_baseline_wifi_off_24)
                                .show();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show());

    }

    private void setTextViewDrawableColor(TextView textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
            }
        }
    }

    /**
     * Get the number of sites
     * @return number of sites
     */
    public int getItemCount() {
        return sites.size();
    }

    /**
     * Set view holder values
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView siteName;
        private TextView siteStatus;
        private TextView riverName;
        private ImageView siteImage;
        private ImageView edit;
        private ImageView delete;
        private TextView crabStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            siteName = itemView.findViewById(R.id.idSiteName);
            siteStatus = itemView.findViewById(R.id.idSiteStatus);
            riverName = itemView.findViewById(R.id.idCardRiverName);
            siteImage = itemView.findViewById(R.id.siteImage);
            edit = itemView.findViewById(R.id.idEditSite);
            delete = itemView.findViewById(R.id.idDeleteSite);
            crabStatus = itemView.findViewById(R.id.idCrabStatus);
        }
    }

}
