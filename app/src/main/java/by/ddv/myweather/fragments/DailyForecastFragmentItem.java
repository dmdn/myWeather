package by.ddv.myweather.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import by.ddv.myweather.R;
import by.ddv.myweather.model_daily_forecast.CommonDailyForecast;
import by.ddv.myweather.model_daily_forecast.OpenWeatherMapDaily;


public class DailyForecastFragmentItem extends Fragment {

    public static int tag;
    public static String json;

    private TextView tvDate, tvDescription, tvHumidity, tvPressure;
    private TextView tvCity, tvLastUpdate;
    private TextView tvTempMorn, tvTempDay,tvTempEve, tvTempNight, tvWind;
    private ImageView ivIcon, ivIconItem;

    private int iconItem;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.item_fragment_daily_forecast, container, false);

        tag = Integer.parseInt(getTag());

        if (tag % 2 == 0){
            view.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.colorBodyItem));
        }

        if (json != null ){
            tvDate = (TextView) view.findViewById(R.id.tvDate);
            tvHumidity = (TextView) view.findViewById(R.id.tvHumidity);
            tvPressure = (TextView) view.findViewById(R.id.tvPressure);
            tvDescription = (TextView) view.findViewById(R.id.tvDescription);
            ivIcon = (ImageView) view.findViewById(R.id.ivIcon);

            tvTempMorn = (TextView) view.findViewById(R.id.tvTempMorn);
            tvTempDay = (TextView) view.findViewById(R.id.tvTempDay);
            tvTempEve = (TextView) view.findViewById(R.id.tvTempEve);
            tvTempNight = (TextView) view.findViewById(R.id.tvTempNight);
            tvWind = (TextView) view.findViewById(R.id.tvWind);

            ivIconItem = (ImageView) view.findViewById(R.id.iconWindItem);

            tvCity = (TextView) view.findViewById(R.id.tvCity);
            tvLastUpdate = (TextView) view.findViewById(R.id.tvLastUpdate);

            GsonBuilder builger = new GsonBuilder();
            Gson gson = builger.create();
            OpenWeatherMapDaily openWeatherMapDaily = gson.fromJson(json, OpenWeatherMapDaily.class);


            tvCity.setText(String.format("%s, %s", openWeatherMapDaily.getCity().getName(), openWeatherMapDaily.getCity().getCountry()));
            tvLastUpdate.setText(String.format("Last upd.: %s", CommonDailyForecast.getDateNow()));

            tvDate.setText(CommonDailyForecast.unixTimeStampToDateTime(openWeatherMapDaily.getList().get(tag).getDt()));

            tvTempMorn.setText(String.format("Morning: %.1f %s",openWeatherMapDaily.getList().get(tag).getTemp().getMorn(), temperatureUnitsFormat(DailyForecastFragment.prefUunitFormat)));
            tvTempDay.setText(String.format("Day: %.1f %s",openWeatherMapDaily.getList().get(tag).getTemp().getDay(), temperatureUnitsFormat(DailyForecastFragment.prefUunitFormat)));
            tvTempEve.setText(String.format("Evening: %.1f %s",openWeatherMapDaily.getList().get(tag).getTemp().getEve(), temperatureUnitsFormat(DailyForecastFragment.prefUunitFormat)));
            tvTempNight.setText(String.format("Night: %.1f %s",openWeatherMapDaily.getList().get(tag).getTemp().getNight(), temperatureUnitsFormat(DailyForecastFragment.prefUunitFormat)));

            tvHumidity.setText(String.format("Humidity: %d %%",openWeatherMapDaily.getList().get(tag).getHumidity()));
            tvPressure.setText(String.format("Pressure: %s hpa" ,openWeatherMapDaily.getList().get(tag).getPressure()));
            tvDescription.setText(String.format("%s", openWeatherMapDaily.getList().get(tag).getWeather().get(0).getDescription()));

            tvWind.setText(String.format("Wind: %s %s" ,openWeatherMapDaily.getList().get(tag).getSpeed(), speedUnitsFormat(DailyForecastFragment.prefUunitFormat)));
            iconItem = openWeatherMapDaily.getList().get(tag).getDeg();
            ivIconItem.setImageResource(getDrawWindItem(iconItem));

            Picasso.with(getActivity())
                    .load(CommonDailyForecast.getImage(openWeatherMapDaily.getList().get(tag).getWeather().get(0).getIcon()))
                    .into(ivIcon);

        }


        return view;
    }


    private String temperatureUnitsFormat(String unit){
        String t;
        switch(unit){
            case "metric":
                t = "°C";
                break;
            case "standart":
                t = "°K";
                break;
            case "imperial":
                t = "°F";
                break;
            default:
                t = "°C";
        }
        return t;
    }


    private String speedUnitsFormat(String unit){
        if (unit.equals("imperial")){
            return "ml/hr";
        } else return "m/s";
    }


    public int getDrawWindItem(int n) {
        int drowWind;

        if (n > 293 && n <= 338) {
            drowWind = R.drawable.wind_315;
        } else if (n > 23 && n <= 68) {
            drowWind = R.drawable.wind_45;
        } else if (n > 68 && n <= 113) {
            drowWind = R.drawable.wind_90;
        } else if (n > 113 && n <= 158) {
            drowWind = R.drawable.wind_135;
        } else if (n > 158 && n <= 203) {
            drowWind = R.drawable.wind_180;
        } else if (n > 203 && n <= 248) {
            drowWind = R.drawable.wind_225;
        } else if (n > 248 && n <= 293) {
            drowWind = R.drawable.wind_270;
        } else if (n > 293 && n <= 338) {
            drowWind = R.drawable.wind_315;
        } else {
            drowWind = R.drawable.wind_0;
        }

        return drowWind;
    }



}
