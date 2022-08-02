package tam3.trabalhos.jorgemartins.db;

import java.util.List;

import tam3.trabalhos.jorgemartins.models.ListName;
import tam3.trabalhos.jorgemartins.models.taskInfo;
import tam3.trabalhos.jorgemartins.userObj.userObj;

public interface DAO_Interface {

    public interface user_register {
        public void onSuccess(userObj user);
        public void onError(String message);
    }
    public void register(userObj user, user_register listener);

    public interface user_login {
        public void onSuccess(userObj user);
        public void onError(String message);
    }
    public void login(userObj user, user_login listener);

// LISTAS ##############################################################################################################
    //obter todas as listas
    public interface get_all_lists {
        public void onSuccess(List listNames);
        public void onError(String message);
    }
    public void get_all_lists(String token, get_all_lists listener);

    //editar lista
    public interface user_edit_list {
        public void onSuccess();
        public void onError(String message);
    }
    public void editList(String token, String id, ListName listName, user_edit_list listener);

    //adicionar lista
    public interface user_add_list {
        public void onSuccess();
        public void onError(String message);
    }
    public void addList(String token, ListName name, user_add_list listener);

    //remover lista
    public interface user_delete_list {
        public void onSuccess();
        public void onError(String message);
    }
    public void deleteList(String token, ListName listName, user_delete_list listener);

// Tarefas ##############################################################################################################
    //obter todos as tarefas de uma lista
    public interface get_all_tasks {
        public void onSuccess(List<taskInfo> tasks);
        public void onError(String message);
    }
    public void get_all_tasks(String token, String id, get_all_tasks listener); //id da lista

    //adicionar tarefa
    public interface user_add_tasks {
        public void onSuccess();
        public void onError(String message);
    }
    public void addTask(String token, taskInfo task, user_add_tasks listener);

    //editar tarefa
    public interface user_edit_tasks {
        public void onSuccess();
        public void onError(String message);
    }
    public void editTask(String token, taskInfo task, user_edit_tasks listener);

    //remover tarefa
    public interface user_delete_tasks {
        public void onSuccess();
        public void onError(String message);
    }
    public void deleteTheTask(String token, taskInfo taskInfo, user_delete_tasks listener);

    //obter todas as tarefas de utilizador
    public interface get_all_tasks_user {
        public void onSuccess(List<taskInfo> taskInfo);
        public void onError(String message);
    }
    public void get_all_tasks_user(String token, get_all_tasks_user listener);

}
