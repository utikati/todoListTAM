package tam3.trabalhos.jorgemartins;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

public class aboutUs extends AppCompatActivity {

    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        back = findViewById(R.id.idBackAboutUs);

        back.setOnClickListener(v -> finish());

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
                startActivity(new Intent(aboutUs.this, user.class));
                return true;  // returning true means that the selection was handled here

            default:
                return false; // returning false means that the selection was not handled here
            // you can also return super.onOptionsItemSelected(item)
        }

    }
}