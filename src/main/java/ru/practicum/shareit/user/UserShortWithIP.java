package ru.practicum.shareit.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserShortWithIP implements UserShort {

    private String name;
    private String email;
    private String ip;

    public UserShortWithIP(UserShort user, String ip){
        this.name = user.getName();
        this.email = user.getEmail();
        this.ip = ip;
    }
}