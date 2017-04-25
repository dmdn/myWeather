package by.ddv.myweather.widgets;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import by.ddv.myweather.MainActivity;
import by.ddv.myweather.R;
import by.ddv.myweather.fragments.CurrentWeatherFragment;
import by.ddv.myweather.model_current_weather.OpenWeatherMap;


public class Widget extends AppWidgetProvider {

    private String unitFormatWidget;

    private SharedPreferences preferences;
    private SharedPreferences preferencesJson;

    private double iconWindWidget;

    private String json, sringWidgetJsonUpdate;

    private String icon, sringCityWidget, sringTempWidget, sringWindWidget, stringPressureWidget, stringHumidityWidget, stringDescriptionWidget;



    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        unitFormatWidget = CurrentWeatherFragment.prefUunitFormat;


        if (unitFormatWidget == null){
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
            unitFormatWidget = preferences.getString(context.getString(R.string.pref_unit_format), "");
        }

        if (unitFormatWidget == null){
            unitFormatWidget = "metric";
        }

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
        Intent intent = new Intent(context, MainActivity.class);

        PendingIntent pending = PendingIntent.getActivity(context, 0, intent, 0);


        GsonBuilder builger = new GsonBuilder();
        Gson gson = builger.create();

        json = CurrentWeatherFragment.stream;

        if (json == null){

            try {
                preferencesJson = context.getSharedPreferences(context.getString(R.string.json_preferences), context.MODE_PRIVATE);
                json = preferencesJson.getString(context.getString(R.string.pref_json_widget), "");
                sringWidgetJsonUpdate  = preferencesJson.getString(context.getString(R.string.pref_json_update), "");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        OpenWeatherMap openWeatherMap = gson.fromJson(json, OpenWeatherMap.class);

        sringTempWidget = String.format("%.1f %s", openWeatherMap.getMain().getTemp(), CurrentWeatherFragment.temperatureUnitsFormat(unitFormatWidget));
        sringCityWidget = String.format("%s, %s", openWeatherMap.getName(), openWeatherMap.getSys().getCountry());
        stringDescriptionWidget = String.format("%s", openWeatherMap.getWeather().get(0).getDescription());
        stringHumidityWidget = String.format("%d %%", openWeatherMap.getMain().getHumidity());
        sringWindWidget = String.format("%s %s", openWeatherMap.getWind().getSpeed(), CurrentWeatherFragment.speedUnitsFormat(unitFormatWidget));
        stringPressureWidget = String.format("%s hpa" ,openWeatherMap.getMain().getPressure());
        icon = openWeatherMap.getWeather().get(0).getIcon();


        remoteViews.setImageViewResource(R.id.iconWetherWidget, CurrentWeatherFragment.getDraw(icon));

        iconWindWidget = openWeatherMap.getWind().getDeg();
        remoteViews.setImageViewResource(R.id.iconWindWidget, getDrawWind(iconWindWidget));
        
        remoteViews.setTextViewText(R.id.widgetTemp, sringTempWidget);
        remoteViews.setTextViewText(R.id.widgetCity, sringCityWidget);
        remoteViews.setTextViewText(R.id.widgetHumidity, stringHumidityWidget);
        remoteViews.setTextViewText(R.id.widgetPressure, stringPressureWidget);
        remoteViews.setTextViewText(R.id.widgetDescription, stringDescriptionWidget);
        remoteViews.setTextViewText(R.id.widgetWind, sringWindWidget);

        remoteViews.setTextViewText(R.id.widgetJsonUpdate, sringWidgetJsonUpdate);

        remoteViews.setTextViewText(R.id.lastUpdateWidget, String.format("Updated: %s", getDateNowWidget()));

        remoteViews.setOnClickPendingIntent(R.id.widgetButtonRefresh, pending);
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
    }


    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }


    public static String getDateNowWidget(){
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        Date date = new Date();
        return dateFormat.format(date);
    }



    public int getDrawWind(double n) {
        int drowWind;

        if (n > 293 && n <= 338) {
            drowWind = R.drawable.wind_315_w;
        } else if (n > 23 && n <= 68) {
            drowWind = R.drawable.wind_45_w;
        } else if (n > 68 && n <= 113) {
            drowWind = R.drawable.wind_90_w;
        } else if (n > 113 && n <= 158) {
            drowWind = R.drawable.wind_135_w;
        } else if (n > 158 && n <= 203) {
            drowWind = R.drawable.wind_180_w;
        } else if (n > 203 && n <= 248) {
            drowWind = R.drawable.wind_225_w;
        } else if (n > 248 && n <= 293) {
            drowWind = R.drawable.wind_270_w;
        } else if (n > 293 && n <= 338) {
            drowWind = R.drawable.wind_315_w;
        } else {
            drowWind = R.drawable.wind_0_w;
        }

        return drowWind;
    }


}
