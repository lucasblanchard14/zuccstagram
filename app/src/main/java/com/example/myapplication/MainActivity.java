package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.LogIn_SignUp.LogIn_SignUp_Main;
import com.example.myapplication.ui.Search.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_nav_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.navigation_home:
                //TODO
                goToLogInSignUp();
                return true;

            case R.id.navigation_dashboard:
                //TODO
                goToSettings();
                return true;

            case R.id.navigation_notifications:
                //TODO
                goToSearch();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbarSetUp();
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        NavigationUI.setupWithNavController(navView, navController);


    }

    void toolbarSetUp(){
        Toolbar toolbar = findViewById(R.id.toolbar_Profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("   Zuccstagram");

    }
    void goToSettings(){
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }
    void goToLogInSignUp(){
        Intent intent = new Intent(this, LogIn_SignUp_Main.class);
        startActivity(intent);
    }

    void goToSearch(){
        // How do I do this?
        /*Fragment fragment = new SearchFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();*/
        Intent intent = new Intent(this, SearchFragment.class);
        startActivity(intent);

        Toast toast = Toast.makeText(this, "TEST...", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onBackPressed(){

    }
}
