package com.rk.amii.models;

import java.util.ArrayList;

public class FilterGroupModel {

    private final String name;
    private final ArrayList<Integer> images;
    private final ArrayList<Integer> bigImages;
    private Boolean selected;
    private final Integer description;

    /**
     * Create new filter group item
     * @param name filter name
     * @param images low quality image
     * @param bigImages good quality image
     * @param description filter description
     */
    public FilterGroupModel(String name, ArrayList<Integer> images, ArrayList<Integer> bigImages, Integer description)
    {
        this.name = name;
        this.images = images;
        this.selected = false;
        this.description = description;
        this.bigImages = bigImages;
    }

    /**
     * Set if the filter is selected
     */
    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    /**
     * Get if the filter is selected
     */
    public Boolean getSelected() {
        return this.selected;
    }

    /**
     * Get filter name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get filter description
     */
    public Integer getDescription() {
        return this.description;
    }

    /**
     * Get filter low quality images
     */
    public ArrayList<Integer> getImages() { return this.images;}

    /**
     * Get filter good quality images
     */
    public ArrayList<Integer> getBigImages() { return this.bigImages;}

}
