package com.example.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    String CITY = "craiova,ro";
    String API = "85c5a62b4b095f1ad2e957d9b84421ad";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        new weatherTask().execute();
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


    private class weatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String response = null;
            try {
                response = new URL("https://api.openweathermap.org/data/2.5/weather?q=" + CITY + "&units=metric&appid=" + API).toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        public void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        public void onPostExecute(String result)
        {
            super.onPostExecute(result);
            android.os.Debug.waitForDebugger();


           /* URL url = null;
            try {
                url = new URL(result);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            URLConnection yc = null;
            try {
                yc = url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            InputStream stream = yc.getInputStream();
            try {
                in = new BufferedReader(new InputStreamReader(
                        yc.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            String inputLine;
            while (true) {
                try {
                    if (!((inputLine = in.readLine()) != null)) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }*/



            /*URL url = null;
            try {
                url = new URL(result);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }


            // Convert to a JSON object to print data
            JsonParser jp = new JsonParser(); //from gson
            JsonElement root = null; //Convert the input stream to a json element
            try {
                root = jp.parse(new InputStreamReader((InputStream) url.getContent()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.
            */


            try {
                /* Extracting JSON returns from the API */
                JSONObject jsonObj = new JSONObject(result);
                JSONObject main = jsonObj.getJSONObject("main");
                JSONObject sys = jsonObj.getJSONObject("sys");
                JSONObject wind = jsonObj.getJSONObject("wind");
                JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);

                long updatedAt = jsonObj.getLong("dt");
                String updatedAtText = "Updated at: "+ new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(new Date(updatedAt*1000));
                String temp = main.getString("temp")+"°C";
                String tempMin = "Min Temp: " + main.getString("temp_min")+"°C";
                String tempMax = "Max Temp: " + main.getString("temp_max")+"°C";
                String pressure = main.getString("pressure");
                String humidity = main.getString("humidity");

                long sunrise = sys.getLong("sunrise");
                long sunset = sys.getLong("sunset");
                String windSpeed = wind.getString("speed");
                String weatherDescription = weather.getString(String.valueOf(Integer.parseInt("description")));

                String address = jsonObj.getString("name")+", "+sys.getString("country");

                /* Populating extracted data into our views */

                TextView variable = (TextView) findViewById(R.id.address);
                variable.setText(address);
                /*(TextView) findViewById(R.id.address) = address;


                findViewById<TextView>(R.id.updated_at).text =  updatedAtText;
                findViewById<TextView>(R.id.status).text = weatherDescription.capitalize();
                findViewById<TextView>(R.id.temp).text = temp;
                findViewById<TextView>(R.id.temp_min).text = tempMin;
                findViewById<TextView>(R.id.temp_max).text = tempMax;
                findViewById<TextView>(R.id.sunrise).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise*1000));
                findViewById<TextView>(R.id.sunset).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset*1000));
                findViewById<TextView>(R.id.wind).text = windSpeed;
                findViewById<TextView>(R.id.pressure).text = pressure;
                findViewById<TextView>(R.id.humidity).text = humidity;*/

                /* Views populated, Hiding the loader, Showing the main design */
               // findViewById<ProgressBar>(R.id.loader).visibility = View.GONE;
                //findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE;

            } catch (JSONException e) {
                e.printStackTrace();
               // findViewById<ProgressBar>(R.id.loader).visibility = View.GONE;
               // findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE;
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