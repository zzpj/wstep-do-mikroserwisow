package com.zzpj2021.UserManagerService;

import com.zzpj2021.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepository {

    private Map<Long, User> users;

    public UserRepository() {
        users = new HashMap<>();
        users.put(10L, User.builder().id(10L).name("Alek").role("Admin").build());
        users.put(20L, User.builder().id(20L).name("Bolek").role("Mod").build());
        users.put(30L, User.builder().id(30L).name("Franek").role("User").build());
    }

    public List<User> getUsers(){
        return new ArrayList<>(users.values());
    }

    public User getUser(Long id) {
        return users.get(id);
    }
}
