package com.rk.amii.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rk.amii.R;
import com.rk.amii.adapters.KeyAdapter;
import com.rk.amii.models.FilterGroupModel;

import java.util.ArrayList;
import java.util.HashMap;

public class KeyActivity extends AppCompatActivity {

    private ArrayList<FilterGroupModel> groupFilters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key);
        // Set the key filters
        this.setKeyFilters();
        // Set the filters for each macroinvertebrate group
        this.setMacroinvertebrateGroupFilters();
        // Initialise the view elements
        this.initElements();
    }

    /**
     * Initialize the view elements and set their values
     */
    private void initElements() {
        RecyclerView keysView = findViewById(R.id.rvGroupFilters);
        TextView likelyGroup = findViewById(R.id.likelyGroup);
        LinearLayout groups = findViewById(R.id.groupsContainer);

        KeyAdapter keyAdapter = new KeyAdapter(this, this, groupFilters, likelyGroup, groups);
        keysView.setLayoutManager(new LinearLayoutManager(this));
        keysView.setAdapter(keyAdapter);
    }

    /**
     * Setup the filters used by the key
     */
    private void setKeyFilters() {
        String[] bugsAndBeetles = {"clearly_defined_legs","appendages","3_pairs_of_legs","feather_like_gills","antennae","rounded_body"};
        String[] caddisflies = {"shelter","clearly_defined_legs","long_thin_body","3_pairs_of_legs","tufted_tail","short_tail","feather_like_gills"};
        String[] crabsAndShrimps = {"clearly_defined_legs","4_or_more_pairs_of_legs","antennae"};
        String[] damselflies = {"clearly_defined_legs","long_thin_body","3_pairs_of_legs","leaf_like_gills","antennae","wing_buds"};
        String[] dragonflies = {"clearly_defined_legs","3_pairs_of_legs","bulging_eyes","stocky_body","antennae","wing_buds"};
        String[] flatWorms = {"flattened_body"};
        String[] leeches = {"segmented_body","suckers_at_both_ends"};
        String[] minnowMayflies = {"clearly_defined_legs","3_pairs_of_legs","elongated_tail","plate_like_gills","antennae","wing_buds"};
        String[] otherMayflies = {"clearly_defined_legs","3_pairs_of_legs","elongated_tail","feather_like_gills","antennae"};
        String[] snailsClamsMussels = {"shell"};
        String[] stoneflies = {"clearly_defined_legs","3_pairs_of_legs","elongated_tail","feather_like_gills","antennae"};
        String[] trueFlies = {"segmented_body","long_thin_body","appendages","short_stubby_legs"};
        String[] worms = {"segmented_body","long_thin_body"};

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
    }

    /**
     * Set the thumbnail and original images used by each group and add them to the group
     */
    private void setMacroinvertebrateGroupFilters() {
        // TODO: find a better way to handle this
        ArrayList<Integer> shelter = new ArrayList<>();
        shelter.add(R.raw.shelter_tumbnail);
        shelter.add(R.raw.shelter_2_tumbnail);

        ArrayList<Integer> clegs = new ArrayList<>();
        clegs.add(R.raw.clearly_defined_legs_tumbnail);

        ArrayList<Integer> sbody = new ArrayList<>();
        sbody.add(R.raw.segmented_body_tumbnail);
        sbody.add(R.raw.segmented_body_2_tumbnail);
        sbody.add(R.raw.segmented_body_3_tumbnail);

        ArrayList<Integer> lbody = new ArrayList<>();
        lbody.add(R.raw.long_thin_body_tumbnail);
        lbody.add(R.raw.long_thin_body_2_tumbnail);
        lbody.add(R.raw.long_thin_body_3_tumbnail);

        ArrayList<Integer> tlegs = new ArrayList<>();
        tlegs.add(R.raw.three_pairs_of_legs_tumbnail);

        ArrayList<Integer> flegs = new ArrayList<>();
        flegs.add(R.raw.four_or_more_pairs_of_legs_tumbnail);
        flegs.add(R.raw.four_or_more_pairs_of_legs_2_tumbnail);

        ArrayList<Integer> etail = new ArrayList<>();
        etail.add(R.raw.elongated_tails_tumbnail);
        etail.add(R.raw.elongated_tails_2_tumbnail);
        etail.add(R.raw.elongated_tails_3_tumbnail);

        ArrayList<Integer> ttail = new ArrayList<>();
        ttail.add(R.raw.tufted_tail_tumbnail);

        ArrayList<Integer> pgills = new ArrayList<>();
        pgills.add(R.raw.plate_like_gills_tumbnail);

        ArrayList<Integer> fgills = new ArrayList<>();
        fgills.add(R.raw.feather_gills_tumbnail);
        fgills.add(R.raw.feather_gills_2_tumbnail);
        fgills.add(R.raw.feather_gills_3_tumbnail);

        ArrayList<Integer> lgills = new ArrayList<>();
        lgills.add(R.raw.leaf_like_gills_tumbnail);
        lgills.add(R.raw.leaf_like_gills_2_tumbnail);

        ArrayList<Integer> beyes = new ArrayList<>();
        beyes.add(R.raw.bulging_eyes);
        beyes.add(R.raw.bulging_eyes_2_tumbnail);

        ArrayList<Integer> stbody = new ArrayList<>();
        stbody.add(R.raw.stocky_body_tumbnail);
        stbody.add(R.raw.stocky_body_2_tumbnail);

        ArrayList<Integer> antennae = new ArrayList<>();
        antennae.add(R.raw.antennae_tumbnail);
        antennae.add(R.raw.antennae_2_tumbnail);
        antennae.add(R.raw.antennae_3_tumbnail);

        ArrayList<Integer> suckers = new ArrayList<>();
        suckers.add(R.raw.suckers_tumbnail);

        ArrayList<Integer> wing_buds = new ArrayList<>();
        wing_buds.add(R.raw.wing_buds_tumbnail);
        wing_buds.add(R.raw.wing_buds_2_tumbnail);

        ArrayList<Integer> fbody = new ArrayList<>();
        fbody.add(R.raw.flattened_body_tumbnail);

        ArrayList<Integer> rbody = new ArrayList<>();
        rbody.add(R.raw.rounded_body_tumbnail);

        ArrayList<Integer> slegs = new ArrayList<>();
        slegs.add(R.raw.short_stubby_legs_tumbnail);

        ArrayList<Integer> appendages = new ArrayList<>();
        appendages.add(R.raw.appendage_tumbnail);
        appendages.add(R.raw.appendage_2_tumbnail);
        appendages.add(R.raw.appendage_3_tumbnail);

        ArrayList<Integer> shell = new ArrayList<>();
        shell.add(R.raw.shell_tumbnail);
        shell.add(R.raw.shell_2_tumbnail);

        ArrayList<Integer> stail = new ArrayList<>();
        stail.add(R.raw.short_tail_tumbnail);
        stail.add(R.raw.short_tail_2_tumbnail);

        ArrayList<Integer> bshelter = new ArrayList<>();
        bshelter.add(R.raw.shelter);
        bshelter.add(R.raw.shelter_2);

        ArrayList<Integer> bclegs = new ArrayList<>();
        bclegs.add(R.raw.clearly_defined_legs);

        ArrayList<Integer> bsbody = new ArrayList<>();
        bsbody.add(R.raw.segmented_body);
        bsbody.add(R.raw.segmented_body_2);
        bsbody.add(R.raw.segmented_body_3);

        ArrayList<Integer> blbody = new ArrayList<>();
        blbody.add(R.raw.long_thin_body);
        blbody.add(R.raw.long_thin_body_2);
        blbody.add(R.raw.long_thin_body_3);

        ArrayList<Integer> btlegs = new ArrayList<>();
        btlegs.add(R.raw.three_pairs_of_legs);

        ArrayList<Integer> bflegs = new ArrayList<>();
        bflegs.add(R.raw.four_or_more_pairs_of_legs);
        bflegs.add(R.raw.four_or_more_pairs_of_legs_2);

        ArrayList<Integer> betail = new ArrayList<>();
        betail.add(R.raw.elongated_tails);
        betail.add(R.raw.elongated_tails_2);
        betail.add(R.raw.elongated_tails_3);

        ArrayList<Integer> bttail = new ArrayList<>();
        bttail.add(R.raw.tufted_tail);

        ArrayList<Integer> bpgills = new ArrayList<>();
        bpgills.add(R.raw.plate_like_gills);

        ArrayList<Integer> bfgills = new ArrayList<>();
        bfgills.add(R.raw.feather_gills);
        bfgills.add(R.raw.feather_gills_2);
        bfgills.add(R.raw.feather_gills_3);

        ArrayList<Integer> blgills = new ArrayList<>();
        blgills.add(R.raw.leaf_like_gills);
        blgills.add(R.raw.leaf_like_gills_2);

        ArrayList<Integer> bbeyes = new ArrayList<>();
        bbeyes.add(R.raw.bulging_eyes);
        bbeyes.add(R.raw.bulging_eyes_2);

        ArrayList<Integer> bstbody = new ArrayList<>();
        bstbody.add(R.raw.stocky_body);
        bstbody.add(R.raw.stocky_body_2);

        ArrayList<Integer> bantennae = new ArrayList<>();
        bantennae.add(R.raw.antennae);
        bantennae.add(R.raw.antennae_2);
        bantennae.add(R.raw.antennae_3);

        ArrayList<Integer> bsuckers = new ArrayList<>();
        bsuckers.add(R.raw.suckers);

        ArrayList<Integer> bwing_buds = new ArrayList<>();
        bwing_buds.add(R.raw.wing_buds);
        bwing_buds.add(R.raw.wing_buds_2);

        ArrayList<Integer> bfbody = new ArrayList<>();
        bfbody.add(R.raw.flattened_body);

        ArrayList<Integer> brbody = new ArrayList<>();
        brbody.add(R.raw.rounded_body);

        ArrayList<Integer> bslegs = new ArrayList<>();
        bslegs.add(R.raw.short_stubby_legs);

        ArrayList<Integer> bappendages = new ArrayList<>();
        bappendages.add(R.raw.appendage);
        bappendages.add(R.raw.appendage_2);
        bappendages.add(R.raw.appendage_3);

        ArrayList<Integer> bshell = new ArrayList<>();
        bshell.add(R.raw.shell);
        bshell.add(R.raw.shell_2);

        ArrayList<Integer> bstail = new ArrayList<>();
        bstail.add(R.raw.short_tail);
        bstail.add(R.raw.short_tail_2);

        groupFilters = new ArrayList<>();
        groupFilters.add(new FilterGroupModel("Shell", shell, bshell, R.string.shell_description));
        groupFilters.add(new FilterGroupModel("Shelter", shelter, bshelter, R.string.shelter_description));
        groupFilters.add(new FilterGroupModel("Clearly defined legs", clegs, bclegs, R.string.clearly_defined_legs_description));
        groupFilters.add(new FilterGroupModel("Segmented body", sbody, bsbody, R.string.segmented_body_description));
        groupFilters.add(new FilterGroupModel("Long thin body", lbody, blbody, R.string.long_thin_body_description));
        groupFilters.add(new FilterGroupModel("Appendages", appendages, bappendages, R.string.appendages_description));
        groupFilters.add(new FilterGroupModel("3 pairs of legs", tlegs, btlegs, R.string.three_pairs_of_legs_description));
        groupFilters.add(new FilterGroupModel("4 or more pairs of legs", flegs, bflegs, R.string.four_or_more_pairs_of_legs_description));
        groupFilters.add(new FilterGroupModel("Elongated tail", etail, betail, R.string.elongated_tail_description));
        groupFilters.add(new FilterGroupModel("Tufted tail", ttail, bttail, R.string.tufted_tail_description));
        groupFilters.add(new FilterGroupModel("Short tail", stail, bstail, R.string.short_tail_description));
        groupFilters.add(new FilterGroupModel("Plate like gills", pgills, bpgills, R.string.plate_like_gills_description));
        groupFilters.add(new FilterGroupModel("Feather like gills", fgills, bfgills, R.string.feather_like_gills_description));
        groupFilters.add(new FilterGroupModel("Leaf like gills", lgills, blgills, R.string.leaf_like_gills_description));
        groupFilters.add(new FilterGroupModel("Bulging eyes", beyes, bbeyes, R.string.bulging_eyes_description));
        groupFilters.add(new FilterGroupModel("Stocky body", stbody, bstbody, R.string.stocky_body_description));
        groupFilters.add(new FilterGroupModel("Antennae", antennae, bantennae, R.string.antennae_description));
        groupFilters.add(new FilterGroupModel("Suckers at both ends", suckers, bsuckers, R.string.suckers_at_both_ends_description));
        groupFilters.add(new FilterGroupModel("Wing buds", wing_buds, bwing_buds, R.string.wing_buds_description));
        groupFilters.add(new FilterGroupModel("Flattened body", fbody, bfbody, R.string.flattened_body_description));
        groupFilters.add(new FilterGroupModel("Short stubby legs", slegs, bslegs, R.string.short_stubby_legs_description));
        groupFilters.add(new FilterGroupModel("Rounded body", rbody, brbody, R.string.rounded_body_description));
    }
}