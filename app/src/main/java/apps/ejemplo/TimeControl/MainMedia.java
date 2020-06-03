package apps.ejemplo.TimeControl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;

import apps.ejemplo.TimeControl.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class MainMedia extends AppCompatActivity {
    private static final int LOCATION = 1;


    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TabAdapter tabAdapter;
    private FirebaseAuth mAuth;
    private BroadcastReceiver br;
    private IntentFilter filter;
    public static Button btnFitxar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_media);


        setTitle("AccesControl");


        viewPager = findViewById(R.id.viewPager);
        tabAdapter = new TabAdapter((getSupportFragmentManager()));
        viewPager.setAdapter(tabAdapter);

        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager, false);

        mAuth=FirebaseAuth.getInstance();

/*
        br = new MyBroadcastReceiver();
        filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        this.registerReceiver(br, filter);


 */



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.my_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.logoutUser:
                logout();
                break;
            case android.R.id.home:
                //finish();
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
        public void onBackPressed() {
            super.onBackPressed();
    }



    private void logout(){
        mAuth.signOut();
        finish();
        Intent intent=new Intent(MainMedia.this,SignActivity.class);
        startActivity(intent);
    }


    /*
    protected void onStart() { //Claim the location permission
        super.onStart();
        //Assume you want to read the SSID when the activity is started
        tryToReadSSID();
    }

    //PERMISSIONS LOCATION FOR BROADCAST

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == LOCATION){
            //User allowed the location and you can read it now
            tryToReadSSID();
        }
    }

    private void tryToReadSSID() {
        //If requested permission isn't Granted yet
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Request permission from user
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION);
        }else{//Permission already granted
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if(wifiInfo.getSupplicantState() == SupplicantState.COMPLETED){
                String ssid = wifiInfo.getSSID();//Here you can access your SSID
                System.out.println(ssid);
            }
        }
    }

     */


}
