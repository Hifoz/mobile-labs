package com.example.hifoz.lab2;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    List<Integer> limits;
    List<Integer> frequencies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setupFrequencySpinner();
        setupLimitSpinner();
        setupLinkEditor();
    }

    private void setupFrequencySpinner(){
        Spinner spinner = findViewById(R.id.updateFrequencySpinner);

        frequencies = new ArrayList<Integer>();
        List<String> freqDisplayText = new ArrayList<String>();
        frequencies.add(10);
        freqDisplayText.add("10 minutes");
        frequencies.add(30);
        freqDisplayText.add("30 minutes");
        frequencies.add(60);
        freqDisplayText.add("1 hour");
        frequencies.add(720);
        freqDisplayText.add("12 hours");
        frequencies.add(1440);
        freqDisplayText.add("1 day");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, freqDisplayText);
        spinner.setAdapter(adapter);

        SharedPreferences prefs = getSharedPreferences("RSS_PREFS", Context.MODE_PRIVATE);;
        spinner.setSelection(prefs.getInt("updateFreq", 2));


        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                SharedPreferences prefs = getSharedPreferences("RSS_PREFS", Context.MODE_PRIVATE);;
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("updateFreq", frequencies.get(position));
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });


    }

    private void setupLimitSpinner(){
        Spinner spinner = findViewById(R.id.itemSpinner);

        limits = new ArrayList<Integer>();
        limits.add(10);
        limits.add(25);
        limits.add(50);
        limits.add(100);


        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this, R.layout.support_simple_spinner_dropdown_item, limits);
        spinner.setAdapter(adapter);


        SharedPreferences prefs = getSharedPreferences("RSS_PREFS", Context.MODE_PRIVATE);
        spinner.setSelection(prefs.getInt("itemLimit", 1));

        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                SharedPreferences prefs = getSharedPreferences("RSS_PREFS", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("itemLimit", limits.get(position));
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void setupLinkEditor(){
        EditText editText = findViewById(R.id.feedLink);
        SharedPreferences prefs = getSharedPreferences("RSS_PREFS", Context.MODE_PRIVATE);
        editText.setText(prefs.getString("feedLink", ""));
    }

    public void saveLink(View view){
        SharedPreferences prefs = getSharedPreferences("RSS_PREFS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("feedLink", ((EditText)findViewById(R.id.feedLink)).getText().toString());
        editor.apply();
    }
}
