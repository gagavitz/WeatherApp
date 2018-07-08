package com.gagavitz.weatherapp;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class PlaceIdTask extends AsyncTask<String, Void, JSONObject> {

    public AsyncResponse delegate = null;
    private final static String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?lat=37.9358&lon=-122.3477&units=imperial";
    private final static String WEATHER_API = "4a759aaff0e075e454e38a07352fab8c";

    public static JSONObject getWeatherJSON(String lat, String lon) {
        try {
            URL url = new URL(WEATHER_URL + "&appid=" + WEATHER_API);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer json = new StringBuffer(1024);
            String tmp;

            while ((tmp = reader.readLine()) != null) {
                json.append(tmp).append("\n");
            }
            reader.close();

            JSONObject data = new JSONObject((json.toString()));

            if (data.getInt("cod") == 200) {
                return data;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static String setWeatherIcon(int actualId, long sunrise, long sunset) {
        int id = actualId / 100;
        String icon = "";

        if (actualId == 800) {
            long currentTime = new Date().getTime();

            if (currentTime >= sunrise && currentTime < sunset) {
                icon = "&#xf00d;";
            } else {
                icon = "&#xf02e;";
            }
        } else {
            switch(id) {
                case 2 : icon = "&#xf01e;";
                    break;
                case 3 : icon = "&#xf01c;";
                    break;
                case 7 : icon = "&#xf014;";
                    break;
                case 8 : icon = "&#xf013;";
                    break;
                case 6 : icon = "&#xf01b;";
                    break;
                case 5 : icon = "&#xf019;";
                    break;
            }
        }
        return icon;
    }

    @Override
    protected JSONObject doInBackground(String... degrees) {
        JSONObject jsonWeather = null;

        try {
            jsonWeather = getWeatherJSON(degrees[0], degrees[1]);
        } catch (Exception e) {
            Log.d("Error", "Cannot find location", e);
        }

        return jsonWeather;
    }

    public PlaceIdTask (AsyncResponse asyncResponse) {
        delegate = asyncResponse;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {

        //Gets all the string values for the interface AsyncResponse and the variable delegate
        try {
            if (jsonObject != null) {
                JSONObject details = jsonObject.getJSONArray("weather").getJSONObject(0);
                JSONObject main = jsonObject.getJSONObject("main");
                DateFormat df = DateFormat.getDateTimeInstance();
                String city = jsonObject.getString("name").toUpperCase(Locale.US) + ", " + jsonObject.getJSONObject("sys").getString("country");
                String description = details.getString("description").toUpperCase(Locale.US);
                String temperature = String.format("%.2f", main.getDouble("temp")) + "°";
                String humidity = String.format("%.0f", main.getDouble("humidity")) + "%";
                String pressure = String.format("%.0f", main.getDouble("pressure")) + "hPa";
                String updateOn = df.format(new Date(jsonObject.getLong("dt") * 1000));
                String iconText = setWeatherIcon(details.getInt("id"), jsonObject.getJSONObject("sys").getLong("sunrise") * 1000, jsonObject.getJSONObject("sys").getLong("sunset") * 1000);

                delegate.processFinish(city, description, temperature, humidity, pressure, updateOn, iconText, "" + (jsonObject.getJSONObject("sys").getLong("sunrise") * 1000));
            }
        }

        //Empty exception catch to help the program run.  This part of the code
        // does nothing.
        catch (JSONException e) {
            //Log.e(LOG_TAG, "Cannot process JSON results", e);
        }
    }
}
