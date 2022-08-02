package tam3.trabalhos.jorgemartins;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button listBotton, allTaskBotton, aboutUsButton;
    private boolean load = true;
    TextView textViewUserName;
    String prefsName = "Nome de Utilizador";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        allTaskBotton = findViewById(R.id.toDoAllButton);
        listBotton = findViewById(R.id.listButton);
        aboutUsButton = findViewById(R.id.idAboutUs);
        textViewUserName = findViewById(R.id.idNomeUtilizadorPagPrincipal);

        SharedPreferences prefs = getSharedPreferences(prefsName, MODE_PRIVATE);

        // reads from sharedPreferences
        // The second argument defines the return value if it does not exist
        String strLeft = prefs.getString("username", null);

        if(strLeft!=null){
            // writes to the left EditText
            textViewUserName.setText("Utilizador: "+strLeft);
        }


        listBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ToDoList.class));
            }
        });

        allTaskBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, allTaskOrder.class));
            }
        });

        aboutUsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, aboutUs.class));
            }
        }
        );

    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getSharedPreferences(prefsName, MODE_PRIVATE);

        // reads from sharedPreferences
        // The second argument defines the return value if it does not exist
        String strLeft = prefs.getString("username", null);

        if(strLeft!=null){
            // writes to the left EditText
            textViewUserName.setText("Utilizador: "+strLeft);
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu (see ficheiro menu_main.xml in /res/menu)
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.idUser:
                startActivity(new Intent(MainActivity.this, user.class));
                return true;  // returning true means that the selection was handled here

            default:
                return false; // returning false means that the selection was not handled here
            // you can also return super.onOptionsItemSelected(item)
        }

    }




}