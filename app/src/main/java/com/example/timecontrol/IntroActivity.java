package com.example.timecontrol;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AppCompatActivity {

    private ViewPager screenPager;
    IntroViewPagerAdapter introViewPagerAdapter;
    TabLayout tabIndicator;
    Button btnNext,btnGetStarted;
    int position=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //make the activity on full screen

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);


        if(restorePrefData()){
            Intent intent= new Intent(IntroActivity.this,SignActivity.class);
            startActivity(intent);
            finish();

        }


        setContentView(R.layout.activity_intro);

        //hide action bar

        getSupportActionBar().hide();

        //fill list screen

        final List<ScreenItem> mList= new ArrayList<>();
        mList.add(new ScreenItem("Bienvenido TimeControl","",R.drawable.logo));
        mList.add(new ScreenItem("Registrar Empresa","Primero de todo, se necessita dar de alta el número de empleados para poder acceder a TimeControl. Una vez establecido el plan de utilizacion" +
                " tendremos que registrar los usuarios con los codigos que os hemos mandado por email",R.drawable.sing));
        mList.add(new ScreenItem("Control Jornada Laboral","TimeControl permite registar el inicio y finalizacion de la jornada laboral de manera simple. Solo tenemos que dar un click en el boton que encontraremos " +
                "en el inicio de l'aplicacion",R.drawable.time));
        mList.add(new ScreenItem("Localització","TimeControl registra la localicacion del usuario cada vez que se realiza un fitxage de salida o entrada",R.drawable.location));
        mList.add(new ScreenItem("Depesses","Por último, TimeControl permite al usuario añadir facturas y subir el tiquet",R.drawable.dolar));

        //setup viewpager

        screenPager= findViewById(R.id.screen_viewpager);
        tabIndicator=findViewById(R.id.tabIndicator);
        btnNext=findViewById(R.id.btnNext);
        btnGetStarted=findViewById(R.id.btnGetStarted);


        //setup viewpager
        introViewPagerAdapter= new IntroViewPagerAdapter(this,mList);
        screenPager.setAdapter(introViewPagerAdapter);

        //setup tablayout with viewpager

        tabIndicator.setupWithViewPager(screenPager);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position=screenPager.getCurrentItem();
                if (position<mList.size()){
                    position++;
                    screenPager.setCurrentItem(position);
                }
                if (position == mList.size()-1){
                    loadLastScreen();
                }

            }
        });
        
        tabIndicator.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition()==mList.size()-1){
                    loadLastScreen();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        
        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePrefsData();
                Intent intent= new Intent(IntroActivity.this,SignActivity.class);
                startActivity(intent);
                finish();

            }
        });

    }

    private boolean restorePrefData(){
        SharedPreferences pref= getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        Boolean isIntroActivityOpenedBefore= pref.getBoolean("isIntroOpened",false);
        return isIntroActivityOpenedBefore;

    }

    private void savePrefsData() {

        SharedPreferences pref= getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor=pref.edit();
        editor.putBoolean("isIntroOpened",true);
        editor.commit();

    }

    private void loadLastScreen() {
        btnNext.setVisibility(View.INVISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);
        btnGetStarted.setVisibility(View.VISIBLE);
    }
}




