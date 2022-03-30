package com.example.myapplication;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.Executor;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    String CITY = "";
    String API = "85c5a62b4b095f1ad2e957d9b84421ad";
    List<Day> days = new ArrayList<Day>();
    int currentTemp = 0;
    private String stringLatitude = "";
    private String stringLongitude = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SearchView search = findViewById(R.id.searchView);
        new weatherTask().execute();
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                CITY = query;
                new weatherTask().execute();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        ImageView refresh = (ImageView) findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                new weatherTask().execute();
                Toast notification = Toast.makeText(getApplicationContext(), "Refreshed!", Toast.LENGTH_SHORT);
                notification.show();
            }
        });
    }

    public void openPopUp(View view) {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.setAnimationStyle(R.style.animation);
        TextView tempText = (TextView) popupView.findViewById(R.id.popupTemp);
        tempText.setText(currentTemp + "°C");
        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }


    private class weatherTask extends AsyncTask<String, Void, JSONObject> {
        private String readAll(Reader rd) throws IOException {
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            return sb.toString();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            //android.os.Debug.waitForDebugger();
            String response = null;
            JSONObject json = null;
            JSONArray jsonArray = null;
            if(!CITY.isEmpty()) {
                try {
                    String url = new URL("https://api.openweathermap.org/geo/1.0/direct?q=" + CITY + "&limit=1&appid=" + API).toString();
                    InputStream is = null;
                    try {
                        is = new URL(url).openStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                    String jsonText = null;

                    jsonText = readAll(rd);
                    jsonArray = new JSONArray(jsonText);

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

                try {
                    JSONObject city = jsonArray.getJSONObject(0);
                    stringLatitude = city.getString("lat");
                    stringLongitude = city.getString("lon");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            try{
                String url;
                if(stringLongitude.isEmpty())
                {
                    url = new URL("https://api.openweathermap.org/geo/1.0/reverse?lat=44.3302&lon=23.7949&limit=5&appid=" + API).toString();
                }
                else
                {
                    url = new URL("https://api.openweathermap.org/geo/1.0/reverse?lat="+ stringLatitude+"&lon="+ stringLongitude +"&limit=5&appid=" + API).toString();
                }
                InputStream is = null;
                try {
                    is = new URL(url).openStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                String jsonText = null;

                jsonText = readAll(rd);
                jsonArray = new JSONArray(jsonText);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            try {
                JSONObject city = jsonArray.getJSONObject(0);
                CITY = city.getString("name");
            } catch (JSONException e) {
                e.printStackTrace();
            }


            try {

                if(stringLongitude.isEmpty()) {
                    response = new URL("https://api.openweathermap.org/data/2.5/onecall?lat=44.3302&lon=23.7949&exclude=minutely,hourly,alerts&units=metric&appid=bb70dea7da536dd218deb52b98132289").toString();
                }
                else
                {
                    response = new URL("https://api.openweathermap.org/data/2.5/onecall?lat=" + stringLatitude + "&lon=" + stringLongitude + "&exclude=minutely,hourly,alerts&units=metric&appid=bb70dea7da536dd218deb52b98132289").toString();
                }
                InputStream is = null;
                try {
                    is = new URL(response).openStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                String jsonText = null;
                try {
                    jsonText = readAll(rd);
                    json = new JSONObject(jsonText);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        public void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        public void onPostExecute(JSONObject result)
        {
            super.onPostExecute(result);
           //android.os.Debug.waitForDebugger();

            try {
                /* Extracting JSON returns from the API */
                JSONObject current = result.getJSONObject("current");
                JSONObject weather = current.getJSONArray("weather").getJSONObject(0);

                long updatedAt = current.getLong("dt");
                String temp = current.getInt("temp")+"°C";
                currentTemp = current.getInt("temp");
                String feelsLike = "Feels like: " + current.getInt("feels_like") + "°C";
                String pressure = current.getString("pressure");
                String humidity = current.getString("humidity");
                String mainIcon = weather.getString("icon");
                String windSpeed = current.getString("wind_speed");
                String weatherDescription = weather.getString("description");
                long sunrise = current.getLong("sunrise");
                long sunset = current.getLong("sunset");
                String uvi = current.getString("uvi");

                // Populating extracted data into our views

                TextView cityText = (TextView) findViewById(R.id.address);
                cityText.setText(CITY);

                Date dateTime = new Date(updatedAt * 1000L);
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                String dateAndTime = format.format(dateTime);

                TextView dateTimeText = (TextView) findViewById(R.id.updated_at);
                dateTimeText.setText(dateAndTime);

                ImageView picture = (ImageView) findViewById(R.id.currentDay);
                Picasso.get().load("https://openweathermap.org/img/wn/" + mainIcon +"@4x.png").into(picture);

                TextView feelsLikeText = (TextView) findViewById(R.id.temp_min);
                feelsLikeText.setText(feelsLike);

                TextView tempText = (TextView) findViewById(R.id.temp);
                tempText.setText(temp);

                TextView statusText = (TextView) findViewById(R.id.status);
                statusText.setText(weatherDescription.substring(0,1).toUpperCase() + weatherDescription.substring(1));

                SimpleDateFormat formatHour = new SimpleDateFormat("HH:mm");

                Date sunriseHour = new Date(sunrise * 1000L);
                String sunriseTime = formatHour.format(sunriseHour);
                TextView sunriseText = (TextView) findViewById(R.id.sunrise);
                sunriseText.setText(sunriseTime);

                Date sunsetHour = new Date(sunset * 1000L);
                String sunsetTime = formatHour.format(sunsetHour);
                TextView sunsetText = (TextView) findViewById(R.id.sunset);
                sunsetText.setText(sunsetTime);

                TextView windText = (TextView) findViewById(R.id.wind_speed);
                windText.setText(windSpeed + " km/h");

                TextView humidityText = (TextView) findViewById(R.id.humidity);
                humidityText.setText(humidity + "%");

                TextView pressureText = (TextView) findViewById(R.id.pressure);
                pressureText.setText(pressure + "hPa");

                TextView uviText = (TextView) findViewById(R.id.uvi);
                uviText.setText(uvi);


                JSONArray daily = result.getJSONArray("daily");
                JSONObject obj = null;

                for (int i=1; i < daily.length(); i++) {
                    obj = daily.getJSONObject(i);

                    String epochString = obj.getString("dt");
                    long epoch = Long.parseLong(epochString);
                    Date date = new Date(epoch * 1000);
                    int min = obj.getJSONObject("temp").getInt("min");
                    int max = obj.getJSONObject("temp").getInt("max");
                    String icon = obj.getJSONArray("weather").getJSONObject(0).getString("icon");

                    Day day = new Day(date.toString().substring(0, 10), min, max, icon);
                    days.add(day);
                }

                for(int i = 0; i <= 5; i++) {
                    String dayId = "day" + (i + 1);
                    int id = getResources().getIdentifier(dayId, "id", getPackageName());

                    TextView day = (TextView) findViewById(id);
                    day.setText(days.get(i).name);

                    ImageView dailyPicture = (ImageView) findViewById(getResources().getIdentifier("icon" + (i + 1), "id", getPackageName()));
                    Picasso.get().load("https://openweathermap.org/img/wn/" + days.get(i).icon +"@4x.png").into(dailyPicture);

                    TextView dailyTemp = (TextView) findViewById(getResources().getIdentifier("temp" + (i + 1), "id", getPackageName()));
                    dailyTemp.setText("Min: " + days.get(i).min + "°C\nMax: " + days.get(i).max + "°C");
                }
                days.clear();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

}
