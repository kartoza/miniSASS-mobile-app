package com.rk.amii.models;

public class AssessmentModel {

    private final Float miniSassScore;
    private final Float miniSassMLScore;
    private final Integer assessmentId;
    private final Integer onlineAssessmentId;
    private final String notes;
    private final String ph;
    private final String waterTemp;
    private final String dissolvedOxygen;
    private final String dissolvedOxygenUnit;
    private final String electricalConductivity;
    private final String electricalConductivityUnit;
    private final String waterClarity;

    /**
     * Create a new assessment
     * @param assessmentId assessment id
     * @param onlineAssessmentId assessment id
     * @param miniSassScore miniSASS score
     * @param miniSassMLScore machine learning model prediction score
     * @param notes notes
     * @param ph ph
     * @param waterTemp water temperature
     * @param dissolvedOxygen dissolved oxygen
     * @param dissolvedOxygenUnit dissolved oxygen unit
     * @param electricalConductivity electrical conductivity
     * @param electricalConductivityUnit electrical conductivity unit
     * @param waterClarity water clarity
     */
    public AssessmentModel (Integer assessmentId, Integer onlneAssessmentId, Float miniSassScore, Float miniSassMLScore,
                            String notes, String ph, String waterTemp,
                            String dissolvedOxygen, String dissolvedOxygenUnit,
                            String electricalConductivity, String electricalConductivityUnit,
                            String waterClarity)
    {
        this.assessmentId = assessmentId;
        this.onlineAssessmentId = onlneAssessmentId;
        this.miniSassScore = miniSassScore;
        this.miniSassMLScore = miniSassMLScore;
        this.notes = notes;
        this.ph = ph;
        this.waterTemp = waterTemp;
        this.dissolvedOxygen = dissolvedOxygen;
        this.dissolvedOxygenUnit = dissolvedOxygenUnit;
        this.electricalConductivity = electricalConductivity;
        this.electricalConductivityUnit = electricalConductivityUnit;
        this.waterClarity = waterClarity;
    }

    /**
     * Get the assessment id
     * @return the assessment id
     */
    public Integer getAssessmentId() { return this.assessmentId;}

    /**
     * Get the online assessment id
     * @return the online assessment id
     */
    public Integer getOnlineAssessmentId() { return this.onlineAssessmentId;}

    /**
     * Get the mini-sass score
     * @return the mini-sass score
     */
    public Float getMiniSassScore() {
        return this.miniSassScore;
    }

    /**
     * Get the mini-sass ML score
     * @return the mini-sass ML score
     */
    public Float getMiniSassMLScore() {
        return this.miniSassMLScore;
    }

    /**
     * Get the assessment notes
     * @return assessment notes
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Get the assessment water ph level measurement
     * @return assessment ph level of water
     */
    public String getPh() { return ph; }

    /**
     * Get the assessment water temperature measurement
     * @return assessment water temperature measurement
     */
    public String getWaterTemp() {
        return waterTemp;
    }

    /**
     * Get the assessment water dissolved oxygen measurement
     * @return assessment water dissolved oxygen measurement
     */
    public String getDissolvedOxygen() {
        return dissolvedOxygen;
    }

    /**
     * Get the assessment water dissolved oxygen measurement unit
     * @return assessment water dissolved oxygen measurement unit
     */
    public String getDissolvedOxygenUnit() {
        return dissolvedOxygenUnit;
    }

    /**
     * Get the assessment water electrical conductivity measurement
     * @return assessment water electrical conductivity measurement
     */
    public String getElectricalConductivity() {
        return electricalConductivity;
    }

    /**
     * Get the assessment water electrical conductivity oxygen measurement unit
     * @return assessment water electrical conductivity measurement unit
     */
    public String getElectricalConductivityUnit() {
        return electricalConductivityUnit;
    }

    /**
     * Get the assessment water clarity measurement
     * @return assessment water clarity measurement
     */
    public String getWaterClarity() {
        return waterClarity;
    }
}
