package com.rk.amii.models;

public class SitesModel {

    private final Integer siteId;
    private final String siteName;
    private final String siteLocation;
    private final String riverName;
    private final String description;
    private final String date;
    private final String riverType;
    private final String country;
    private final String onlineSiteId;
    private final Integer userId;

    /**
     * Create new site
     * @param siteId site id
     * @param siteName site name
     * @param siteLocation site location
     * @param riverName river name
     * @param description site description
     * @param date date created
     * @param riverType river type
     * @param country Country
     * @param onlineSiteId online site id, 0 if not uploaded yet
     * @param userId ID of the user who created the site
     */
    public SitesModel(
            Integer siteId,
            String siteName,
            String siteLocation,
            String riverName,
            String description,
            String date,
            String riverType,
            String country,
            String onlineSiteId,
            Integer userId)
    {
        this.siteId = siteId;
        this.siteName = siteName;
        this.siteLocation = siteLocation;
        this.riverName = riverName;
        this.description = description;
        this.date = date;
        this.riverType = riverType;
        this.country = country;
        this.onlineSiteId = onlineSiteId;
        this.userId = userId;
    }

    /**
     * Get the site id
     * @return site id
     */
    public Integer getSiteId() {return siteId;}

    /**
     * Get the site name
     * @return site name
     */
    public String getSiteName() {
        return siteName;
    }

    /**
     * Get the river name
     * @return river name
     */
    public String getRiverName() {
        return riverName;
    }

    /**
     * Get the site description
     * @return site description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the date the site was created on
     * @return site creation date
     */
    public String getDate() {
        return date;
    }

    /**
     * Get the river type
     * @return river type
     */
    public  String getRiverType() {return riverType; }

    /**
     * Get the country
     * @return country
     */
    public  String getCountry() {return country; }

    /**
     * Get the site's location (Lat,Long)
     * @return site location
     */
    public String getSiteLocation() {
        return siteLocation;
    }

    /**
     * Get the online site's id
     * @return online site id
     */
    public String getOnlineSiteId() {return onlineSiteId;}

    /**
     * Get user ID
     * @return User ID
     */
    public Integer getUserId() {return userId;}

}
