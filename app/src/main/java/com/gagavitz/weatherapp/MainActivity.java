package com.gagavitz.weatherapp;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    TextView cityField, detailsField, currentTemperatureField, humidityField, pressureField, weatherIcon, updatedField;
    PlaceIdTask asyncTask;

    Typeface weatherFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        //Set values for variables
        weatherFont = Typeface.createFromAsset(getAssets(), "fonts/weathericons-regular-webfont.ttf");
        cityField = findViewById(R.id.tvCityField);
        updatedField = findViewById(R.id.tvUpdatedField);
        detailsField = findViewById(R.id.tvDetailsField);
        currentTemperatureField = findViewById(R.id.tvCurrentTemperatureField);
        humidityField = findViewById(R.id.tvHumidityField);
        pressureField = findViewById(R.id.tvPressureField);
        weatherIcon = findViewById(R.id.tvWeatherIcon);
        weatherIcon.setTypeface(weatherFont);

        asyncTask = new PlaceIdTask(new AsyncResponse() {
            @Override
            public void processFinish(String weatherCity, String weatherDescription, String weatherTemperature, String weatherHumidity, String weatherPressure, String weatherUpdatedOn, String weatherIconText, String sunrise) {
                cityField.setText(weatherCity);
                updatedField.setText(weatherUpdatedOn);
                detailsField.setText(weatherDescription);
                currentTemperatureField.setText(weatherTemperature);
                humidityField.setText("Humidity: " + weatherHumidity);
                pressureField.setText("Pressure: " + weatherPressure);
                weatherIcon.setText(Html.fromHtml(weatherIconText));
            }
        });

        asyncTask.execute("-122.3477", "37.9358"); //Lon and lat for Richmond, CA
    }
}
