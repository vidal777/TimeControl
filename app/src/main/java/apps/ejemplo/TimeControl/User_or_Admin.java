package apps.ejemplo.TimeControl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class User_or_Admin extends AppCompatActivity {

    private Button btnCompany,btnUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_or__admin);
        btnCompany=findViewById(R.id.btnCompany);
        btnUser=findViewById(R.id.btnUser);

        btnCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(User_or_Admin.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(User_or_Admin.this,SignActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
