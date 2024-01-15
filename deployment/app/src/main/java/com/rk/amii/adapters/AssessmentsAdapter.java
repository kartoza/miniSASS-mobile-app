package com.rk.amii.adapters;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.rk.amii.database.DBHandler;
import com.rk.amii.models.AssessmentModel;
import com.rk.amii.models.PhotoModel;
import com.rk.amii.R;
import com.rk.amii.models.SitesModel;
import com.rk.amii.shared.Utils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;

public class AssessmentsAdapter extends RecyclerView.Adapter<AssessmentsAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<AssessmentModel> assessments;
    private PhotoAdapter photoAdapter;
    private DBHandler dbHandler;

    public AssessmentsAdapter(Context context, ArrayList<AssessmentModel> assessments) {
        this.context = context;
        this.assessments = assessments;
    }

    @NonNull
    @Override
    public AssessmentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AssessmentsAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.assessment_item,
                parent, false));
    }

    /**
     * Bind view holder
     * @param holder
     * @param position
     */
    public void onBindViewHolder(@NonNull AssessmentsAdapter.ViewHolder holder, int position) {
        AssessmentModel modal = assessments.get(position);

        dbHandler = new DBHandler(context);

        Integer siteId = dbHandler.getSiteIdByAssessmentId(modal.getAssessmentId());
        SitesModel site = dbHandler.getSiteById(siteId);

        //TODO get real online site id
        if (site != null) {
            holder.miniSassScore.setText("User score: " + String.format("%.2f", modal.getMiniSassScore()));
            holder.miniSassMLScore.setText("ML score: " + String.format("%.2f", modal.getMiniSassMLScore()));
            holder.userCondition.setText("Condition: " + Utils.calculateCondition(modal.getMiniSassScore(), site.getRiverType()));
            holder.mlCondition.setText("Condition: " + Utils.calculateCondition(modal.getMiniSassMLScore(), site.getRiverType()));
        } else {
            holder.miniSassScore.setText("User score: " + String.format("%.2f", modal.getMiniSassScore()));
            holder.miniSassMLScore.setText("ML score: " + String.format("%.2f", modal.getMiniSassMLScore()));
            holder.userCondition.setText("Condition: " + Utils.calculateCondition(modal.getMiniSassScore(), "sandy"));
            holder.mlCondition.setText("Condition: " + Utils.calculateCondition(modal.getMiniSassMLScore(), "sandy"));
        }

        if (!TextUtils.isEmpty(modal.getWaterTemp())) {
            holder.waterTemp.setText(Html.fromHtml("Water temperature: <b>" + modal.getWaterTemp() + " °C</b>", Html.FROM_HTML_MODE_LEGACY));
        } else {
            holder.waterTemp.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(modal.getWaterClarity())) {
            holder.waterClarity.setText(Html.fromHtml("Water clarity: <b>" + modal.getWaterClarity() + "</b>", Html.FROM_HTML_MODE_LEGACY));
        } else {
            holder.waterClarity.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(modal.getDissolvedOxygen())) {
            holder.dissolvedOxygen.setText(Html.fromHtml("Dissolved oxygen: <b>" + modal.getDissolvedOxygen() + " " + modal.getDissolvedOxygenUnit() + "</b>", Html.FROM_HTML_MODE_LEGACY));
        } else {
            holder.dissolvedOxygen.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(modal.getElectricalConductivity())) {
            holder.electricalConductivity.setText(Html.fromHtml("Electrical conductivity: <b>" + modal.getElectricalConductivity() + " " + modal.getElectricalConductivityUnit() + "</b>", Html.FROM_HTML_MODE_LEGACY));
        } else {
            holder.electricalConductivity.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(modal.getPh())) {
            holder.ph.setText(Html.fromHtml("ph: <b>" + modal.getPh() + "</b>", Html.FROM_HTML_MODE_LEGACY));
        } else {
            holder.ph.setVisibility(View.GONE);
        }

        holder.note.setText(modal.getNotes());

        if (TextUtils.isEmpty(modal.getNotes())) {
            holder.toggleNoteContainer.setVisibility(View.GONE);
        }

        ArrayList<PhotoModel> photos = dbHandler.getPhotoInfo(modal.getAssessmentId());
        if (photos.size() > 0) {
            photoAdapter = new PhotoAdapter (context, photos);
            holder.photoView.setLayoutManager(new LinearLayoutManager(context));
            holder.photoView.setAdapter(photoAdapter);
        } else {
            holder.togglePhotosContainer.setVisibility(View.GONE);
        }

        holder.noteContainer.setVisibility(View.GONE);
        holder.photoContainer.setVisibility(View.GONE);

        holder.toggleNoteContainer.setOnClickListener(view -> {
            if (holder.noteContainer.getVisibility() != View.GONE) {
                holder.noteContainer.setVisibility(View.GONE);
                holder.toggleNote.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
            } else {
                holder.noteContainer.setVisibility(View.VISIBLE);
                holder.toggleNote.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
            }
        });

        holder.toggleNote.setOnClickListener(view -> {
            if (holder.noteContainer.getVisibility() != View.GONE) {
                holder.noteContainer.setVisibility(View.GONE);
                holder.toggleNote.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
            } else {
                holder.noteContainer.setVisibility(View.VISIBLE);
                holder.toggleNote.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
            }
        });

        holder.togglePhotosContainer.setOnClickListener(view -> {
            if (holder.photoContainer.getVisibility() != View.GONE) {
                holder.photoContainer.setVisibility(View.GONE);
                holder.togglePhotos.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
            } else {
                holder.photoContainer.setVisibility(View.VISIBLE);
                holder.togglePhotos.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
            }
        });

        holder.togglePhotos.setOnClickListener(view -> {
            if (holder.photoContainer.getVisibility() != View.GONE) {
                holder.photoContainer.setVisibility(View.GONE);
                holder.togglePhotos.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
            } else {
                holder.photoContainer.setVisibility(View.VISIBLE);
                holder.togglePhotos.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
            }
        });
    }

    /**
     * Get the number of assessments
     * @return number of assessments
     */
    public int getItemCount() {
        return assessments.size();
    }

    /**
     * Set view holder values
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView miniSassScore;
        private final TextView miniSassMLScore;
        private final TextView userCondition;
        private final TextView mlCondition;
        private final RecyclerView photoView;
        private final ImageButton togglePhotos;
        private final LinearLayout togglePhotosContainer;
        private final LinearLayout photoContainer;

        private final ImageButton toggleNote;
        private final LinearLayout toggleNoteContainer;
        private final LinearLayout noteContainer;

        private final TextView ph;
        private final TextView waterTemp;
        private final TextView dissolvedOxygen;
        private final TextView electricalConductivity;
        private final TextView waterClarity;
        private final TextView note;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            miniSassScore = itemView.findViewById(R.id.idMiniSassScore);
            miniSassMLScore = itemView.findViewById(R.id.idMiniSassMLScore);
            userCondition = itemView.findViewById(R.id.idUserCondition);
            mlCondition = itemView.findViewById(R.id.idMLCondition);
            photoView = itemView.findViewById(R.id.rvPhotos);
            togglePhotosContainer = itemView.findViewById(R.id.idShowPhotosContainer);
            photoContainer = itemView.findViewById(R.id.idPhotoContainer);
            togglePhotos = itemView.findViewById(R.id.idShowPhotos);

            toggleNoteContainer = itemView.findViewById(R.id.idShowNoteContainer);
            noteContainer = itemView.findViewById(R.id.idNoteContainer);
            toggleNote = itemView.findViewById(R.id.idShowNote);

            ph = itemView.findViewById(R.id.idPh);
            waterTemp = itemView.findViewById(R.id.idWaterTemp);
            dissolvedOxygen = itemView.findViewById(R.id.idDissolvedOxygen);
            electricalConductivity = itemView.findViewById(R.id.idElectricalConductivity);
            waterClarity = itemView.findViewById(R.id.idWaterClarity);
            note = itemView.findViewById(R.id.idNote);
        }
    }

}
