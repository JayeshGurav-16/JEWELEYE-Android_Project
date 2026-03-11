package com.example.admin;

import java.io.Serializable;

public class CustomerAddress implements Serializable {
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private boolean isDefault;

    public CustomerAddress() {
        this.country = "USA"; // Default country
        this.isDefault = false;
    }

    public CustomerAddress(String street, String city, String state, String zipCode) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.country = "USA"; // Default country
        this.isDefault = false;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    @Override
    public String toString() {
        return street + ", " + city + ", " + state + " " + zipCode + ", " + country;
    }
}