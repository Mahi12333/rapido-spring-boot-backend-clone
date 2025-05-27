package com.maven.Rapido.payload.request.driver;

public class LatLngDTO {
    private double lat;
    private double lng;

    // Constructors
    public LatLngDTO() {}
    public LatLngDTO(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    // Getters and Setters
    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
