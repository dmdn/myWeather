package by.ddv.myweather.model_daily_forecast;

import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class CommonDailyForecast {
    public static String API_LINK2 = "http://api.openweathermap.org/data/2.5/forecast/daily";
    public static String API_KEY2 = "a859e46a9d81552de0bb61ef65490f23";


    //http://api.openweathermap.org/data/2.5/forecast/daily?lat=53.9&lon=27.57&APPID=a859e46a9d81552de0bb61ef65490f23&units=metric

    @NonNull
    public static String apiRequest(String lat, String lng, String unitsFormat){
        StringBuilder sb = new StringBuilder(API_LINK2);

        sb.append(String.format("?lat=%s&lon=%s&APPID=%s%s", lat, lng, API_KEY2, unitsFormat(unitsFormat)));
        return sb.toString();
    }

    @NonNull
    public static String apiRequest2(String prefManualLocation, String unitsFormat){
        StringBuilder sb = new StringBuilder(API_LINK2);

        sb.append(String.format("?q=%s&APPID=%s%s", prefManualLocation, API_KEY2, unitsFormat(unitsFormat)));
        return sb.toString();
    }


    public static String getImage(String icon){
        return String.format("http://openweathermap.org/img/w/%s.png", icon);
    }


    public static String unixTimeStampToDateTime(double unixTimeStamp){
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy - EE", Locale.ENGLISH);
        Date date = new Date();
        date.setTime((long)unixTimeStamp*1000);
        return dateFormat.format(date);
    }

    public static String getDateNow(){
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private static String unitsFormat(String unit){
        String u;
        switch(unit){
            case "metric":
                u = "&units=metric";
                break;
            case "standart":
                u = "";
                break;
            case "imperial":
                u = "&units=imperial";
                break;
            default:
                u = "&units=metric";
        }
        return u;
    }

}
