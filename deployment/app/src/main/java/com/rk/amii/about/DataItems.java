package com.rk.amii.about;

import android.content.Context;

import com.rk.amii.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataItems {
    public static HashMap<String, List<String>> getData(Context context) {
        HashMap<String, List<String>> expandableDetailList = new HashMap<>();

        List<String> list1 = new ArrayList<>();
        list1.add(context.getString(R.string.aquatic_macroinvertebrates_definition));

        List<String> list2 = new ArrayList<>();
        list2.add(context.getString(R.string.freshwater_is_essential));

        List<String> list3 = new ArrayList<>();
        list3.add(context.getString(R.string.minisass_is_based_on));

        List<String> list4 = new ArrayList<>();
        list4.add(context.getString(R.string.always_remember_safety_first));

        List<String> list5 = new ArrayList<>();
        list5.add(context.getString(R.string.aquatic_macroinvertebrates_small_but_visible));
        list5.add(context.getString(R.string.biomonitoring_broadly_refers_to_using_organisms));
        list5.add(context.getString(R.string.biodiversity_all_different_kinds));
        list5.add(context.getString(R.string.citizen_science_the_general_public));
        list5.add(context.getString(R.string.conservation_protecting_and_restoring_natural_systems));
        list5.add(context.getString(R.string.ecosystem_community_of_interacting_living_organisms));
        list5.add(context.getString(R.string.machine_learning_computer_systems));

        List<String> list6 = new ArrayList<>();
        list6.add(context.getString(R.string.we_can_all_play_a_part_in_monitoring_freshwater_resources));

        List<String> list7 = new ArrayList<>();
        list7.add(
                context.getString(R.string.item_format, context.getString(R.string.shell)) + " " +
                        context.getString(R.string.shell_description)
        );
        list7.add(
                context.getString(R.string.item_format, context.getString(R.string.shelter)) + " " +
                        context.getString(R.string.shelter_description)
        );
        list7.add(
                context.getString(
                        R.string.item_format,
                        context.getString(R.string.clearly_defined_legs)
                ) + " " + context.getString(R.string.clearly_defined_legs_description)
        );
        list7.add(
                context.getString(
                        R.string.item_format,
                        context.getString(R.string.segmented_body)
                ) + " " + context.getString(R.string.segmented_body_description)
        );
        list7.add(
                context.getString(
                        R.string.item_format,
                        context.getString(R.string.long_thin_body)
                ) + " " + context.getString(R.string.long_thin_body_description)
        );
        list7.add(
                context.getString(
                        R.string.item_format,
                        context.getString(R.string.appendages)
                ) + " " + context.getString(R.string.appendages_description)
        );
        list7.add(
                context.getString(
                        R.string.item_format,
                        context.getString(R.string.three_pairs_of_legs)
                ) + " " + context.getString(R.string.three_pairs_of_legs_description)
        );
        list7.add(
                context.getString(
                        R.string.item_format,
                        context.getString(R.string.four_or_more_pairs_of_legs)
                ) + " " + context.getString(R.string.four_or_more_pairs_of_legs_description)
        );
        list7.add(
                context.getString(
                        R.string.item_format,
                        context.getString(R.string.elongated_tail)
                ) + " " + context.getString(R.string.elongated_tail_description)
        );
        list7.add(
                context.getString(
                        R.string.item_format,
                        context.getString(R.string.tufted_tail)
                ) + " " + context.getString(R.string.tufted_tail_description)
        );
        list7.add(
                context.getString(
                        R.string.item_format, context.getString(R.string.short_tail)
                ) + " " + context.getString(R.string.short_tail_description)
        );
        list7.add(
                context.getString(
                        R.string.item_format,
                        context.getString(R.string.plate_like_gills)
                ) + " " + context.getString(R.string.plate_like_gills_description)
        );
        list7.add(
                context.getString(
                        R.string.item_format,
                        context.getString(R.string.feather_like_gills
                        )
                ) + " " + context.getString(R.string.feather_like_gills_description)
        );
        list7.add(
                context.getString(
                        R.string.item_format,
                        context.getString(R.string.leaf_like_gills)
                ) + " " + context.getString(R.string.leaf_like_gills_description)
        );
        list7.add(
                context.getString(
                        R.string.item_format,
                        context.getString(R.string.bulging_eyes)
                ) + " " + context.getString(R.string.bulging_eyes_description)
        );
        list7.add(
                context.getString(
                        R.string.item_format,
                        context.getString(R.string.stocky_body)
                ) + " " + context.getString(R.string.stocky_body_description)
        );
        list7.add(
                context.getString(
                        R.string.item_format,
                        context.getString(R.string.antennae)
                ) + " " + context.getString(R.string.antennae_description)
        );
        list7.add(
                context.getString(
                        R.string.item_format,
                        context.getString(R.string.suckers_at_both_ends)
                ) + " " + context.getString(R.string.suckers_at_both_ends_description)
        );
        list7.add(
                context.getString(
                        R.string.item_format,
                        context.getString(R.string.wing_buds)
                ) + " " + context.getString(R.string.wing_buds_description)
        );
        list7.add(
                context.getString(
                        R.string.item_format,
                        context.getString(R.string.flattened_body)
                ) + " " + context.getString(R.string.flattened_body_description)
        );
        list7.add(
                context.getString(
                        R.string.item_format,
                        context.getString(R.string.short_stubby_legs)
                ) + " " + context.getString(R.string.short_stubby_legs_description)
        );
        list7.add(
                context.getString(
                        R.string.item_format,
                        context.getString(R.string.rounded_body)
                ) + " " + context.getString(R.string.rounded_body_description)
        );

        expandableDetailList.put(context.getString(R.string.what_are_aquatic), list1);
        expandableDetailList.put(context.getString(R.string.why_is_water_quality), list2);
        expandableDetailList.put(context.getString(R.string.history_of_minisass), list3);
        expandableDetailList.put(context.getString(R.string.river_safety), list4);
        expandableDetailList.put(context.getString(R.string.citizen_science_minisass), list6);
        expandableDetailList.put(context.getString(R.string.dichotomous_key_filter), list7);
        expandableDetailList.put(context.getString(R.string.glossary), list5);

        return expandableDetailList;
    }
}