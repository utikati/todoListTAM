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

public class editTask extends AppCompatActivity {

    private Button editTask, setTime, setDate;

    private taskInfo task;
    private int thour = 23, tminute = 59;
    private TextView time, dateText;
    private Calendar calendar = Calendar.getInstance();
    private TextView description;
    private DAO_Interface dao;
    private dialLoggin dialLogin;
    private String username;
    private String password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        /*
        Local onde recebe os dados oriundos da outra activity
         */
        Bundle e = getIntent().getExtras();
        if (e != null) {
            task = (taskInfo) e.getSerializable("task");
        }
        dao = new REST_DAO_CLASS();
        dialLogin = new dialLoggin(this);

        description = findViewById(R.id.idEditDescTask);
        dateText = findViewById(R.id.idDataTextEdit);
        setDate = findViewById(R.id.idEditDataButton);
        /* Parte haver com as horas*/
        setTime = findViewById(R.id.idEditHourButton);
        time = findViewById(R.id.idHourTextEdit);
        SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat d = new SimpleDateFormat("dd");
        SimpleDateFormat MM = new SimpleDateFormat("MM");
        SimpleDateFormat y = new SimpleDateFormat("yyyy");

        if (task != null) {
            description.setText(task.getDescription()); //seta a descrição
            String date = sd.format(task.getDeadline());
            dateText.setText(date); //seta a data
            calendar.setTime(task.getDeadline()); //para mesmo que não mude ele adiciona a data que já estava
            if (task.isCheckUsedHour()) {
                SimpleDateFormat s = new SimpleDateFormat("HH:mm");
                SimpleDateFormat h = new SimpleDateFormat("HH");
                SimpleDateFormat m = new SimpleDateFormat("mm");
                this.thour = Integer.parseInt(h.format(task.getDeadline()));
                this.tminute = Integer.parseInt(m.format(task.getDeadline()));
                String hour = s.format(task.getDeadline());
                time.setText(hour);
            }
        } else {
            description.setText("");
            String date = sd.format(calendar.getTime());
            dateText.setText(date);
        }

        /*Local haver com a data */
        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog(dateText);
            }
        });

        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(editTask.this, new TimePickerDialog.OnTimeSetListener() {


                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        task.setCheckUsedHour(true);
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


        editTask = findViewById(R.id.idEditTask);

        editTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialLogin.startDial();
                if (description.getText().toString().equals("")) {
                    dialLogin.finishDial();
                    Toast.makeText(editTask.this, "Por favor, preencha a descrição", Toast.LENGTH_SHORT).show();
                } else {
                    calendar.set(Calendar.HOUR_OF_DAY, thour);
                    calendar.set(Calendar.MINUTE, tminute);
                    Date date = new Date(calendar.getTime().getTime());
                    task.setDeadline(date);
                    task.setDescription(description.getText().toString());
                    String token = getSharedPreferences("Nome de Utilizador", MODE_PRIVATE).getString("token", null);
                    dao.editTask(token, task, new DAO_Interface.user_edit_tasks() {
                        @Override
                        public void onSuccess() {
                            dialLogin.finishDial();
                            Toast.makeText(editTask.this, "Tarefa editada com sucesso", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onError(String message) {
                            dialLogin.finishDial();
                            if(message.equals("Response code: 498")){
                                refreshToken();
                            }else{
                                Toast.makeText(editTask.this, message, Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
                }
            }
        });
    }

        private void showDateDialog (TextView dateText){
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

        public void onSaveInstanceState (Bundle outState)
        { // called when the activity is going to be stopped
            // called before onStop (before or after onPause)
            // not called when the activity is closed by the user (back button)
            outState.putSerializable("task", task);
            outState.putString("description", this.description.getText().toString());
            outState.putInt("thour", this.thour);
            outState.putInt("tminute", this.tminute);
            outState.putString("dateText", this.dateText.getText().toString());
            outState.putString("timeText", this.time.getText().toString());
            outState.putSerializable("calendar", calendar);
            super.onSaveInstanceState(outState);
        }

        public void onRestoreInstanceState (Bundle outState)
        { // called when the activity is recreated after being destroyed by the system
            super.onRestoreInstanceState(outState);          // called after onStart
            this.task = (taskInfo) outState.getSerializable("task");
            this.description.setText(outState.getString("description"));
            this.thour = outState.getInt("thour");
            this.tminute = outState.getInt("tminute");
            this.calendar = (Calendar) outState.getSerializable("calendar");
            this.dateText.setText(outState.getString("dateText"));
            this.time.setText(outState.getString("timeText"));
        }

        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            // Inflate the menu (see ficheiro menu_main.xml in /res/menu)
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }

        public boolean onOptionsItemSelected (MenuItem item){
            switch (item.getItemId()) {
                case R.id.idUser:
                    startActivity(new Intent(editTask.this, user.class));
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
                    Toast.makeText(editTask.this, "Tente novamente", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(editTask.this, "Não foi possivel comunicar com o servidor, ou credenciais invalidas", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }


    }
