package tam3.trabalhos.jorgemartins.db;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

import retrofit2.http.PUT;
import retrofit2.http.Path;
import tam3.trabalhos.jorgemartins.models.ListName;
import tam3.trabalhos.jorgemartins.models.taskInfo;
import tam3.trabalhos.jorgemartins.userObj.userObj;

public interface Contact_Service {

    @Headers("Accept: application/json")
    @POST("registar_utilizador")
    Call<userObj> addUser(@Body userObj user);

    @Headers("Accept: application/json")
    @POST("login")
    Call<userObj> doLogin(@Body userObj user);

    @Headers("Accept: application/json")
    @GET("listas")
    Call <List<ListName>> getAllLists(@Header("Authorization") String token);

    @Headers("Accept: application/json")
    @PUT("listas/{id}")
    Call<Void> editList(@Header("Authorization") String token, @Path("id") String id, @Body ListName list);

    @Headers({"Accept: application/json"})
    @POST("listas")
    Call<Void> addList(@Header("Authorization") String token, @Body ListName list);

    @Headers("Accept: application/json")
    @DELETE("listas/{id}")
    Call<Void> deleteList(@Header("Authorization") String token, @Path("id") String id);

//Tarefas ##############################################################################################################

    @Headers("Accept: application/json")
    @GET("listas/{id}/tarefas")
    Call<List<taskInfo>> getAllTasks(@Header("Authorization") String token, @Path("id") String id);

    @Headers("Accept: application/json")
    @POST("listas/{id}/tarefas")
    Call<Void> addTask(@Header("Authorization") String token, @Path("id") String id, @Body taskInfo task);

    @Headers("Accept: application/json")
    @PUT("listas/{id}/tarefas/{id_tarefa}")
    Call<Void> editTask(@Header("Authorization") String token, @Path("id") String id, @Path("id_tarefa") String id_tarefa, @Body taskInfo task);

    @Headers("Accept: application/json")
    @DELETE("listas/{id}/tarefas/{id_tarefa}")
    Call<Void> deleteTask(@Header("Authorization") String token, @Path("id") String id, @Path("id_tarefa") String id_tarefa);

    @Headers("Accept: application/json")
    @GET("listas/tarefas")
    Call<List<taskInfo>> getAllTasksUser(@Header("Authorization") String token); //todas as tarefas

}
