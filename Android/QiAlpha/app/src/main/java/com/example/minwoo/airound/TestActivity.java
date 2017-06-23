package com.example.minwoo.airound;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

public class TestActivity extends AppCompatActivity {

    ArrayAdapter sAdapter;
    Spinner spinner;
    CheckBox checkBox3,checkBox5,checkBox6,checkBox7,checkBox8,checkBox9,
            checkBox10,checkBox11,checkBox12,checkBox13,checkBox14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        checkBox3 = (CheckBox)findViewById(R.id.checkBox3);
        checkBox5 = (CheckBox)findViewById(R.id.checkBox5);
        checkBox6 = (CheckBox)findViewById(R.id.checkBox6);
        checkBox7 = (CheckBox)findViewById(R.id.checkBox7);
        checkBox8 = (CheckBox)findViewById(R.id.checkBox8);
        checkBox9 = (CheckBox)findViewById(R.id.checkBox9);
        checkBox10 = (CheckBox)findViewById(R.id.checkBox10);
        checkBox11 = (CheckBox)findViewById(R.id.checkBox11);
        checkBox12 = (CheckBox)findViewById(R.id.checkBox12);
        checkBox13 = (CheckBox)findViewById(R.id.checkBox13);
        checkBox14 = (CheckBox)findViewById(R.id.checkBox14);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);

        toolbar.setTitleTextColor(Color.parseColor("#000000"));
        //toolbar.setSubtitle("hi");
        //toolbar.setNavigationIcon(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);



        //Define spinner - connecting spinner3 to variable spinner
        spinner = (Spinner)findViewById(R.id.spinner);
        //Define array adapter; array adapater connect to sources(array.xml) - Using array adapter on this layout
        sAdapter = ArrayAdapter.createFromResource(this, R.array.heero, android.R.layout.simple_spinner_dropdown_item);
        //Connection between adapter and spinner
        spinner.setAdapter(sAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast.makeText(this, "Clear filters", Toast.LENGTH_SHORT).show();
        clearAllFilter();
        return super.onOptionsItemSelected(item);
    }
    public void clearAllFilter(){
        checkBox3.setChecked(false);
        checkBox5.setChecked(false);
        checkBox6.setChecked(false);
        checkBox7.setChecked(false);
        checkBox8.setChecked(false);
        checkBox9.setChecked(false);
        checkBox10.setChecked(false);
        checkBox11.setChecked(false);
        checkBox12.setChecked(false);
        checkBox13.setChecked(false);
        checkBox14.setChecked(false);
    }
}
