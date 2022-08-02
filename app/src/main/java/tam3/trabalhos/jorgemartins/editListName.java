package tam3.trabalhos.jorgemartins;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import tam3.trabalhos.jorgemartins.db.DAO_Interface;
import tam3.trabalhos.jorgemartins.db.REST_DAO_CLASS;
import tam3.trabalhos.jorgemartins.models.ListName;
import tam3.trabalhos.jorgemartins.models.dialLoggin;
import tam3.trabalhos.jorgemartins.userObj.userObj;

public class editListName extends AppCompatActivity {
    private ImageButton editListNameButton;
    private TextView editListNameTextField;
    private ListName listName;
    private DAO_Interface dao;
    private dialLoggin dialog;
    private String username;
    private String password;
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list_name);
        dao = new REST_DAO_CLASS();
        dialog = new dialLoggin(this);

        editListNameButton = findViewById(R.id.idEditList);
        editListNameTextField = findViewById(R.id.idEditListNameText);


        Bundle e = getIntent().getExtras();
        if(e != null){
            this.listName = (ListName) e.getSerializable("listName");
        }

        editListNameTextField.setText(listName.getName());

        editListNameButton.setOnClickListener(v -> {
            dialog.startDial();
            String newName = editListNameTextField.getText().toString();
            if (newName.isEmpty()) {
                dialog.finishDial();
                Toast.makeText(editListName.this, "Nome da lista não pode ser vazio", Toast.LENGTH_SHORT).show();
            } else {
                listName.setName(newName);
                String token = getSharedPreferences("Nome de Utilizador", MODE_PRIVATE).getString("token", "");
                dao.editList(token, Integer.toString(listName.getId()), listName, new DAO_Interface.user_edit_list() {
                    @Override
                    public void onSuccess() {
                        dialog.finishDial();
                        Toast.makeText(editListName.this, "Lista editada com sucesso", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onError(String message) {
                        if (message.equals("Response code: 498")) {
                            refreshToken();
                            dialog.finishDial();
                        }else{
                            dialog.finishDial();
                            Toast.makeText(editListName.this, "Erro ao editar lista", Toast.LENGTH_SHORT).show();
                        }
                    }

                });
            }
        });





    }

    public void onSaveInstanceState(Bundle outState){ // called when the activity is going to be stopped
        // called before onStop (before or after onPause)
        // not called when the activity is closed by the user (back button)
        if(editListNameTextField.getText().toString() != ""){
            outState.putString("editname", editListNameTextField.getText().toString());
        }
        else{
            outState.putString("editnamet", "");
        }

        outState.putSerializable("listName", listName);

        super.onSaveInstanceState(outState);


    }

    public void onRestoreInstanceState(Bundle outState){ // called when the activity is recreated after being destroyed by the system
        super.onRestoreInstanceState(outState);          // called after onStart
        if(outState.getString("editname") != null){
            editListNameTextField.setText(outState.getString("editname"));
        }
        this.listName = (ListName) outState.getSerializable("listName");
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
                startActivity(new Intent(editListName.this, user.class));
                return true;  // returning true means that the selection was handled here

            default:
                return false; // returning false means that the selection was not handled here
            // you can also return super.onOptionsItemSelected(item)
        }

    }

    private void refreshToken() {
        this.username = getSharedPreferences("Nome de Utilizador", MODE_PRIVATE).getString("username", null);
        this.password = getSharedPreferences("Nome de Utilizador", MODE_PRIVATE).getString("password", null);
        if (username != null && password != null && username != "" && password != "") {
            dao.login(new userObj(username, password), new DAO_Interface.user_login() {
                @Override
                public void onSuccess(userObj user) {
                    SharedPreferences prefs = getSharedPreferences("Nome de Utilizador", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("token", user.getToken());
                    editor.commit();
                    Toast.makeText(editListName.this, "Tente novamente", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(editListName.this, "Não foi possivel comunicar com o servidor, ou credenciais invalidas", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }

}