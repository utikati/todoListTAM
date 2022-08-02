package tam3.trabalhos.jorgemartins.db;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import tam3.trabalhos.jorgemartins.models.ListName;
import tam3.trabalhos.jorgemartins.models.taskInfo;
import tam3.trabalhos.jorgemartins.userObj.userObj;

public class REST_DAO_CLASS implements DAO_Interface {

    public static final String BASE_URL = "https://tam-project3.herokuapp.com/";
    //public static final String BASE_URL = "http://10.0.2.2:5000/";


    Contact_Service service;

    public REST_DAO_CLASS(){
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level (others: NONE, BASIC, HEADERS)
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);



        OkHttpClient.Builder httpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder()
                        .addHeader("Connection", "close")
                        .build();
                return chain.proceed(newRequest); // foi inserido o header "connection: close" para que o servidor não seja aberto para mais conexões e não dar o unexpected end of stream error
            }
        }).addInterceptor(logging);

        // add logging as a interceptor (it should be the last one)
        //httpClient.addInterceptor(logging);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build());

        Retrofit retrofit = builder.build();

        service = retrofit.create(Contact_Service.class);

    }


    @Override
    public void register(userObj user, user_register listener) {

        Call<userObj> call = service.addUser(user);

        call.enqueue(new Callback<userObj>() {
            @Override
            public void onResponse(Call<userObj> call, Response<userObj> response) {

                switch(response.code()) {
                    case 201:
                        listener.onSuccess(user);
                        break;
                        case 400:
                            listener.onError("Utilizador já existe");
                            break;
                    default:
                        listener.onError("Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<userObj> call, Throwable t) {
                listener.onError(t.getMessage());
            }

    });
    }

    @Override
    public void login(userObj user, user_login listener) {
        Call<userObj> call = service.doLogin(user);

        call.enqueue(new Callback<userObj>() {
            @Override
            public void onResponse(Call<userObj> call, Response<userObj> response) {

                switch(response.code()) {
                    case 200:
                        userObj user = response.body();
                        if(user == null){
                            listener.onError("Utilizador nao encontrado");
                            return;
                        }
                        listener.onSuccess(user);
                        break;
                        case 400:
                            listener.onError("Não foi possivel fazer login");
                            break;
                    default:
                        listener.onError("Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<userObj> call, Throwable t) {
                listener.onError(t.getMessage());
            }

        });
    }

    @Override
    public void get_all_lists(String token, get_all_lists listener) {


        Call <List<ListName>> call = service.getAllLists(token);

        call.enqueue(new Callback<List<ListName>>() {
            @Override
            public void onResponse(Call<List<ListName>> call, Response<List<ListName>> response) {

                switch(response.code()) {
                    case 200:
                        List<ListName> lists = response.body();
                        if(lists == null){
                            listener.onError("Não existem listar para mostrar");
                            return;
                        }
                        listener.onSuccess(lists);
                        break;
                        case 400:
                            listener.onError("Não existem listas para mostrar");
                            break;
                    default:
                        listener.onError("Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call <List<ListName>> call, Throwable t) {
                listener.onError(t.getMessage());
            }

        });

    }

    @Override
    public void editList(String token, String id, ListName listName, user_edit_list listener) {
        id = Integer.toString(listName.getId());
        Call<Void> call = service.editList(token,id, listName);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                switch(response.code()) {
                    case 200:
                        listener.onSuccess();
                        break;
                        case 400:
                            listener.onError("Não foi possivel editar a lista");
                            break;
                    default:
                        listener.onError("Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                listener.onError(t.getMessage());
            }

        });

    }

    @Override
    public void addList(String token, ListName name, user_add_list listener) {

        Call<Void> call = service.addList(token, name);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                switch(response.code()) {
                    case 201:
                        listener.onSuccess();
                        break;
                        case 400:
                            listener.onError("Não foi possivel adicionar a lista");
                            break;
                    default:
                        listener.onError("Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                listener.onError(t.getMessage());
            }

        });

    }

    @Override
    public void deleteList(String token, ListName listName, user_delete_list listener) {

        String id = Integer.toString(listName.getId());
        Call<Void> call = service.deleteList(token, id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                switch(response.code()) {
                    case 200:
                        listener.onSuccess();
                        break;
                        case 400:
                            listener.onError("Não foi possivel apagar a lista");
                            break;
                    default:
                        listener.onError("Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                listener.onError(t.getMessage());
            }

        });


    }
    // Tasks ################################################################################################################
    @Override
    public void get_all_tasks(String token, String id, get_all_tasks listener) {
        Call<List<taskInfo>> call = service.getAllTasks(token, id);

        call.enqueue(new Callback<List<taskInfo>>() {
            @Override
            public void onResponse(Call<List<taskInfo>> call, Response<List<taskInfo>> response) {

                switch(response.code()) {
                    case 200:
                        List<taskInfo> tasks = response.body();
                        if(tasks == null){
                            listener.onError("Não existem tarefas para listar");
                            return;
                        }
                        listener.onSuccess(tasks);
                        break;
                        case 400:
                            listener.onError("Não existem tarefas para listar");
                            break;
                    default:
                        listener.onError("Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<taskInfo>> call, Throwable t) {
                listener.onError(t.getMessage());
            }

        });

    }

    @Override
    public void addTask(String token, taskInfo task, user_add_tasks listener) {
        String id = Integer.toString(task.getIdList());
        Call<Void> call = service.addTask(token, id, task);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                switch(response.code()) {
                    case 201:
                        listener.onSuccess();
                        break;
                        case 400:
                            listener.onError("Não foi possivel adicionar a tarefa");
                            break;
                    default:
                        listener.onError("Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                listener.onError(t.getMessage());
            }

        });


    }

    @Override
    public void editTask(String token, taskInfo task, user_edit_tasks listener) {

        String id = Integer.toString(task.getIdList());
        String idTask = Integer.toString(task.getIdTask());
        Call<Void> call = service.editTask(token, id, idTask, task);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                switch(response.code()) {
                    case 200:
                        listener.onSuccess();
                        break;
                        case 400:
                            listener.onError("Não foi possivel editar a tarefa");
                            break;
                    default:
                        listener.onError("Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                listener.onError(t.getMessage());
            }

        });


    }

    @Override
    public void deleteTheTask(String token, taskInfo taskInfo, user_delete_tasks listener) {

        String id = Integer.toString(taskInfo.getIdList());
        String idTask = Integer.toString(taskInfo.getIdTask());

        Call<Void> call = service.deleteTask(token, id, idTask);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                switch(response.code()) {
                    case 200:
                        listener.onSuccess();
                        break;
                        case 400:
                            listener.onError("Não foi possivel apagar a tarefa");
                            break;
                    default:
                        listener.onError("Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                listener.onError(t.getMessage());
            }

        });
    }

    @Override
    public void get_all_tasks_user(String token, get_all_tasks_user listener) {

        Call<List<taskInfo>> call = service.getAllTasksUser(token);

        call.enqueue(new Callback<List<taskInfo>>() {
            @Override
            public void onResponse(Call<List<taskInfo>> call, Response<List<taskInfo>> response) {

                switch(response.code()) {
                    case 200:
                        List<taskInfo> tasks = response.body();
                        if(tasks == null){
                            listener.onError("Não existem tarefas para listar");
                            return;
                        }
                        listener.onSuccess(tasks);
                        break;
                        case 400:
                            listener.onError("Não existem tarefas para listar");
                            break;
                    default:
                        listener.onError("Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<taskInfo>> call, Throwable t) {
                listener.onError(t.getMessage());
            }

        });


    }


}
