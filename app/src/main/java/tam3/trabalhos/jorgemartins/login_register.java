package tam3.trabalhos.jorgemartins;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import tam3.trabalhos.jorgemartins.db.DAO_Interface;
import tam3.trabalhos.jorgemartins.db.REST_DAO_CLASS;
import tam3.trabalhos.jorgemartins.models.dialLoggin;
import tam3.trabalhos.jorgemartins.userObj.userObj;

public class login_register extends AppCompatActivity {

    private Button btn_login, btn_register;
    private TextView txt_username, txt_password;
    private DAO_Interface dao;
    private dialLoggin dialLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
        dao = new REST_DAO_CLASS();
        SharedPreferences prefs = getSharedPreferences("Nome de Utilizador", MODE_PRIVATE);
        txt_username = findViewById(R.id.idUserLogin);
        txt_password = findViewById(R.id.idPasswordLogin);

        btn_login = findViewById(R.id.idLoginButton);
        btn_register = findViewById(R.id.idRegisterButton);
        dialLogin = new dialLoggin(this);
        if(prefs.getString("username", "")!=null && prefs.getString("password", "")!=null && prefs.getString("username", "")!="" && prefs.getString("password", "")!=""){
            txt_password.setText(prefs.getString("password", ""));
            txt_username.setText(prefs.getString("username", ""));
            logginCred();
        }

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialLogin.startDial();
                String username = txt_username.getText().toString();
                String password = txt_password.getText().toString();

                if(username.equals("") || password.equals("")){
                    dialLogin.finishDial();
                    Toast.makeText(login_register.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                dao.login(new userObj(username, password), new DAO_Interface.user_login() {
                    @Override
                    public void onSuccess(userObj user) {
                        dialLogin.finishDial();
                        Toast.makeText(login_register.this, "Login realizado com sucesso", Toast.LENGTH_SHORT).show();
                        SharedPreferences prefs = getSharedPreferences("Nome de Utilizador", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("username", username);
                        editor.putString("password", password);
                        editor.putString("token", user.getToken());
                        editor.commit();
                        Intent intent = new Intent(login_register.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(String message) {
                        dialLogin.finishDial();
                        Toast.makeText(login_register.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = txt_username.getText().toString();
                String password = txt_password.getText().toString();
                dialLogin.startDial();

                if(username.equals("") || password.equals("")){
                    dialLogin.finishDial();
                    Toast.makeText(login_register.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                dao.register(new userObj(username, password), new DAO_Interface.user_register() {
                    @Override
                    public void onSuccess(userObj user) {
                        dialLogin.finishDial();
                        Toast.makeText(login_register.this, "Registo realizado com sucesso", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String message) {
                        dialLogin.finishDial();
                        Toast.makeText(login_register.this, message, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });




    }

    private void logginCred(){ //caso tenha credenciais guardadas faz o login automaticamente na aplicação

        SharedPreferences prefs = getSharedPreferences("Nome de Utilizador", MODE_PRIVATE);
        String username = prefs.getString("username", "");
        String password = prefs.getString("password", "");
        dialLogin.startDial();
        if(!username.equals("") && !password.equals("")){
            dao.login(new userObj(username, password), new DAO_Interface.user_login() {
                @Override
                public void onSuccess(userObj user) {
                    Toast.makeText(login_register.this, "Login realizado com sucesso", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("token", user.getToken());
                    editor.commit();
                    Intent intent = new Intent(login_register.this, MainActivity.class);
                    startActivity(intent);
                    dialLogin.finishDial();
                    finish();
                }

                @Override
                public void onError(String message) {
                    dialLogin.finishDial();
                    Toast.makeText(login_register.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}