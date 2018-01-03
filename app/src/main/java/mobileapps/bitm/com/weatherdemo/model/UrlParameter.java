package mobileapps.bitm.com.weatherdemo.model;

/**
 * Created by Zahangir Alam on 2018-01-04.
 */

public class UrlParameter {

    private double lon;
    private double lat;
    private String unit;
    private String dataType;

    public UrlParameter() {
    }

    public UrlParameter(double lon, double lat, String unit, String dataType) {
        this.lon = lon;
        this.lat = lat;
        this.unit = unit;
        this.dataType = dataType;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
