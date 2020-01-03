package com.example.meteoservice;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public String request = "";

    private List<String> stationsList = new ArrayList<>();
    private List<String> idsList = new ArrayList<>();

    private static final String STATIONS_URL = "http://infomet.nazwa.pl/data/stations.php";
    private static final String STATION = "Station";
    private static final String ID = "ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Send request to retrive list of Meteo Stations
        HttpRequest getRequest = new HttpRequest();
        try {
            String str = getRequest.execute(STATIONS_URL).get();
            stationsList.clear();
            idsList.clear();

            String decodedToUTF8 = new String(str.getBytes("ISO-8859-15"), "UTF-8");
            JSONArray jsonArray = new JSONArray(str);

            //Iterating on JSONObjects
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                String station = jsonObject.get(STATION).toString();
                stationsList.add(station);

                String id = jsonObject.get(ID).toString();
                idsList.add(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Add stationlist to spinner
        Spinner spinner = findViewById(R.id.stationsSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stationsList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        ((TextView) findViewById(R.id.editTextDateAndTime)).setText("Wybierz stację");
        ((TextView) findViewById(R.id.editTextTemperature)).setText("Wybierz stację");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getApplicationContext(), "Stacja: "+ idsList.get(position) ,Toast.LENGTH_SHORT).show();
        String stationID = idsList.get(position);
        try {
            HttpRequest getRequest = new HttpRequest();
            String str = getRequest.execute("http://infomet.nazwa.pl/data/values.php?stationid=" + stationID).get();
            if (str == null) return ;
            JSONObject jobj = new JSONObject(str);

            ((TextView) findViewById(R.id.editTextDateAndTime)).setText(jobj.get("dt").toString());
            ((TextView) findViewById(R.id.editTextTemperature)).setText(jobj.get("TempOut").toString()+" °C ");
        } catch (Exception err)
        {

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        ((TextView) findViewById(R.id.editTextDateAndTime)).setText("Wybierz stację");
        ((TextView) findViewById(R.id.editTextTemperature)).setText("Wybierz stację");
    }
}
