package com.rk.amii.models;

import com.google.gson.annotations.SerializedName;

public class UserModel {

    @SerializedName("email")
    private String email;

    @SerializedName("name")
    private String name;

    @SerializedName("surname")
    private String surname;

    @SerializedName("organisation_type")
    private String organisationType;

    @SerializedName("organisation_name")
    private String organisationName;

    @SerializedName("country")
    private String country;

    @SerializedName("upload_preference")
    private String uploadPreference;

    // Constructor
    public UserModel(String email, String name, String surname, String organisationType,
                String organisationName, String country, String uploadPreference) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.organisationType = organisationType;
        this.organisationName = organisationName;
        this.country = country;
        this.uploadPreference = uploadPreference;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getOrganisationType() {
        return organisationType;
    }

    public void setOrganisationType(String organisationType) {
        this.organisationType = organisationType;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getUploadPreference() {
        return uploadPreference;
    }

    public void setUploadPreference(String uploadPreference) {
        this.uploadPreference = uploadPreference;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", organisationType='" + organisationType + '\'' +
                ", organisationName='" + organisationName + '\'' +
                ", country='" + country + '\'' +
                ", uploadPreference='" + uploadPreference + '\'' +
                '}';
    }
}
