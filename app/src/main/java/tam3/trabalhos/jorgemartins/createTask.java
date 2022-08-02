package tam3.trabalhos.jorgemartins;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import tam3.trabalhos.jorgemartins.db.DAO_Interface;
import tam3.trabalhos.jorgemartins.db.REST_DAO_CLASS;
import tam3.trabalhos.jorgemartins.models.dialLoggin;
import tam3.trabalhos.jorgemartins.models.taskInfo;
import tam3.trabalhos.jorgemartins.userObj.userObj;

public class createTask extends AppCompatActivity {

    private Button createTask, setTime, setDate;
    private int idListName;
    private String listName;
    private taskInfo task;
    private int thour=23, tminute=59;
    private TextView time, dateText;
    private Calendar calendar = Calendar.getInstance();
    private TextView description;
    private boolean checkUsedHour=false;
    private dialLoggin dialLogin;
    private DAO_Interface dao;
    private String username;
    private String password;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        description = findViewById(R.id.idCreateDescTask);
        dateText = findViewById(R.id.idDataTextCreate);
        setDate = findViewById(R.id.idInsertDataButton);
        //inicar o calendario com a data atual
        SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
        String date = sd.format(calendar.getTime());
        dateText.setText(date);


        SimpleDateFormat d = new SimpleDateFormat("dd");
        SimpleDateFormat MM = new SimpleDateFormat("MM");
        SimpleDateFormat y = new SimpleDateFormat("yyyy");

        dialLogin = new dialLoggin(this);
        dao = new REST_DAO_CLASS();




        /*
        Local onde recebe os dados oriundos da outra activity
         */
        Bundle e = getIntent().getExtras();
        if(e != null){
            this.idListName = e.getInt("idList");
            this.listName = e.getString("listName");

        }
        /*Local haver com a data */
        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog(dateText);
            }
        });



        /* Parte haver com as horas*/
        setTime = findViewById(R.id.idInsertHourButton);
        time = findViewById(R.id.idHourTextCreate);



        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(createTask.this, new TimePickerDialog.OnTimeSetListener() {


                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        checkUsedHour=true;
                        thour = hourOfDay;
                        tminute = minute;
                        calendar.set(Calendar.HOUR_OF_DAY, thour);
                        calendar.set(Calendar.MINUTE, tminute);
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                        String date = sdf.format(calendar.getTime());
                        time.setText(date);
                    }
                }, 0, 0, true);
                timePickerDialog.updateTime(thour, tminute);
                timePickerDialog.show();
            }
        });


        createTask = findViewById(R.id.idCreateTask);

        createTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (description.getText().toString().isEmpty()) {
                    Toast.makeText(createTask.this, "Por favor insira uma descrição", Toast.LENGTH_SHORT).show();
                } else {
                    dialLogin.startDial();
                    String token = getSharedPreferences("Nome de Utilizador", MODE_PRIVATE).getString("token", null);
                    calendar.set(Calendar.HOUR_OF_DAY, thour);
                    calendar.set(Calendar.MINUTE, tminute);
                    Date date = new Date(calendar.getTime().getTime());
                    task = new taskInfo(0, idListName, listName, description.getText().toString(), date);
                    task.setCheckUsedHour(checkUsedHour);
                    dao.addTask(token, task, new DAO_Interface.user_add_tasks() {
                        @Override
                        public void onSuccess() {
                            dialLogin.finishDial();
                            Toast.makeText(createTask.this, "Tarefa criada com sucesso", Toast.LENGTH_SHORT).show();
                            finish(); //após usar esta
                        }

                        @Override
                        public void onError(String message) {
                            dialLogin.finishDial();
                            if(message.equals("Response code: 498")){
                                refreshToken();
                            }else{
                                Toast.makeText(createTask.this, message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }
            }

        });
    }

    private void showDateDialog(TextView dateText) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {


                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String date = sdf.format(calendar.getTime());
                dateText.setText(date);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public void onSaveInstanceState(Bundle outState){ // called when the activity is going to be stopped
        // called before onStop (before or after onPause)
        // not called when the activity is closed by the user (back button)
        outState.putInt("id", this.idListName);
        outState.putString("listName", this.listName);
        outState.putString("description", this.description.getText().toString());
        outState.putInt("thour", this.thour);
        outState.putInt("tminute", this.tminute);
        outState.putSerializable("calendar", this.calendar);
        outState.putBoolean("checkUsedHour", this.checkUsedHour);
        outState.putString("dateText", this.dateText.getText().toString());
        outState.putString("timeText", this.time.getText().toString());
        super.onSaveInstanceState(outState);
    }

    public void onRestoreInstanceState(Bundle outState){ // called when the activity is recreated after being destroyed by the system
        super.onRestoreInstanceState(outState);          // called after onStart
        this.idListName = outState.getInt("id");
        this.listName = outState.getString("listName");
        this.description.setText(outState.getString("description"));
        this.thour = outState.getInt("thour");
        this.tminute = outState.getInt("tminute");
        this.calendar = (Calendar) outState.getSerializable("calendar");
        this.checkUsedHour = outState.getBoolean("checkUsedHour");
        this.dateText.setText(outState.getString("dateText"));
        this.time.setText(outState.getString("timeText"));
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
                startActivity(new Intent(createTask.this, user.class));
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
                    Toast.makeText(createTask.this, "Tente novamente", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(createTask.this, "Não foi possivel comunicar com o servidor, ou credenciais invalidas", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }


}