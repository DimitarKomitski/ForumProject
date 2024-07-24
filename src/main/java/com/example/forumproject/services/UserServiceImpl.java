package com.example.forumproject.services;

import com.example.forumproject.exceptions.AuthorizationException;
import com.example.forumproject.exceptions.EntityDuplicateException;
import com.example.forumproject.exceptions.EntityNotFoundException;
import com.example.forumproject.models.Post;
import com.example.forumproject.models.User;
import com.example.forumproject.models.UserFilterOptions;
import com.example.forumproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> searchUsers(UserFilterOptions options) {
        return userRepository.searchUsers(options);
    }

    @Override
    public List<User> getUsers() {
        return userRepository.getUsers();
    }

    @Override
    public User getById(int id) {
        return userRepository.getById(id);
    }

    @Override
    public User getByUsername(String username) {
        return userRepository.getByUsername(username);
    }

    @Override
    public User create(User user) {
        boolean duplicateExists = true;

        try {
            userRepository.getByUsername(user.getUsername());
        } catch (EntityNotFoundException e) {
            duplicateExists = false;
        }

        if (duplicateExists) {
            throw new EntityDuplicateException("User", "username", user.getUsername());
        }

        return userRepository.create(user);
    }

    @Override
    public void delete(int id, User user) {
        User existingUser = userRepository.getById(id);
        if (!(user.isAdmin() || user.isModerator() || existingUser.equals(user))) {
            throw new AuthorizationException("Only admin or the user themselves can delete the user.");
        }

        userRepository.delete(id);
    }

    @Override
    public User update(User user, User currentUser) {
        if (currentUser.getId() != user.getId()) {
            throw new AuthorizationException("Only the user themselves can update the user.");
        }

        boolean duplicateExists = true;
        try {
            User existingUser = userRepository.getByUsername(currentUser.getUsername());
            if (existingUser.getId() == currentUser.getId()) {
                duplicateExists = false;
            }
        } catch (EntityNotFoundException e) {
            duplicateExists = false;
        }

        if (duplicateExists) {
            throw new EntityDuplicateException("User", "username", user.getUsername());
        }

        return userRepository.update(user);
    }
}
