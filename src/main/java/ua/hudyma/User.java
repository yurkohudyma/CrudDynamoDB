package ua.hudyma;

import com.google.gson.Gson;
import lombok.Data;

@Data
public class User {

    private int id;
    private String name;
    private String email;

    public User (String json) {
        Gson gson = new Gson();
        User user = gson.fromJson(json, User.class);
        this.id = user.id;
        this.name = user.name;
        this.email = user.email;
    }

    public String toString (){
        return new Gson().toJson(this);
    }
}
