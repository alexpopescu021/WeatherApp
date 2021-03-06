package com.example.myapplication;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
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

public class MainActivity extends AppCompatActivity {
    private Button button_notify;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private static final String ACTION_UPDATE_NOTIFICATION =
            "com.android.example.notifyme.ACTION_UPDATE_NOTIFICATION";
    private NotificationManager mNotifyManager;
    private static final int NOTIFICATION_ID = 0;
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    String CITY = "";
    String API = "85c5a62b4b095f1ad2e957d9b84421ad";
    List<Day> days = new ArrayList<Day>();
    int currentTemp = 0;
    private String stringLatitude = "";
    private String stringLongitude = "";
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }

        find_Location();
        SearchView search = findViewById(R.id.searchView);
        createNotificationChannel();

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
        refresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new weatherTask().execute();

                Toast notification = Toast.makeText(getApplicationContext(), "Refreshed!", Toast.LENGTH_SHORT);
                notification.show();
            }
        });

        ImageView location = (ImageView) findViewById(R.id.location);
        location.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                find_Location();
                CITY = "";
                new weatherTask().execute();

            }
        });

        // Add onClick handlers to all the buttons.
        button_notify = findViewById(R.id.notify);
        button_notify.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                // Send the notification
                sendNotification();
            }
        });
        setNotificationButtonState(true, false, false);
    }

    public void find_Location() {
        String location_context = Context.LOCATION_SERVICE;
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(location_context);
        List<String> providers = locationManager.getProviders(true);
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(provider, 1000, 0,
                    new LocationListener() {

                        public void onLocationChanged(Location location) {
                        }

                        public void onProviderDisabled(String provider) {
                        }

                        public void onProviderEnabled(String provider) {
                        }

                        public void onStatusChanged(String provider, int status,
                                                    Bundle extras) {
                        }
                    });
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                stringLatitude = String.valueOf(location.getLatitude());
                stringLongitude = String.valueOf(location.getLongitude());
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void sendNotification() {

        // Sets up the pending intent to update the notification.
        // Corresponds to a press of the Update Me! button.
        Intent updateIntent = new Intent(ACTION_UPDATE_NOTIFICATION);
        PendingIntent updatePendingIntent = PendingIntent.getBroadcast(this,
                NOTIFICATION_ID, updateIntent, PendingIntent.FLAG_IMMUTABLE);

        // Build the notification with all of the parameters using helper
        // method.
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();

        // Deliver the notification.
        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());

        // Enable the update and cancel buttons but disables the "Notify
        // Me!" button.
        setNotificationButtonState(false, true, true);
    }

    void setNotificationButtonState(Boolean isNotifyEnabled, Boolean
            isUpdateEnabled, Boolean isCancelEnabled) {
        button_notify.setEnabled(isNotifyEnabled);

    }


    private NotificationCompat.Builder getNotificationBuilder() {

        // Set up the pending intent that is delivered when the notification
        // is clicked.
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity
                (this, NOTIFICATION_ID, notificationIntent,
                        PendingIntent.FLAG_IMMUTABLE);

        // Build the notification with all of the parameters.
        NotificationCompat.Builder notifyBuilder = new NotificationCompat
                .Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle(CITY)
                .setContentText(String.valueOf(currentTemp))
                .setSmallIcon(R.drawable.ic_sunrise)
                .setAutoCancel(true).setContentIntent(notificationPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
        return notifyBuilder;
    }

    public void createNotificationChannel() {
        mNotifyManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {
            // Create a NotificationChannel
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID,
                    "Mascot Notification", NotificationManager
                    .IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Mascot");
            mNotifyManager.createNotificationChannel(notificationChannel);
        }

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
        tempText.setText(currentTemp + "??C");
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
           // android.os.Debug.waitForDebugger();


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
            try{        // reverse geocoding
                String url;
                if(stringLatitude == null && stringLatitude.isEmpty() && stringLatitude.trim().isEmpty())
                {
                    url = new URL("https://api.openweathermap.org/geo/1.0/reverse?lat="+ stringLatitude+"&lon="+ stringLongitude +"&limit=5&appid=" + API).toString();
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

                if(stringLatitude == null && stringLatitude.isEmpty() && stringLatitude.trim().isEmpty())
                {
                    response = new URL("https://api.openweathermap.org/data/2.5/onecall?lat=" + stringLatitude + "&lon=" + stringLongitude + "&exclude=minutely,hourly,alerts&units=metric&appid=bb70dea7da536dd218deb52b98132289").toString();
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
                String temp = current.getInt("temp")+"??C";
                currentTemp = current.getInt("temp");
                String feelsLike = "Feels like: " + current.getInt("feels_like") + "??C";
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
                    dailyTemp.setText("Min: " + days.get(i).min + "??C\nMax: " + days.get(i).max + "??C");
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
