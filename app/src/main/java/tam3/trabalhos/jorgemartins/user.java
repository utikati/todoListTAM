package tam3.trabalhos.jorgemartins;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class user extends AppCompatActivity {

    String prefsName = "Nome de Utilizador";
    Button btnBack, btnLogout;
    TextView txtUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        btnBack = (Button) findViewById(R.id.idBack);
        txtUser = (TextView) findViewById(R.id.idUserOn);
        btnLogout = (Button) findViewById(R.id.idLogout);

        SharedPreferences prefs = getSharedPreferences(prefsName, MODE_PRIVATE);

        // reads from sharedPreferences
        // The second argument defines the return value if it does not exist
        String strLeft = prefs.getString("username", null);

        if(strLeft!=null){
            // writes to the left EditText
            txtUser.setText(strLeft);
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }
        );

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences(prefsName, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.commit();
                Intent intent = new Intent(user.this, login_register.class);
                startActivity(intent);
                finish();
            }
        }
        );


    }
}