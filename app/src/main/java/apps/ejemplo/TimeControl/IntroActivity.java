package apps.ejemplo.TimeControl;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import apps.ejemplo.TimeControl.R;
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
        mList.add(new ScreenItem("Welcome TimeControl","",R.drawable.logo));
        mList.add(new ScreenItem("Register Company","First of all, it is necessary to register the number of employees to be able to access TimeControl. Once the usage plan is established we will have to register the users with the codes that we have sent you by email",R.drawable.sing));
        mList.add(new ScreenItem("Control Workday","TimeControl allows you to record the start and end of the workday in a simple way. We just have to click on the button that we will find at the beginning of the application",R.drawable.time));
        mList.add(new ScreenItem("Location","Time Control records the user's location each time an exit or entry is made",R.drawable.location));
        mList.add(new ScreenItem("Expenses","Finally, TimeControl allows the user to add invoices and upload the ticket",R.drawable.dolar));

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
                Intent intent= new Intent(IntroActivity.this,User_or_Admin.class);
                startActivity(intent);
                finish();

            }
        });

    }

    private boolean restorePrefData(){
        SharedPreferences pref= getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        return pref.getBoolean("isIntroOpened",false);

    }

    private void savePrefsData() {

        SharedPreferences pref= getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor=pref.edit();
        editor.putBoolean("isIntroOpened",true);
        editor.apply();

    }

    private void loadLastScreen() {
        btnNext.setVisibility(View.INVISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);
        btnGetStarted.setVisibility(View.VISIBLE);
    }
}




