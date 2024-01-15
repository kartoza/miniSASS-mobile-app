package com.rk.amii.models;

public class LocationPinModel {

    private final Double latitude;
    private final Double longitude;
    private final String pinColor;
    private final Integer id;

    /**
     * Create new location pin item
     * @param latitude latitude value
     * @param longitude longitude value
     * @param pinColor pin color
     * @param id site id
     */
    public LocationPinModel(Double latitude, Double longitude, String pinColor, Integer id)
    {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.pinColor = pinColor;
    }

    /**
     * Get the location latitude value
     * @return latitude
     */
    public Double getLatitude() {return this.latitude;}

    /**
     * Get the location longitude value
     * @return latitude
     */
    public Double getLongitude() {return this.longitude;}

    /**
     * Get the pinColor
     * @return pinColor
     */
    public String getPinColor() {return this.pinColor;}

    /**
     * Get the site id
     * @return site id
     */
    public Integer getPinId() {return this.id;}
}
