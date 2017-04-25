package by.ddv.myweather.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

import by.ddv.myweather.Helper;
import by.ddv.myweather.MainActivity;
import by.ddv.myweather.R;
import by.ddv.myweather.model_daily_forecast.CommonDailyForecast;



public class DailyForecastFragment extends Fragment {

    public DailyForecastFragment() {
    }

    private static String stream = null;

    private String prefManualLocation;
    public static String prefUunitFormat;
    public static Boolean prefSwitchGeolocation;
    private String prefnumberDays;
    private int numberDays;

    private SharedPreferences preferences = null;
    private SharedPreferences preferencesJson;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_forecast, container, false);

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();

        removeFragment();

        prefnumberDays = preferences.getString(getString(R.string.pref_number_days), "");

        if (prefnumberDays.isEmpty()){
            prefnumberDays = "3";
        }
        numberDays = Integer.parseInt(prefnumberDays);

        prefManualLocation = preferences.getString(getString(R.string.pref_manual_location), "");
        prefSwitchGeolocation = preferences.getBoolean(getString(R.string.pref_geolocation_enabled), true);
        prefUunitFormat = preferences.getString(getString(R.string.pref_unit_format), "");


        if (CurrentWeatherFragment.hasConnection(getActivity())){

            if (prefSwitchGeolocation){
                double lat = MainActivity.lat;
                double lng = MainActivity.lng;

                new GetDailyWeather().execute(CommonDailyForecast.apiRequest(String.valueOf(lat),String.valueOf(lng), prefUunitFormat));

            } else {
                new GetDailyWeather().execute(CommonDailyForecast.apiRequest2(prefManualLocation, prefUunitFormat));
            }

        } else {
            getDataWithoutConnectionDaily();
        }


    }


    @Override
    public void onStop() {
        super.onStop();
        saveTextJson();
    }

    private void saveTextJson() {
        if (stream != null){
            preferencesJson = getActivity().getSharedPreferences(getString(R.string.json_preferences), Context.MODE_PRIVATE);
            SharedPreferences.Editor ed = preferencesJson.edit();
            ed.putString(getString(R.string.pref_json_daily), stream);
            ed.apply();
        }

    }

    private void getDataWithoutConnectionDaily() {

        File file= new File("/data/data/by.ddv.myweather/shared_prefs/json_preferences.xml");

        if (file.exists()){

            preferencesJson = this.getActivity().getSharedPreferences((getString(R.string.json_preferences)), Context.MODE_PRIVATE);
            stream = preferencesJson.getString(getString(R.string.pref_json_daily), "");

            for (int i = 0; i < numberDays; i++) {
                generateFragment(i);
            }

        } else {
            LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.container);
            TextView txt = new TextView(this.getActivity());
            txt.setText(getString(R.string.no_data) + " (" + getString(R.string.not_have_internet) + ")");
            txt.setTextSize(25);
            txt.setPadding(16, 0, 0, 0);
            layout.addView(txt);
        }

    }


    private class GetDailyWeather extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

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

            for (int i = 0; i < numberDays; i++) {
                generateFragment(i);
            }
        }
    }


    public void generateFragment(int i) {

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        String fragmentTag = Integer.toString(i);

        DailyForecastFragmentItem dailyForecastFragmentItem = new DailyForecastFragmentItem();

        fragmentTransaction.add(R.id.container, dailyForecastFragmentItem, fragmentTag);
        fragmentTransaction.commit();

        DailyForecastFragmentItem.json = stream;

    }



    void removeFragment(){
        LinearLayout lLayout = (LinearLayout) getActivity().findViewById(R.id.container);
        if (lLayout.getChildCount() != 0){
            lLayout.removeAllViews();
        }
    }


}
