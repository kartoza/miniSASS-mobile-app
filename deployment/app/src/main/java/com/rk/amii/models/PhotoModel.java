package com.rk.amii.models;

public class PhotoModel {

    private final Integer photoId;
    private final Integer assessmentId;
    private final String location;
    private final String userChoice;
    private final String mlPredictions;

    /**
     * Create a new photo item
     * @param assessmentId assessment ID value
     * @param location location value
     * @param userChoice user choice value
     * @param mlPredictions ml prediction value
     */
    public PhotoModel(Integer assessmentId, String location, String userChoice,
                      String mlPredictions, Integer photoId)
    {
        this.assessmentId = assessmentId;
        this.location = location;
        this.userChoice = userChoice;
        this.mlPredictions = mlPredictions;
        this.photoId = photoId;
    }

    /**
     * get the assessment's id
     * @return assessment id
     */
    public Integer getAssessmentId() {
        return this.assessmentId;
    }

    /**
     * get the location of the photo
     * @return location of the photo
     */
    public String getPhotoLocation() {
        return this.location;
    }

    /**
     * Get the group the user chose
     * @return the group chosen by the user
     */
    public String getUserChoice() { return this.userChoice;}

    /**
     * Get the group the ML model predicted
     * @return the group predicted by the ML model
     */
    public String getMlPredictions() { return this.mlPredictions;}

    /**
     * Get the photo id
     * @return the photo id
     */
    public Integer getPhotoId() { return this.photoId;}
}
