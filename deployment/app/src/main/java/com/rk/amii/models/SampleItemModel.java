package com.rk.amii.models;

import android.graphics.Bitmap;

public class SampleItemModel {

    private final Bitmap image;
    private final String invertType;
    private final String location;
    private String mlPredictions;

    /**
     * Create a new river sample item
     * @param image image location
     * @param invertType macroinvertebrate type
     * @param mlPredictions ml prediction
     * @param location sample location
     */
    public SampleItemModel(Bitmap image, String invertType, String mlPredictions, String location)
    {
        this.image = image;
        this.invertType = invertType;
        this.mlPredictions = mlPredictions;
        this.location = location;
    }

    /**
     * Get the image of the sample
     * @return image
     */
    public Bitmap getImage() {
        return this.image;
    }

    /**
     * Get the macroinvertebrate type of the sample
     * @return macroinvertebrate type
     */
    public String getInvertType() {
        return this.invertType;
    }

    /**
     * Get the prediction of the ML model made on the sample image
     * @return ML model prediction
     */
    public String getMlPredictions() { return this.mlPredictions;}

    /**
     * Get the location of the image
     * @return image location
     */
    public String getLocation() { return this.location;}

    /**
     * Set the ML prediction
     * @param mlPredictions ml prediction value
     */
    public void setMlPredictions(String mlPredictions) { this.mlPredictions = mlPredictions; }
}
