package by.ddv.myweather.model_daily_forecast;


import java.util.List;


public class OpenWeatherMapDaily {

    private List<DailyWeather> list;
    private City city;

    public OpenWeatherMapDaily() {

    }

    public List<DailyWeather> getList() {
        return list;
    }

    public void setList(List<DailyWeather> list) {
        this.list = list;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}
