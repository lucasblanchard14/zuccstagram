package com.example.myapplication.ui.Settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.R;

import java.util.ArrayList;

public class Settings extends AppCompatActivity {


    ListView listViewSettings;
    ArrayList<String> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setUpUI();
        setUpArrayList();
        toolbarSetUp();

        listViewSettings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    goToEditProfile();
                }
                else if (position == 1){

                }
                else if (position == 2){

                }
                else if (position == 3){

                }
                else if (position == 4){
                    goToAdvanceSettings();
                }
            }
        });

    }

    void toolbarSetUp(){
        Toolbar toolbar = findViewById(R.id.toolbar_Settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    void setUpUI(){
        listViewSettings = findViewById(R.id.listView_Settings);


    }
    void setUpArrayList(){
        arrayList = new ArrayList<>();
        arrayList.add(getResources().getString(R.string.edit_Profile));
        arrayList.add(getResources().getString(R.string.account_privacy));
        arrayList.add(getResources().getString(R.string.dark_mode));
        arrayList.add(getResources().getString(R.string.help_support));
        arrayList.add(getResources().getString(R.string.advance_setting));
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList);
        listViewSettings.setAdapter(arrayAdapter);
    }


    void goToAdvanceSettings(){
        Intent intent = new Intent(this, AdvanceSettingsActivity.class);
        startActivity(intent);

    }
    void goToEditProfile(){
        Intent intent = new Intent(this, EditProfile.class);
        startActivity(intent);

    }

}
