package by.ddv.myweather.model_daily_forecast;

import by.ddv.myweather.model_current_weather.Coord;


public class City {

    private String name;
    private String country;
    private Coord coord;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Coord getCoord() {
        return coord;
    }

    public void setCoord(Coord coord) {
        this.coord = coord;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public City(String name, Coord coord, String country) {
        this.name = name;
        this.coord = coord;
        this.country = country;
    }


}
