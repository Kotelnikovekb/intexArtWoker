package ru.ekbapp.intexartwoker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.File;

import ru.ekbapp.intexartwoker.MainFragment.fragment_ads;
import ru.ekbapp.intexartwoker.MainFragment.fragment_profile;
import ru.ekbapp.intexartwoker.MainFragment.fragment_request;
import ru.ekbapp.intexartwoker.Services.SendService;

import static ru.ekbapp.intexartwoker.StartUpActivity.SESSION_S;
import static ru.ekbapp.intexartwoker.StartUpActivity.SETTINGS_S;
import static ru.ekbapp.intexartwoker.StartUpActivity.USER_NAME_S;
import static ru.ekbapp.intexartwoker.StartUpActivity.USER_ROLE_S;
import static ru.ekbapp.intexartwoker.StartUpActivity.USER_SIGNATURE_DEVICE_S;
import static ru.ekbapp.intexartwoker.StartUpActivity.USER_SIGNATURE_S;

public class  MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private AppBarConfiguration mAppBarConfiguration;
    String selectFragment;
    int PERMISSION_ALL = 1;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Fragment fragment = null;
        Class fragmentClass = null;
        fragment_request fr=new fragment_request();
        fragmentClass= fragment_request.class;
        selectFragment="fragment_request";
        sharedPreferences=getSharedPreferences(SETTINGS_S,MODE_PRIVATE);

        try {

            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
        setTitle("Заявки");




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        String[] PERMISSIONS = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.CAMERA
        };
        File folderRequest= new File(Environment.getExternalStorageDirectory() +
                File.separator+getResources().getString(R.string.folder_name)+
                File.separator+getResources().getString(R.string.folder_zip)

        );
        if (!folderRequest.exists()){
            folderRequest.mkdirs();
            Log.e("PERMISSION","Папки не существует!");
            Log.e("PERMISSION","Создаю папку "+folderRequest.mkdirs());
        }

        if (!folderRequest.exists()){
            folderRequest.mkdirs();
            Log.e("PERMISSION","Папки не существует!");
            Log.e("PERMISSION","Создаю папку "+folderRequest.mkdirs());
        }

        startService(new Intent(MainActivity.this, SendService.class));

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        TextView logout=findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.remove(SESSION_S);
                editor.remove(USER_SIGNATURE_DEVICE_S);
                editor.remove(USER_SIGNATURE_S);
                editor.remove(USER_NAME_S);
                editor.remove(USER_ROLE_S);
                editor.commit();
                startActivity(new Intent(MainActivity.this,StartUpActivity.class));

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        TextView name=(TextView)findViewById(R.id.nameUser);
        TextView dol=(TextView)findViewById(R.id.textView);
        String p=sharedPreferences.getString(USER_NAME_S,"error");
        name.setText(p);
        dol.setText(sharedPreferences.getString(USER_ROLE_S,"error"));

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressLint("RestrictedApi")
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment = null;
        Class fragmentClass = fragment_request.class;

        selectFragment="fragment_map";

        switch (item.getItemId()){
            case R.id.nav_home:
                fragmentClass=fragment_request.class;
                selectFragment="fragment_request";
                setTitle("Заявки");
                break;
            case R.id.nav_gallery:
                fragmentClass= fragment_ads.class;
                selectFragment="fragment_ads";
                setTitle("Объявления");
                break;
            case R.id.nav_profile:
                fragmentClass= fragment_profile.class;
                selectFragment="fragment_profile";
                setTitle("Профиль");
                break;



        }


        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
        item.setChecked(true);
        setTitle(item.getTitle());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        TextView out=drawer.findViewById(R.id.logout);
        out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString(SESSION_S,"");
                editor.commit();
                startActivity(new Intent(MainActivity.this,StartUpActivity.class));
                finish();
            }
        });

        return true;
    }
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
