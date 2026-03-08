package com.example.usermanagement.user;

import java.util.List;

public interface UserService {

    User createUser(String username, String email, String password);

    User getUserById(Long id);

    List<User> getAllUsers();

    User updateUser(Long id, String username, String email);

    void deleteUser(Long id);
}
