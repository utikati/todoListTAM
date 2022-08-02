package tam3.trabalhos.jorgemartins.userObj;

import java.io.Serializable;

public class userObj implements Serializable {
    private int id;
    private String username;
    private String password;
    private String Token;

    public userObj(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public userObj() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        this.Token = token;
    }

    public String toString() {
        return "userObj{" + "id=" + id + ", username=" + username + ", password=" + password + ", token=" + Token + '}';
    }
}
