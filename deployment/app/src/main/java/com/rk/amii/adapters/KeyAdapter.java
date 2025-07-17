package com.rk.amii.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rk.amii.R;
import com.rk.amii.activities.ImageViewDescriptionActivity;
import com.rk.amii.models.FilterGroupModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class KeyAdapter extends RecyclerView.Adapter<KeyAdapter.ViewHolder> {

    private Context context;
    private ArrayList<FilterGroupModel> filterGroup;
    private ArrayList<FilterGroupModel> filterGroupCopy = new ArrayList<>();
    private CheckBox groupItemName;
    private ArrayList<String> selected = new ArrayList<>();
    public TextView likelyGroupText;
    public LinearLayout likelyGroups;
    private Activity activity;

    // Declare the arrays as instance variables but don't initialize them yet
    private String[] bugsAndBeetles;
    private String[] caddisflies;
    private String[] crabsAndShrimps;
    private String[] damselflies;
    private String[] dragonflies;
    private String[] flatWorms;
    private String[] leeches;
    private String[] minnowMayflies;
    private String[] otherMayflies;
    private String[] snailsClamsMussels;
    private String[] stoneflies;
    private String[] trueFlies;
    private String[] worms;

    public KeyAdapter(Activity activity, Context context, ArrayList<FilterGroupModel> filterGroup, TextView likelyGroupText, LinearLayout likelyGroups) {
        this.context = context;
        this.filterGroup = filterGroup;
        Collections.sort(this.filterGroup, (t1, t2) -> t1.getName().compareTo(t2.getName()));
        this.filterGroupCopy.addAll(filterGroup);
        this.likelyGroupText = likelyGroupText;
        this.likelyGroups = likelyGroups;
        this.activity = activity;
        initializeStringArrays();
    }

    private void initializeStringArrays() {
        bugsAndBeetles = new String[]{
                context.getString(R.string.clearly_defined_legs),
                context.getString(R.string.appendages),
                context.getString(R.string.three_pairs_of_legs),
                context.getString(R.string.feather_like_gills),
                context.getString(R.string.antennae),
                context.getString(R.string.rounded_body)
        };

        caddisflies = new String[]{
                context.getString(R.string.shelter),
                context.getString(R.string.clearly_defined_legs),
                context.getString(R.string.long_thin_body),
                context.getString(R.string.three_pairs_of_legs),
                context.getString(R.string.tufted_tail),
                context.getString(R.string.short_tail),
                context.getString(R.string.feather_like_gills)
        };

        crabsAndShrimps = new String[]{
                context.getString(R.string.clearly_defined_legs),
                context.getString(R.string.four_or_more_pairs_of_legs),
                context.getString(R.string.antennae)
        };

        damselflies = new String[]{
                context.getString(R.string.clearly_defined_legs),
                context.getString(R.string.long_thin_body),
                context.getString(R.string.three_pairs_of_legs),
                context.getString(R.string.leaf_like_gills),
                context.getString(R.string.antennae),
                context.getString(R.string.wing_buds)
        };

        dragonflies = new String[]{
                context.getString(R.string.clearly_defined_legs),
                context.getString(R.string.three_pairs_of_legs),
                context.getString(R.string.bulging_eyes),
                context.getString(R.string.stocky_body),
                context.getString(R.string.antennae),
                context.getString(R.string.wing_buds)
        };

        flatWorms = new String[]{
                context.getString(R.string.flattened_body)
        };

        leeches = new String[]{
                context.getString(R.string.segmented_body),
                context.getString(R.string.suckers_at_both_ends)
        };

        minnowMayflies = new String[]{
                context.getString(R.string.clearly_defined_legs),
                context.getString(R.string.three_pairs_of_legs),
                context.getString(R.string.elongated_tail),
                context.getString(R.string.plate_like_gills),
                context.getString(R.string.antennae),
                context.getString(R.string.wing_buds)
        };

        otherMayflies = new String[]{
                context.getString(R.string.clearly_defined_legs),
                context.getString(R.string.three_pairs_of_legs),
                context.getString(R.string.elongated_tail),
                context.getString(R.string.feather_like_gills),
                context.getString(R.string.antennae)
        };

        snailsClamsMussels = new String[]{
                context.getString(R.string.shell)
        };

        stoneflies = new String[]{
                context.getString(R.string.clearly_defined_legs),
                context.getString(R.string.three_pairs_of_legs),
                context.getString(R.string.elongated_tail),
                context.getString(R.string.feather_like_gills),
                context.getString(R.string.antennae)
        };

        trueFlies = new String[]{
                context.getString(R.string.segmented_body),
                context.getString(R.string.long_thin_body),
                context.getString(R.string.appendages),
                context.getString(R.string.short_stubby_legs)
        };

        worms = new String[]{
                context.getString(R.string.segmented_body),
                context.getString(R.string.long_thin_body)
        };
    }

    @NonNull
    @Override
    public KeyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new KeyAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.group_filter_item,
                parent, false));
    }

    public void onBindViewHolder(@NonNull KeyAdapter.ViewHolder holder, int position) {

        HashMap<String,String[]> keyFilters = new HashMap<>();

        keyFilters.put("bugs_and_beetles", bugsAndBeetles);
        keyFilters.put("caddisflies", caddisflies);
        keyFilters.put("crabs_and_shrimps", crabsAndShrimps);
        keyFilters.put("damselflies", damselflies);
        keyFilters.put("dragonflies", dragonflies);
        keyFilters.put("flat_worms", flatWorms);
        keyFilters.put("leeches", leeches);
        keyFilters.put("minnow_mayflies", minnowMayflies);
        keyFilters.put("other_mayflies", otherMayflies);
        keyFilters.put("snails_clams_mussels", snailsClamsMussels);
        keyFilters.put("stoneflies", stoneflies);
        keyFilters.put("true_flies", trueFlies);
        keyFilters.put("worms", worms);

        FilterGroupModel modal = filterGroup.get(position);
        groupItemName = holder.itemView.findViewById(R.id.groupItemName);
        groupItemName.setText(modal.getName());

        RecyclerView imagesView = holder.itemView.findViewById(R.id.rvFilterImages);

        BitmapAdapter bitmapAdapter = new BitmapAdapter(this.context, modal.getImages(), modal.getBigImages() ,modal.getDescription());
        imagesView.setLayoutManager(new LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false));
        imagesView.setAdapter(bitmapAdapter);

        ImageView info = holder.itemView.findViewById(R.id.groupItemInfo);
        info.setOnClickListener(view -> {
            Intent i = new Intent(context, ImageViewDescriptionActivity.class);
            i.putExtra("image", modal.getBigImages().get(0));
            i.putExtra("description", modal.getDescription());
            context.startActivity(i);
        });

        if ((holder.checkbox.getText().toString().equals(modal.getName())) && modal.getSelected()) {
            holder.checkbox.setChecked(true);
        } else {
            holder.checkbox.setChecked(false);
        }

        holder.checkbox.setOnClickListener(v -> {
            FilterGroupModel filter = filterGroup.get(position);
            filter.setSelected(false);

            if (holder.checkbox.isChecked()){
                selected.add(filter.getName());
                filter.setSelected(true);
            }else{
                if (selected.contains(filter.getName())) {
                    selected.remove(filter.getName());
                }
            }
            filter(keyFilters);
        });
    }

    public int getItemCount() {
        return this.filterGroup.size();
    }

    public ArrayList<String> getSelected() {
        return selected;
    }

    public void filter(HashMap<String,String[]> keyFilters) {
        filterGroup.clear();

        ArrayList<String> possible = getPossible(keyFilters);

        for(String pos : possible)  {
            for (HashMap.Entry<String,String[]> filter : keyFilters.entrySet()) {
                String key = filter.getKey();
                String[] value = filter.getValue();

                if (key.equals(pos)) {
                    for(int i = 0; i < value.length; i++) {
                        for (int j = 0; j < filterGroupCopy.size(); j++) {
                            if (filterGroupCopy.get(j).getName().equals(value[i])) {
                                FilterGroupModel current = filterGroupCopy.get(j);
                                if (!filterGroup.contains(current)) {
                                    filterGroup.add(current);
                                }
                            }
                        }
                    }
                }
            }
        }

        Collections.sort(this.filterGroup, (t1, t2) -> t1.getName().compareTo(t2.getName()));

        System.out.println("Size: " + filterGroupCopy);

        this.likelyGroups.removeAllViews();
        this.likelyGroupText.setVisibility(View.VISIBLE);

        if (possible.size() == 13) {
            this.likelyGroupText.setText(context.getString(R.string.activity_key_start));
        } else {
            if (possible.size() <= 2) {
                for (String x : possible) {
                    String prettyX = "";
                    if (x.contains("snail")) {
                        prettyX = x.replace("_", "/");
                    } else {
                        prettyX = x.replace("_", " ");
                        prettyX = prettyX.replace("and", "&");
                    }

                    System.out.println("POSS " + x);
                    GradientDrawable shape =  new GradientDrawable();
                    shape.setCornerRadius( 8 );
                    shape.setColor(ContextCompat.getColor(this.context, R.color.green));
                    Button group = new Button(this.context);
                    group.setText(prettyX);
                    group.setGravity(Gravity.CENTER);
                    group.setPadding(20,20,20,20);
                    group.setBackground(shape);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMarginEnd(10);
                    group.setLayoutParams(params);

                    group.setOnClickListener(view -> {
                        this.activity.setResult(150,
                                new Intent().putExtra("selected", x.split("_")[0]));
                        this.activity.finish();
                    });

                    this.likelyGroups.addView(group);
                    this.likelyGroupText.setVisibility(View.GONE);
                }
            } else {
                this.likelyGroupText.setText(context.getString(R.string.activity_key_more_filters));
            }
        }

        notifyDataSetChanged();
    }

    public ArrayList<String> getPossible(HashMap<String,String[]> keyFilters) {
        ArrayList<String> likelyGroups = new ArrayList<>();

        ArrayList<String> selectedList = selected;

        for (HashMap.Entry<String,String[]> filter : keyFilters.entrySet()) {
            String key = filter.getKey();
            String[] value = filter.getValue();

            if (Arrays.asList(value).containsAll(selectedList)) {
                likelyGroups.add(key);
            } else {
                if(likelyGroups.contains(key)) {
                    likelyGroups.remove(key);
                }
            }
        }
        return likelyGroups;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkbox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            checkbox = itemView.findViewById(R.id.groupItemName);
        }
    }

}
