package tam3.trabalhos.jorgemartins;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


import tam3.trabalhos.jorgemartins.db.DAO_Interface;
import tam3.trabalhos.jorgemartins.db.REST_DAO_CLASS;
import tam3.trabalhos.jorgemartins.models.dialLoggin;
import tam3.trabalhos.jorgemartins.models.taskInfo;
import tam3.trabalhos.jorgemartins.userObj.userObj;

public class tasksList extends AppCompatActivity {

    private ListView taskListView;
    private List<taskInfo> intaskList;
    private Button inserTask;
    private int idListName;
    private String listName;
    private ListAdapterTasks adapter;
    private DAO_Interface dao;
    private dialLoggin dialLogin;
    private Button addTask;
    int idTask = 1; //apenas usado enquanto não existe base de dados relacional (depois assume o numero que virá de lá)
    LinearLayout linearLayout;
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_list);
        dao = new REST_DAO_CLASS();
        intaskList = new ArrayList<taskInfo>();
        dialLogin = new dialLoggin(this);

        Intent intent = getIntent();
        idListName = intent.getIntExtra("idList", 0);
        listName = intent.getStringExtra("listName");


        loadLists();


        this.adapter = new ListAdapterTasks(this, intaskList);
        taskListView = (ListView) findViewById(R.id.listTasksView);

        taskListView.setAdapter(adapter);
        taskListView.setTextFilterEnabled(true);




        inserTask = findViewById(R.id.insertTaskButton);

        inserTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(tasksList.this, createTask.class);
                i.putExtra("idList", idListName);
                i.putExtra("listName", listName);
                startActivity(i); //envia o id da lista para depois nas tarefas receber o valor e no pedido ter o id que tem de ir buscar
            }
        });


    }



    @Override
    protected void onResume() {
        super.onResume();
        loadLists();
        this.adapter.notifyDataSetChanged();
    }


    private void loadLists ()
        {
            String token = getSharedPreferences("Nome de Utilizador", MODE_PRIVATE).getString("token", null);
            String idlist = String.valueOf(idListName);
            dao.get_all_tasks(token, idlist, new DAO_Interface.get_all_tasks() {
                @Override
                public void onSuccess(List<taskInfo> tasks) {
                    intaskList.clear();
                    intaskList.addAll(tasks);
                    orderList();
                    adapter.notifyDataSetChanged();
                }
                @Override
                public void onError(String message) {
                    intaskList.clear();
                    if(message.equals("Response code: 498")){
                        refreshToken();
                    }
                    else{
                        Toast.makeText(tasksList.this, message, Toast.LENGTH_SHORT).show();
                    }
                    adapter.notifyDataSetChanged();
                }
            });
        }


        class ListAdapterTasks extends BaseAdapter {
            Context context;
            List<taskInfo> adaptList;

            // The constructor receives a context and the data
            public ListAdapterTasks(Context ctx, List<taskInfo> list) {
                context = ctx;
                adaptList = list;

            }

            @Override
            public int getCount() {
                return adaptList.size();
            }

            @Override
            public Object getItem(int position) {
                return adaptList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            // This method is called each time an item needs to be presented in the ListView
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                // convertView has the previous View for this position
                View rowView = convertView;

                // we only need to create the view if it does not exist
                if (rowView == null) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    rowView = inflater.inflate(R.layout.task_model, parent, false);
                }

                // These are the Views inside the ListView item
                TextView textDesc = (TextView) rowView.findViewById(R.id.descriptionId);
                TextView textListName = (TextView) rowView.findViewById(R.id.idTaskListName);
                TextView textDeadLine = (TextView) rowView.findViewById(R.id.idDeadLine);
                TextView textState = (TextView) rowView.findViewById(R.id.idConclusionTask);
                TextView textDateState = (TextView) rowView.findViewById(R.id.idStateTask);
                ImageButton deleteList = (ImageButton) rowView.findViewById(R.id.idDeleteTaskButton);
                ImageButton setState = (ImageButton) rowView.findViewById(R.id.idSetConclusionTaskButton);
                ImageButton editTask = (ImageButton) rowView.findViewById(R.id.idEditListTaskButton);
                linearLayout = (LinearLayout) rowView.findViewById(R.id.idLinearColorStuff);





                // obtains the contact for this position
                taskInfo c = adaptList.get(position);
                c.stringyfyDate();

                if(c.isConcluded()){
                    linearLayout.setBackgroundResource(R.color.green);
                }else{
                    if(c.getDeadline().before(new Date())){
                        linearLayout.setBackgroundResource(R.color.red);
                    }else{
                        linearLayout.setBackgroundResource(R.color.yellow);

                    }
                }

                // sets the TextView texts
                textDesc.setText(c.getDescription());
                textListName.setText(c.getListName());
                textDeadLine.setText(c.getDateOnString());
                textState.setText(c.isConcludedToString());
                textDateState.setText(c.verifyDate());

                setState.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialLogin.startDial();
                        String token = getSharedPreferences("Nome de Utilizador", MODE_PRIVATE).getString("token", null);
                         c.changeConclusion();
                         dao.editTask(token, c, new DAO_Interface.user_edit_tasks() {
                             @Override
                             public void onSuccess() {
                                 loadLists();
                                 dialLogin.finishDial();
                             }

                             @Override
                             public void onError(String message) {
                                 dialLogin.finishDial();
                                 if(message.equals("Response code: 498")){
                                     refreshToken();
                                     Toast.makeText(tasksList.this, "Tente novamente", Toast.LENGTH_SHORT).show();
                                 }
                                 else{
                                     Toast.makeText(tasksList.this, message, Toast.LENGTH_SHORT).show();
                                 }
                             }
                         });
                    }
                }
                );


                deleteList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialLogin.startDial();
                        String token = getSharedPreferences("Nome de Utilizador", MODE_PRIVATE).getString("token", null);

                        dao.deleteTheTask(token, c, new DAO_Interface.user_delete_tasks() {
                            @Override
                            public void onSuccess() {
                                loadLists();
                                dialLogin.finishDial();
                            }

                            @Override
                            public void onError(String message) {
                                dialLogin.finishDial();
                                if(message.equals("Response code: 498")){
                                    refreshToken();
                                    Toast.makeText(tasksList.this, "Tente novamente", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(tasksList.this, message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        ListAdapterTasks myAdapter = (ListAdapterTasks) taskListView.getAdapter();
                        myAdapter.notifyDataSetChanged();
                    }
                });

                editTask.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(tasksList.this, editTask.class);
                        intent.putExtra("task", c);
                        startActivity(intent);
                    }
                });




                // returns the view
                return rowView;
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
                startActivity(new Intent(tasksList.this, user.class));
                return true;  // returning true means that the selection was handled here

            default:
                return false; // returning false means that the selection was not handled here
            // you can also return super.onOptionsItemSelected(item)
        }

    }

    private boolean orderList() { //ordena a lista de tarefas por datas dealine
        if (intaskList != null && intaskList.size() > 1) {
            Collections.sort(intaskList);     // 4.º passo ordenamento
            return true;
        }
        return false;
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
                    loadLists();
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(tasksList.this, "Não foi possivel comunicar com o servidor, ou credenciais invalidas", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }


    }
