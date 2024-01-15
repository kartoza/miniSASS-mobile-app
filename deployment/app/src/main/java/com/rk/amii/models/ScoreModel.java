package com.rk.amii.models;

public class ScoreModel {

    private final String invertType;
    private final Integer score;

    /**
     * Create a new score object
     * @param invertType macroinvertebrate type
     * @param score score value
     */
    public ScoreModel(String invertType, Integer score)
    {
        this.invertType = invertType;
        this.score = score;
    }

    /**
     * Get the score
     * @return score
     */
    public Integer getScore() {
        return this.score;
    }

    /**
     * Get the macroinvertebrate type
     * @return macroinvertebrate type
     */
    public String getInvertType() {
        return this.invertType;
    }
}
