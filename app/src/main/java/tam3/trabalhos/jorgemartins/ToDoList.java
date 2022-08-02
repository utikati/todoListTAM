package tam3.trabalhos.jorgemartins;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import tam3.trabalhos.jorgemartins.db.DAO_Interface;
import tam3.trabalhos.jorgemartins.db.REST_DAO_CLASS;
import tam3.trabalhos.jorgemartins.models.ListName;
import tam3.trabalhos.jorgemartins.models.dialLoggin;
import tam3.trabalhos.jorgemartins.userObj.userObj;

public class ToDoList extends AppCompatActivity {

    private ListView toDoListView;


    private ImageButton addList;
    private EditText nameOfList;
    private ImageButton viewListTasks;
    int numberOfTasks;
    ListAdapterTasks myAdapter;
    List<ListName> nameList;
    private DAO_Interface dao;
    private dialLoggin dialLogin;
    private String username;
    private String password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);
        dao = new REST_DAO_CLASS();
        nameList = new ArrayList<>();
        myAdapter = new ListAdapterTasks(this, nameList);
        toDoListView = (ListView) findViewById(R.id.toDoListView);
        toDoListView.setAdapter(myAdapter);

        addList = findViewById(R.id.addList);
        nameOfList = findViewById(R.id.listName);


        toDoListView.setTextFilterEnabled(true);
        dialLogin = new dialLoggin(this);
        loadLists();
        addList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialLogin.startDial();
                String name = nameOfList.getText().toString();
                if (name.isEmpty()) {
                    dialLogin.finishDial();
                    Toast.makeText(ToDoList.this, "Insira um nome para a lista", Toast.LENGTH_SHORT).show();
                } else {
                    String token = getSharedPreferences("Nome de Utilizador", MODE_PRIVATE).getString("token", null);
                    ListName listName = new ListName();
                    listName.setName(name);
                    dao.addList(token, listName, new DAO_Interface.user_add_list() {
                        @Override
                        public void onSuccess() {
                            dialLogin.finishDial();
                            Toast.makeText(ToDoList.this, "Lista criada com sucesso", Toast.LENGTH_SHORT).show();
                            nameOfList.setText("");
                            loadLists();
                        }

                        @Override
                        public void onError(String message) {
                            dialLogin.finishDial();
                            if (message.equals("Response code: 498")) {
                                refreshToken();
                                Toast.makeText(ToDoList.this, "Tente novamente", Toast.LENGTH_SHORT).show(); //poderia criar a lista a partir daqui, mas fica com um aviso
                            } else {
                                Toast.makeText(ToDoList.this, "Erro ao criar lista", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                    loadLists(); //isto será depois um insert na base de dados e depois um refresh da lista
                    myAdapter = (ListAdapterTasks) toDoListView.getAdapter();
                    myAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        dao = new REST_DAO_CLASS();
        dialLogin = new dialLoggin(this);
        loadLists();
        ListAdapterTasks myAdapter = (ListAdapterTasks) toDoListView.getAdapter();
        myAdapter.notifyDataSetChanged();
    }

    private void loadLists() {
        String token = getSharedPreferences("Nome de Utilizador", MODE_PRIVATE).getString("token", null);
        dao.get_all_lists(token, new DAO_Interface.get_all_lists() {

            @Override
            public void onSuccess(List listNames) {

                nameList.clear();
                nameList.addAll(listNames);
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String message) {
                nameList.clear();
                if (message.equals("Response code: 498")) {
                        refreshToken();
                } else {
                    Toast.makeText(ToDoList.this, message, Toast.LENGTH_SHORT).show();
                }
                myAdapter.notifyDataSetChanged();
            }
        });

    }

    public void onSaveInstanceState(Bundle outState) { // called when the activity is going to be stopped
        // called before onStop (before or after onPause)
        // not called when the activity is closed by the user (back button)
        if (nameOfList.getText().toString() != "") {
            outState.putString("name", nameOfList.getText().toString());
        } else {
            outState.putString("name", "");
        }

        super.onSaveInstanceState(outState);


    }

    public void onRestoreInstanceState(Bundle outState) { // called when the activity is recreated after being destroyed by the system
        super.onRestoreInstanceState(outState);          // called after onStart

        if (nameOfList.getText().toString() == null) {
            nameOfList.setText(outState.getString("name"));
        }
    }

    class ListAdapterTasks extends BaseAdapter {
        Context context;
        List<ListName> adaptList;

        // The constructor receives a context and the data
        public ListAdapterTasks(Context ctx, List<ListName> list) {
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
                rowView = inflater.inflate(R.layout.name_list, parent, false);
            }

            // These are the Views inside the ListView item
            TextView textName = (TextView) rowView.findViewById(R.id.nameofList);
            ImageButton viewTasks = (ImageButton) rowView.findViewById(R.id.idViewListTasks);
            ImageButton editListName = (ImageButton) rowView.findViewById(R.id.idEditListTasks);
            ImageButton deleteList = (ImageButton) rowView.findViewById(R.id.idDeleteListTasks);

            // obtains the contact for this position
            ListName c = adaptList.get(position);

            // sets the TextView texts
            textName.setText(c.getName());

            viewTasks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(ToDoList.this, tasksList.class).setData(Uri.parse(String.valueOf(c.getId())));
                    i.putExtra("idList", c.getId());
                    i.putExtra("listName", c.getName());
                    Toast.makeText(context, "Selecionou: " + c.getName(), Toast.LENGTH_SHORT).show();
                    startActivity(i); //envia o id da lista para depois nas tarefas receber o valor e no pedido ter o id que tem de ir buscar
                }
            });

            deleteList.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View view) {
                                                  dialLogin.startDial();
                                                  String token = getSharedPreferences("Nome de Utilizador", MODE_PRIVATE).getString("token", null);
                                                  dao.deleteList(token, c, new DAO_Interface.user_delete_list() {
                                                      @Override
                                                      public void onSuccess() {
                                                          dialLogin.finishDial();
                                                          Toast.makeText(context, "Lista apagada com sucesso", Toast.LENGTH_SHORT).show();
                                                          loadLists();
                                                          myAdapter.notifyDataSetChanged();
                                                      }

                                                      @Override
                                                      public void onError(String message) {
                                                          dialLogin.finishDial();
                                                            if (message.equals("Response code: 498")) {
                                                                refreshToken();
                                                                Toast.makeText(context, "Tente novamente", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                                            }
                                                      }
                                                  });
                                                  loadLists();
                                                  ListAdapterTasks myAdapter = (ListAdapterTasks) toDoListView.getAdapter();
                                                  myAdapter.notifyDataSetChanged();

                                              }
            }
            );

            editListName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(ToDoList.this, editListName.class);
                    i.putExtra("listName", c);
                    Toast.makeText(context, "Selecionou: " + c.getName(), Toast.LENGTH_SHORT).show();
                    startActivity(i); //envia o id da lista para depois nas tarefas receber o valor e no pedido ter o id que tem de ir buscar
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
                startActivity(new Intent(ToDoList.this, user.class));
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
                    loadLists();
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(ToDoList.this, "Não foi possivel comunicar com o servidor, ou credenciais invalidas", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }
}