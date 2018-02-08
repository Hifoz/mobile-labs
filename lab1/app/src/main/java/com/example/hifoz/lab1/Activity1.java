package com.example.hifoz.lab1;

import android.content.Context;
import android.content.Intent;
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

public class Activity1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1);

        Spinner spinner = findViewById(R.id.spinner);
        addSpinnerContent(spinner);
        setSpinnerListener(spinner);
        setChoiceFromSetting(spinner);

    }

    public void onButton1Click(View view){
        EditText editText = findViewById(R.id.editText1);
        Intent intent = new Intent(Activity1.this, Activity2.class);

        intent.putExtra("et1", editText.getText().toString());
        Activity1.this.startActivity(intent);
    }


    private void addSpinnerContent(Spinner spinner){
        List<String> data = new ArrayList<>();
        data.add("Element 1");
        data.add("Element 2");
        data.add("Element 3");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, data);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void setSpinnerListener(Spinner spinner){
        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                saveSetting(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void saveSetting(int index) {
        SharedPreferences sharedPrefs = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt("itemIndex", index);
        editor.commit();

    }

    protected void setChoiceFromSetting(Spinner spinner){
        SharedPreferences sharedPrefs = getPreferences(Context.MODE_PRIVATE);
        int value = sharedPrefs.getInt("itemIndex", 0);

        spinner.setSelection(value);
    }


}
