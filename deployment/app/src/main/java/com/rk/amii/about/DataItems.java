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
        list1.add("Aquatic macroinvertebrates are small animals that live in water systems and have no internal skeleton. They are visible to the naked eye, hence the term ‘macro’ meaning ‘large’.\n" +
                "Different kinds of aquatic macroinvertebrates have different tolerance levels or sensitivities to disturbance in their environment. This means that one can look at the community of macroinvertebrates present in a stream or river and infer something about the water quality and health of the system. \n" +
                "Basically in disturbed systems, for example where there is pollution or abundant alien invasive species, the most sensitive and least tolerant species can be expected to disappear, while the tolerant and least sensitive species will remain present or increase in numbers.\n" +
                "MiniSASS puts all the aquatic macroinvertebrate types into 13 relatively easy to identify groups, and gives those groups scores based on the known general, average tolerance and sensitivity levels of the types in those groups. Low scores are assigned to groups generally made up of very tolerant species (which can survive pollution and disturbance), while high scores are assigned to groups generally made up of very sensitive species (which can’t live and survive if there is pollution or disturbance).\n" +
                "In the miniSASS app, you capture images of the aquatic macroinvertebrates you have sampled. You assign an identification to these images. At the same time, a machine-learning (ML) artificial intelligence (AI) identification algorithm runs on the image, identifying it as well. The app then generates a user score, and a ML score. These can be compared as a guide to the user if they are identifying correctly or perhaps need to revisit their identifications.\n" +
                "A miniSASS survey samples a stream or river for these aquatic macroinvertebrates, and generates score based on the average sensitivity and tolerance of the community present. The score then relates to an ecological category, indicating the stream or river health and water quality.\n");

        List<String> list2 = new ArrayList<>();
        list2.add("Fresh water is essential for biodiversity and humans. It sustains rich " +
                "ecosystems along with all the goods and services they provide, and supports human" +
                " agriculture, industry, sanitation, and hydration. Managing our precious and often" +
                " scarce freshwater systems requires monitoring the health and quality of our " +
                "streams and rivers. Basically, appropriate management and intervention for " +
                "freshwater ecosystems can only be designed and implemented if there are good " +
                "monitoring data to show where problems are, or where solution are working!");

        List<String> list3 = new ArrayList<>();
        list3.add("MiniSASS is based on the more in-depth biomonitoring technique, the South " +
                "African Scoring System (SASS). The SASS system rigorously samples streams and" +
                " rivers, identifying all the aquatic macroinvertebrates present to family level " +
                "(over 90 different groupings). Dr P. Mark Graham and Dr Chris W.S. Dickens" +
                " refined the groupings down to the miniSASS 13, which are much more easily " +
                "identifiable but still provide a good picture of water quality and stream or " +
                "river health, similar to the more details SASS survey. The original miniSASS" +
                " was based on SASS version 4, but has been updated to align with the more " +
                "recent SASS5.");

        List<String> list4 = new ArrayList<>();
        list4.add("Always remember, safety first! When thinking of doing a miniSASS survey, please check your surroundings and make sure the stream or river and the area are safe. Don’t enter streams or rivers that are deep or that have powerful, fast-flowing currents, or any dangerous animals in them or nearby. \n" +
                "Protect yourself with sunscreen and appropriate clothing (gumboots and hats), and remember to wash your hands with sanitizer or soap regularly. Be especially careful with polluted rivers and streams, taking extra precautions to keep yourself safe and healthy. MiniSASS is more fun and safer with others! \n");

        List<String> list5 = new ArrayList<>();
        list5.add("- Aquatic macroinvertebrates: Small, but visible to the naked eye, animals" +
                " that have no internal skeleton and which live in water systems. ");
        list5.add("- Biomonitoring: This broadly refers to using organisms (e.g., algae, " +
                "macroinvertebrates, fish, birds, or mammals) living in an environment to " +
                "assess quantitative or qualitative information about that environment.");
        list5.add("- Biodiversity: All different kinds of life in an area. Basically, a term " +
                "that refers to all the plants, animals, fungi, or any life in an environment. ");
        list5.add("- Citizen science: The general public being involved in scientific research," +
                " in any way from research design, through data collection and analysis, to " +
                "reporting findings.");

        list5.add("- Conservation: Protecting and restoring natural systems, preventing extinctions" +
                ", and maintaining biological diversity and ecosystem health and function.");

        list5.add("- Ecosystem: A community of interacting living organisms and the physical " +
                "environment in which they live. A description of a whole system, including all " +
                "its biodiversity and physical habitat features.");

        list5.add("- Machine-learning (ML): Computer systems that are able to learn and adapt " +
                "without following explicit instructions. The computer learns by using algorithms " +
                "and statistical models to analyse and draw inferences from patterns in data. " +
                "For miniSASS a deep-neural-network trains on correctly identified images of " +
                "aquatic macroinvertebrates and learn to identify them according to the 13" +
                " miniSASS groups.");

        List<String> list6 = new ArrayList<>();
        list6.add("We can all play a part in monitoring freshwater resources, helping gather the data needed to manage them. By participating in citizen science, anyone can become an important contributor to research and conservation! \n" +
                "Conducting a miniSASS survey helps us learn about freshwater systems and the importance of biodiversity and healthy freshwater ecosystems, and gets us engaged with nature and our communities. It is a tool that lets us take action to help monitor and conserve our freshwater systems. \n" +
                "The results of a miniSASS survey give a good indication of the water quality and health of a stream or river, serving to flag possible pollution, or healthy and functioning systems. Continuous monitoring of the same stream or river can give amazing insight into changes over time.\n" +
                "Once you get your miniSASS score, you can even follow up with relevant local authorities to get them to follow up on suspected problems using your data as an indicator!\n");

        List<String> list7 = new ArrayList<>();
        list7.add("- Shell: " + context.getString(R.string.shell_description));
        list7.add("- Shelter: " + context.getString(R.string.shelter_description));
        list7.add("- Clearly defined legs: " + context.getString(R.string.clearly_defined_legs_description));
        list7.add("- Segmented body: " + context.getString(R.string.segmented_body_description));
        list7.add("- Long thin body: " + context.getString(R.string.long_thin_body_description));
        list7.add("- Appendages: " + context.getString(R.string.appendages_description));
        list7.add("- Three pairs of legs: " + context.getString(R.string.three_pairs_of_legs_description));
        list7.add("- Four or more pairs of legs: " + context.getString(R.string.four_or_more_pairs_of_legs_description));
        list7.add("- Elongated tail: " + context.getString(R.string.elongated_tail_description));
        list7.add("- Tufted tail: " + context.getString(R.string.tufted_tail_description));
        list7.add("- Short tail description: " + context.getString(R.string.short_tail_description));
        list7.add("- Plate like gills: " + context.getString(R.string.plate_like_gills_description));
        list7.add("- Feather like gills: " + context.getString(R.string.feather_like_gills_description));
        list7.add("- Leaf like gills: " + context.getString(R.string.leaf_like_gills_description));
        list7.add("- Bulging eyes: " + context.getString(R.string.bulging_eyes_description));
        list7.add("- Stocky body: " + context.getString(R.string.stocky_body_description));
        list7.add("- Antennae: " + context.getString(R.string.antennae_description));
        list7.add("- Suckers at both ends: " + context.getString(R.string.suckers_at_both_ends_description));
        list7.add("- Wing buds: " + context.getString(R.string.wing_buds_description));
        list7.add("- Flattened body: " + context.getString(R.string.flattened_body_description));
        list7.add("- Short stubby legs: " + context.getString(R.string.short_stubby_legs_description));
        list7.add("- Rounded body: " + context.getString(R.string.rounded_body_description));

        expandableDetailList.put("What are aquatic macroinvertebrates and how does miniSASS used them for biomonitoring?", list1);
        expandableDetailList.put("Why is water quality and stream health monitoring important?", list2);
        expandableDetailList.put("History of miniSASS", list3);
        expandableDetailList.put("River safety", list4);
        expandableDetailList.put("Citizen science and miniSASS", list6);
        expandableDetailList.put("Dichotomous key filter descriptions", list7);
        expandableDetailList.put("Glossary", list5);

        return expandableDetailList;
    }
}