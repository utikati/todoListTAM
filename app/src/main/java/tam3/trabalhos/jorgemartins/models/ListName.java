package tam3.trabalhos.jorgemartins.models;

import java.io.Serializable;

public class ListName implements Serializable {

    private int id_list;
    private String list_name;
    private int user_id_user;

    public ListName (int id, String name){
        this.id_list = id;
        this.list_name = name;
    }



    public ListName(){

    }

    public int getId() {
        return id_list;
    }

    public void setId(int id) {
        this.id_list = id;
    }

    public String getName() {
        return list_name;
    }

    public void setName(String name) {
        this.list_name = name;
    }

    public int getUserId() {
        return user_id_user;
    }

    public void setUserId(int userId) {
        this.user_id_user = userId;
    }


    @Override
    public String toString() {
        return "Lista -> " + list_name ;
    }
}
