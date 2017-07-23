package by.ddv.myweather.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.lang.reflect.Type;

import by.ddv.myweather.Helper;
import by.ddv.myweather.MainActivity;
import by.ddv.myweather.R;
import by.ddv.myweather.model_current_weather.CommonCurrentWeather;
import by.ddv.myweather.model_current_weather.OpenWeatherMap;
import by.ddv.myweather.widgets.Widget;


public class CurrentWeatherFragment extends Fragment {

    public static String stream = null;

    public static Boolean prefSwitchGeolocation;

    private String prefManualLocation;
    public static String prefUunitFormat;

    private ImageView ivCurrentIcon;
    private ImageView ivIcon;
    private TextView tvCurrentCity;
    private TextView tvCurrentDate;
    private TextView tvCurrentTemp;
    private TextView tvCurrentWind;
    private TextView tvCurrentHumidity;
    private TextView tvCurrentPressure;
    private TextView tvCurrentDescription;
    private TextView tvCurrentLastUpdate;
    private TextView tvProvider;

    private double icon;

    private SharedPreferences preferences = null;
    private SharedPreferences preferencesJson;

    OpenWeatherMap openWeatherMap = new OpenWeatherMap();
    private String json;

    public CurrentWeatherFragment() {
    }

    private AdView mAdView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_current_weather, container, false);

        view.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.colorBodyItem));

        ivCurrentIcon = (ImageView) view.findViewById(R.id.ivCurrentIcon);
        tvCurrentDate = (TextView) view.findViewById(R.id.tvCurrentDate);
        tvCurrentTemp = (TextView) view.findViewById(R.id.tvCurrentTemp);
        tvCurrentWind = (TextView) view.findViewById(R.id.tvCurrentWind);
        tvCurrentCity = (TextView) view.findViewById(R.id.tvCurrentCity);
        tvCurrentHumidity = (TextView) view.findViewById(R.id.tvCurrentHumidity);
        tvCurrentPressure = (TextView) view.findViewById(R.id.tvCurrentPressure);
        tvCurrentDescription = (TextView) view.findViewById(R.id.tvCurrentDescription);
        tvCurrentLastUpdate = (TextView) view.findViewById(R.id.tvCurrentLastUpdate);
        ivIcon = (ImageView) view.findViewById(R.id.icon_wind);

        tvProvider = (TextView) view.findViewById(R.id.tvProvider);


        // Mobile Ads AdMob (Test banner)
        //MobileAds.initialize(view.getContext(), "ca-app-pub-8065368927185750~7222625111");
        mAdView = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)//Causes a device to receive test ads
                //.addTestDevice("F809CF07739B068B492ABA8E8CED65E0")
                .build();
        mAdView.loadAd(adRequest);


        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        mAdView.resume();//Restore the visibility of banner

        prefManualLocation = preferences.getString(getString(R.string.pref_manual_location), "");
        prefSwitchGeolocation = preferences.getBoolean(getString(R.string.pref_geolocation_enabled), true);
        prefUunitFormat = preferences.getString(getString(R.string.pref_unit_format), "");

        if (hasConnection(getActivity())){


            if (prefSwitchGeolocation){

                double lat = MainActivity.lat;
                double lng = MainActivity.lng;
                new GetWeather().execute(CommonCurrentWeather.apiRequest(String.valueOf(lat),String.valueOf(lng), prefUunitFormat));

                try {
                    tvProvider.setText(getString(R.string.provider_geolocation) + ": " + MainActivity.provider);
                } catch (Exception e) {
                    e.printStackTrace();
                    tvProvider.setText(getString(R.string.no_data));
                }
            } else {

                new GetWeather().execute(CommonCurrentWeather.apiRequest2(prefManualLocation, prefUunitFormat));

                try {
                    tvProvider.setText(getString(R.string.no_provider));
                } catch (Exception e) {
                    e.printStackTrace();
                    tvProvider.setText(getString(R.string.no_data));
                }
            }

        } else {
            getDataWithoutConnection();
            tvProvider.setText(getString(R.string.no_internet));

            showAlertDialog(getActivity(), getString(R.string.no_internet), getString(R.string.not_have_internet), false);
        }
    }

    private void getDataWithoutConnection() {
        GsonBuilder builger = new GsonBuilder();
        Gson gson = builger.create();
        preferencesJson = this.getActivity().getSharedPreferences(getString(R.string.json_preferences), Context.MODE_PRIVATE);

        File file= new File("/data/data/by.ddv.myweather/shared_prefs/json_preferences.xml");
        if (file.exists()){

            json = preferencesJson.getString(getString(R.string.pref_json_widget), "");
            OpenWeatherMap openWeatherMap = gson.fromJson(json, OpenWeatherMap.class);

            tvCurrentCity.setText(String.format("%s, %s", openWeatherMap.getName(), openWeatherMap.getSys().getCountry()));

            tvCurrentDate.setText(String.format("Today is: %s", CommonCurrentWeather.getDateNow()));
            tvCurrentDescription.setText(String.format("%s", openWeatherMap.getWeather().get(0).getDescription()));
            tvCurrentHumidity.setText(String.format("Humidity: %d %%", openWeatherMap.getMain().getHumidity()));

            tvCurrentTemp.setText(String.format("Temperature: %.1f %s", openWeatherMap.getMain().getTemp(), temperatureUnitsFormat(prefUunitFormat)));

            tvCurrentWind.setText(String.format("Wind: %s %s" , openWeatherMap.getWind().getSpeed(), speedUnitsFormat(prefUunitFormat)));
            icon = openWeatherMap.getWind().getDeg();
            ivIcon.setImageResource(getDrawWind(icon));

            tvCurrentPressure.setText(String.format("Pressure: %s hpa" ,openWeatherMap.getMain().getPressure()));

            tvCurrentLastUpdate.setText(String.format("Last Updated: %s", CommonCurrentWeather.unixTimeStampToDateTime(openWeatherMap.getDt())));

            ivCurrentIcon.setImageResource(getDraw(openWeatherMap.getWeather().get(0).getIcon()));
       }

    }


    @Override
    public void onStop() {
        super.onStop();
        saveTextJson();
    }


    @Override
    public void onPause() {
        mAdView.pause();//Suspension of banner display

        super.onPause();
    }

    @Override
    public void onDestroy() {
        mAdView.destroy();//Destruction of banner

        super.onDestroy();
    }

    private void saveTextJson() {
        if (stream != null){
            preferencesJson = getActivity().getSharedPreferences(getString(R.string.json_preferences), Context.MODE_PRIVATE);
            SharedPreferences.Editor ed = preferencesJson.edit();
            ed.putString(getString(R.string.pref_json_widget), stream);
            ed.apply();

            SharedPreferences.Editor ed2 = preferencesJson.edit();
            ed2.putString(getString(R.string.pref_json_update), String.format("Last weather update: %s", Widget.getDateNowWidget()));
            ed2.apply();


        }

    }


    private class GetWeather extends AsyncTask<String,Void,String> {
        ProgressDialog pd = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setTitle(getString(R.string.wait));
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String urlString = params[0];

            Helper http = new Helper();
            stream = http.getHTTPData(urlString);
            return stream;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.contains(getString(R.string.not_found_city))){
                pd.dismiss();
                return;
            }

            Gson gson = new Gson();
            Type mType = new TypeToken<OpenWeatherMap>(){}.getType();
            openWeatherMap = gson.fromJson(s,mType);

            pd.dismiss();

            String cityString = openWeatherMap.getName();
            String cityCountry = openWeatherMap.getSys().getCountry();

            tvCurrentCity.setText(String.format("%s, %s", cityString, cityCountry));

            tvCurrentCity.setText(String.format("%s, %s", openWeatherMap.getName(), openWeatherMap.getSys().getCountry()));
            tvCurrentDate.setText(String.format("Today is: %s", CommonCurrentWeather.getDateNow()));
            tvCurrentDescription.setText(String.format("%s", openWeatherMap.getWeather().get(0).getDescription()));
            tvCurrentHumidity.setText(String.format("Humidity: %d %%", openWeatherMap.getMain().getHumidity()));

            tvCurrentTemp.setText(String.format("Temperature: %.1f %s", openWeatherMap.getMain().getTemp(), temperatureUnitsFormat(prefUunitFormat)));

            tvCurrentWind.setText(String.format("Wind: %s %s" , openWeatherMap.getWind().getSpeed(), speedUnitsFormat(prefUunitFormat)));
            icon = openWeatherMap.getWind().getDeg();
            ivIcon.setImageResource(getDrawWind(icon));

            tvCurrentPressure.setText(String.format("Pressure: %s hpa" ,openWeatherMap.getMain().getPressure()));

            tvCurrentLastUpdate.setText(String.format("Last Updated: %s", CommonCurrentWeather.unixTimeStampToDateTime(openWeatherMap.getDt())));


            Picasso.with(getActivity())
                    .load(CommonCurrentWeather.getImage(openWeatherMap.getWeather().get(0).getIcon()))
                    .into(ivCurrentIcon);

        }

    }


    public static boolean hasConnection(final Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        return false;
    }


    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setIcon(R.drawable.no_conection);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }



    public static String temperatureUnitsFormat(String unit){
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


    public static String speedUnitsFormat(String unit){
        if (unit.equals("imperial")){
            return "ml/hr";
        } else return "m/s";
    }


    public int getDrawWind(double n) {
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


    public static int getDraw(String n) {
        int drow;
        switch(n){
            case "01d":
                drow = R.drawable.ic_01d;
                break;
            case "02d":
                drow = R.drawable.ic_02d;
                break;
            case "03d":
                drow = R.drawable.ic_03d;
                break;
            case "04d":
                drow = R.drawable.ic_04d;
                break;
            case "09d":
                drow = R.drawable.ic_09d;
                break;
            case "10d":
                drow = R.drawable.ic_10d;
                break;
            case "11d":
                drow = R.drawable.ic_11d;
                break;
            case "13d":
                drow = R.drawable.ic_13d;
                break;
            case "50d":
                drow = R.drawable.ic_50d;
                break;
            case "01n":
                drow = R.drawable.ic_01n;
                break;
            case "02n":
                drow = R.drawable.ic_02n;
                break;
            case "03n":
                drow = R.drawable.ic_03n;
                break;
            case "04n":
                drow = R.drawable.ic_04n;
                break;
            case "09n":
                drow = R.drawable.ic_09n;
                break;
            case "10n":
                drow = R.drawable.ic_10n;
                break;
            case "11n":
                drow = R.drawable.ic_11n;
                break;
            case "13n":
                drow = R.drawable.ic_13n;
                break;
            case "50n":
                drow = R.drawable.ic_50n;
                break;
            default:
                drow = R.drawable.no_image;
        }
        return drow;
    }
}
